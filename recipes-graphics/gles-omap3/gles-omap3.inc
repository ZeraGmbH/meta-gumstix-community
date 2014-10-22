DESCRIPTION = "libGLES for the omap3"
LICENSE = "proprietary-binary"
# 'TSPA.txt' might not be the best file to md5sum
LIC_FILES_CHKSUM = "file://TSPA.txt;md5=c0d5d9c1e38b41677144c4e24d6ddee1"

EXCLUDE_FROM_WORLD = "1"

COMPATIBLE_MACHINE = "(omap3|ti814x|ti816x|ti33x|ti43x)"

PVR_INIT ?= "pvrsrvctl"

PROVIDES += "virtual/libgles1 virtual/libgles2"
PROVIDES += "${@base_contains('DISTRO_FEATURES', 'x11', 'virtual/egl', '', d)}"
RREPLACES_${PN} = "libgles1 libgles2"
RREPLACES_${PN} += "${@base_contains('DISTRO_FEATURES', 'x11', 'libegl', '', d)}"

S = "${WORKDIR}/OMAP35x_Graphics_SDK_${SGXPV}"

# Logic to unpack installjammer file
TI_BIN_UNPK_CMDS="Y: qY:workdir:Y"
require ti-eula-unpack.inc

# GLESTYPE gfx_rel / gfx_dbg (debug crashed here..)
GLESTYPE = "gfx_rel"

do_configure() {
	# Attempt to fix up the worst offenders for file permissions
	for i in $(find ${S} -name "*.h") $(find ${S} -name "*.c") $(find ${S} -name "Make*") ; do
		chmod 0644 $i
	done

	if ${@base_contains('DISTRO_FEATURES', 'wayland', 'true', 'false', d)}; then
		# rename EGL libraries so that ti-omap-sgx-wayland-wsegl can find and load it
		for egllib in $(find ${S} -name 'libEGL.*') ; do
			mv $egllib $(echo $egllib | sed 's:libEGL.:libEGL-sgx.:')
		done
	fi
	# Attempt to create proper library softlinks
	for sofile in $(find ${S} -name "lib*Open*.so") $(find ${S} -name "lib*srv*.so") $(find ${S} -name "lib*gl*.so") $(find ${S} -name "libpvr*.so") $(find ${S} -name "lib*GL*.so"); do
		if [ "$(readlink -n ${sofile})" = "" ] ; then
			mv $sofile ${sofile}.${IMGPV}
			ln -sf $(basename ${sofile}.${IMGPV}) ${sofile}
			ln -sf $(basename ${sofile}.${IMGPV}) ${sofile}$(echo ${IMGPV} | awk -F. '{print "." $1}')
			ln -sf $(basename ${sofile}.${IMGPV}) ${sofile}$(echo ${IMGPV} | awk -F. '{print "." $1 "." $2}')
		fi
	done

	# Due to recursive make PLAT_* isn't always passed down correctly, so use sed to fix those
	for mak in $(find ${S} -name "*.mak") ; do
		sed -i -e s:arm-none-linux-gnueabi-:${TARGET_PREFIX}:g $mak
	done

	# clear out old stuff
	find Binaries/ | xargs rm -f || true
}

do_compile() {
}

do_install () {
	BINLOCATION="${S}/${GLESTYPE}"
	ES3_0_LOCATION="${S}/${GLESTYPE}_es3.x"
	ES5_0_LOCATION="${S}/${GLESTYPE}_es5.x"
	ES6_0_LOCATION="${S}/${GLESTYPE}_es6.x"
	ES8_0_LOCATION="${S}/${GLESTYPE}_es8.x"
	ES9_0_LOCATION="${S}/${GLESTYPE}_es9.x"

	install -d ${D}${libdir}
	cp -pPR ${BINLOCATION}/*.so* ${D}${libdir}
	rm ${D}${libdir}/libpvrPVR2D_DRIWSEGL.so*
	rm ${D}${libdir}/libsrv_um_dri.so*
	rm ${D}${libdir}/pvr_drv.so* 

	install -m 0644 ${BINLOCATION}/*.a ${D}${libdir}

	install -d ${D}${bindir}/
	install -m 0755 ${WORKDIR}/cputype ${D}${bindir}/

	install -m 0755 ${BINLOCATION}/*_test ${D}${bindir}/
	install -m 0755 ${BINLOCATION}/gl* ${D}${bindir}/
	install -m 0755 ${BINLOCATION}/${PVR_INIT} ${D}${bindir}/

	install -d ${D}${includedir}
	cp -pPR ${S}/GFX_Linux_KM/include4 ${D}${includedir}/
	cp -pPR ${S}/GFX_Linux_KM/services4 ${D}${includedir}/

	cp -pPr ${S}/include/pvr2d/*.h ${D}${includedir}
	cp -pPr ${S}/include/OGLES2/* ${D}${includedir}/
	cp -pPr ${S}/include/wsegl/*.h ${D}${includedir}/
	
	install -d ${D}${sysconfdir}/init.d/
	cp -pP ${WORKDIR}/rc.pvr ${D}${sysconfdir}/init.d/pvr-init
	chmod +x ${D}${sysconfdir}/init.d/pvr-init

	install -d ${D}${sysconfdir}
	echo "[default]" > ${D}${sysconfdir}/powervr.ini
	echo "WindowSystem=waylandwsegl.so" >> ${D}${sysconfdir}/powervr.ini

	shared_prog="eglinfo pvr2d_test ${PVR_INIT} services_test sgx_blit_test sgx_clipblit_test sgx_flip_test sgx_init_test sgx_render_flip_test"
	raw_prog="gles1test1 gles2test1"

	for esrev in '3.0' '5.0' '6.0' '8.0' '9.0' ; do
		# Create different folders for ease of installing into different platforms with different display drivers/ SGX core
		install -d ${D}${libdir}/ES${esrev}
		install -d ${D}${bindir}/ES${esrev}
		
		TARGET=$(echo $esrev | sed $'s:\.:_:')
		ESLOCATION=$(eval echo $(echo \$\{ES${TARGET}_LOCATION\}))
		if [ -e ${ESLOCATION} ] ; then
			cp -pPR ${ESLOCATION}/lib*${IMGPV} ${ESLOCATION}/pvr_drv.so ${ESLOCATION}/*.a ${D}${libdir}/ES${esrev}/
			for esprog in $shared_prog $raw_prog ; do
				install -m 0755 ${ESLOCATION}/$esprog ${D}${bindir}/ES${esrev}/ 2>/dev/null || true
			done
		fi
	done

	rm ${D}${bindir}/ES*/*.h ${D}${bindir}/ES*/pdsasm ${D}${bindir}/pdsasm -f || true

	# Delete objects and linker scripts hidden between the headers
	find ${D} -name "*.o" -delete
	find ${D} -name "*.o.cmd" -delete

	install -d ${D}${sysconfdir}/udev/rules.d
	install -m 0644 ${WORKDIR}/99-bufferclass.rules ${D}${sysconfdir}/udev/rules.d/

	# pkgconfig
	install -d ${D}${libdir}/pkgconfig
	for pkgconfig_name in ${PKGCONFIGS-SHIPPED} ; do
		sed \
			-e 's:%PREFIX%:${prefix}:g' \
			-e 's:%LIBDIR%:${libdir}:g' \
			-e 's:%INCDIR%:${includedir}:g' \
			-e 's:%VERSION%:${IMGPV}:g' \
			< "${WORKDIR}/${pkgconfig_name}.pc.in" > "${D}/${libdir}/pkgconfig/${pkgconfig_name}.pc"
	done

}

RPROVIDES_${PN} += "libGLESv2.so libEGL.so libGLES_CM.so libpvr2d.so libIMGegl.so libsrv_init.so libsrv_um.so libsrv_um_dri.so libglslcompiler.so"

CONFFILES_${PN} = "${sysconfdir}/powervr.ini"

FILES_${PN} = " \
	${sysconfdir} \
	${libdir}/*.so* \
	${libdir}/ES* \
	${bindir}/${PVR_INIT} \
	${bindir}/cputype \
	${bindir}/gl* \
	${bindir}/pv* \
	${bindir}/s* \
	${bindir}/ES* \
"

FILES_${PN}-tests = "${bindir}/*test*"
FILES_${PN}-dbg = "${libdir}/.debug/* ${bindir}/.debug/* \
                   ${libdir}/ES*/.debug ${bindir}/*/.debug\
"

RDEPENDS_${PN} = "ti-sdk-pvr ${@base_contains('DISTRO_FEATURES', 'wayland', 'ti-omap3-sgx-wayland-wsegl', '', d)} fbset devmem2"

#HACK! These are binaries, so we can't guarantee that LDFLAGS match :(
INSANE_SKIP_${PN} = "ldflags dev-so already-stripped"
INSANE_SKIP_${PN}-tests = "ldflags"

# Quality control is really poor on these SDKs, so hack around the latest madness:
FILES_${PN} += "${libdir}/*.so "
FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig"
FILES_${PN}-staticdev += " \
	${libdir}/*.a \
	${libdir}/*/*.a \
"

inherit update-rc.d

INITSCRIPT_NAME = "pvr-init"
INITSCRIPT_PARAMS = "start 30 5 2 . stop 40 0 1 6 ."

# Append to update-rc.d postinst
pkg_postinst_${PN}_append() {
rm -f $D${sysconfdir}/powervr-esrev

}
