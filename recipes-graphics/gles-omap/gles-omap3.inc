DESCRIPTION = "libGLES for the omap3"
LICENSE = "proprietary-binary"
# 'TSPA.txt' might not be the best file to md5sum
LIC_FILES_CHKSUM = "file://TSPA.txt;md5=c0d5d9c1e38b41677144c4e24d6ddee1"

EXCLUDE_FROM_WORLD = "1"

COMPATIBLE_MACHINE = "(omap3|ti814x|ti816x|ti33x|ti43x)"

PVR_INIT ?= "pvrsrvctl"

PROVIDES += "virtual/libgles1 virtual/libgles2"
PROVIDES += "${@base_contains('DISTRO_FEATURES', 'wayland', 'virtual/libgl', '', d)}"

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

	# we are interested in libsrv_um_dri.so
	for sofile in $(find ${S} -name libsrv_um_dri.so); do
		rm -f $(dirname $sofile)/libsrv_um.so
		mv $sofile $(dirname $sofile)/libsrv_um.so
	done

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
	#rm ${D}${libdir}/libsrv_um_dri.so*
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

# Package the base libraries per silicon revision
PACKAGES += "${PN}-es3 ${PN}-es5 ${PN}-es6 ${PN}-es8 ${PN}-es9"
RRECOMMENDS_${PN} += "${PN}-es3 ${PN}-es5 ${PN}-es6 ${PN}-es8 ${PN}-es9"
FILES_${PN}-es3 = "${libdir}/ES3*/* ${bindir}/ES3*/*"
FILES_${PN}-es5 = "${libdir}/ES5*/* ${bindir}/ES5*/*"
FILES_${PN}-es6 = "${libdir}/ES6*/* ${bindir}/ES6*/*"
FILES_${PN}-es8 = "${libdir}/ES8*/* ${bindir}/ES8*/*"
FILES_${PN}-es9 = "${libdir}/ES9*/* ${bindir}/ES9*/*"

# Stop shlib code from picking a subpackage
PRIVATE_LIBS_${PN}-es3 = "libGLESv2.so libIMGegl.so libsrv_um.so libpvr2d.so libsrv_init.so libEGL.so libsrv_um_dri.so libglslcompiler.so libGLES_CM.so"
PRIVATE_LIBS_${PN}-es5 = "libGLESv2.so libIMGegl.so libsrv_um.so libpvr2d.so libsrv_init.so libEGL.so libsrv_um_dri.so libglslcompiler.so libGLES_CM.so"
PRIVATE_LIBS_${PN}-es6 = "libGLESv2.so libIMGegl.so libsrv_um.so libpvr2d.so libsrv_init.so libEGL.so libsrv_um_dri.so libglslcompiler.so libGLES_CM.so"
PRIVATE_LIBS_${PN}-es8 = "libGLESv2.so libIMGegl.so libsrv_um.so libpvr2d.so libsrv_init.so libEGL.so libsrv_um_dri.so libglslcompiler.so libGLES_CM.so"
PRIVATE_LIBS_${PN}-es9 = "libGLESv2.so libIMGegl.so libsrv_um.so libpvr2d.so libsrv_init.so libEGL.so libsrv_um_dri.so libglslcompiler.so libGLES_CM.so"


RPROVIDES_${PN} += "libGLESv2.so libEGL.so libGLES_CM.so libpvr2d.so libIMGegl.so libsrv_init.so libsrv_um.so libsrv_um_dri.so libglslcompiler.so"

PACKAGES =+ "${PN}-blitwsegl ${PN}-flipwsegl ${PN}-frontwsegl ${PN}-linuxfbwsegl"
FILES_${PN}-blitwsegl = "${libdir}/libpvrPVR2D_BLITWSEGL.so.*"
FILES_${PN}-flipwsegl = "${libdir}/libpvrPVR2D_FLIPWSEGL.so.*"
FILES_${PN}-frontwsegl = "${libdir}/libpvrPVR2D_FRONTWSEGL.so.*"
FILES_${PN}-linuxfbwsegl = "${libdir}/libpvrPVR2D_LINUXFBWSEGL.so.*"

PACKAGES =+ "${PN}-blitwsegl-es3 ${PN}-blitwsegl-es5 ${PN}-blitwsegl-es6 ${PN}-blitwsegl-es8 ${PN}-blitwsegl-es9 ${PN}-flipwsegl-es3 ${PN}-flipwsegl-es5 ${PN}-flipwsegl-es6 ${PN}-flipwsegl-es8 ${PN}-flipwsegl-es9 ${PN}-frontwsegl-es3 ${PN}-frontwsegl-es5 ${PN}-frontwsegl-es6 ${PN}-frontwsegl-es8 ${PN}-frontwsegl-es9 ${PN}-linuxfbwsegl-es3 ${PN}-linuxfbwsegl-es5 ${PN}-linuxfbwsegl-es6 ${PN}-linuxfbwsegl-es8 ${PN}-linuxfbwsegl-es9"

FILES_${PN}-blitwsegl-es3 = "${libdir}/ES3*/libpvrPVR2D_BLITWSEGL.so.*"
FILES_${PN}-blitwsegl-es5 = "${libdir}/ES5*/libpvrPVR2D_BLITWSEGL.so.*"
FILES_${PN}-blitwsegl-es6 = "${libdir}/ES6*/libpvrPVR2D_BLITWSEGL.so.*"
FILES_${PN}-blitwsegl-es8 = "${libdir}/ES8*/libpvrPVR2D_BLITWSEGL.so.*"
FILES_${PN}-blitwsegl-es9 = "${libdir}/ES9*/libpvrPVR2D_BLITWSEGL.so.*"
RRECOMMENDS_${PN}-blitwsegl = " ${PN}-blitwsegl-es3 ${PN}-blitwsegl-es5 ${PN}-blitwsegl-es6 ${PN}-blitwsegl-es8 ${PN}-blitwsegl-es9"

FILES_${PN}-flipwsegl-es3 = "${libdir}/ES3*/libpvrPVR2D_FLIPWSEGL.so.*"
FILES_${PN}-flipwsegl-es5 = "${libdir}/ES5*/libpvrPVR2D_FLIPWSEGL.so.*"
FILES_${PN}-flipwsegl-es6 = "${libdir}/ES6*/libpvrPVR2D_FLIPWSEGL.so.*"
FILES_${PN}-flipwsegl-es8 = "${libdir}/ES8*/libpvrPVR2D_FLIPWSEGL.so.*"
FILES_${PN}-flipwsegl-es9 = "${libdir}/ES9*/libpvrPVR2D_FLIPWSEGL.so.*"
RRECOMMENDS_${PN}-flipwsegl = " ${PN}-flipwsegl-es3 ${PN}-flipwsegl-es5 ${PN}-flipwsegl-es6 ${PN}-flipwsegl-es8 ${PN}-flipwsegl-es9"

FILES_${PN}-frontwsegl-es3 = "${libdir}/ES3*/libpvrPVR2D_FRONTWSEGL.so.*"
FILES_${PN}-frontwsegl-es5 = "${libdir}/ES5*/libpvrPVR2D_FRONTWSEGL.so.*"
FILES_${PN}-frontwsegl-es6 = "${libdir}/ES6*/libpvrPVR2D_FRONTWSEGL.so.*"
FILES_${PN}-frontwsegl-es8 = "${libdir}/ES8*/libpvrPVR2D_FRONTWSEGL.so.*"
FILES_${PN}-frontwsegl-es9 = "${libdir}/ES9*/libpvrPVR2D_FRONTWSEGL.so.*"
RRECOMMENDS_${PN}-frontwsegl = " ${PN}-frontwsegl-es3 ${PN}-frontwsegl-es5 ${PN}-frontwsegl-es6 ${PN}-frontwsegl-es8 ${PN}-frontwsegl-es9"

FILES_${PN}-linuxfbwsegl-es3 = "${libdir}/ES3*/libpvrPVR2D_LINUXFBWSEGL.so.*"
FILES_${PN}-linuxfbwsegl-es5 = "${libdir}/ES5*/libpvrPVR2D_LINUXFBWSEGL.so.*"
FILES_${PN}-linuxfbwsegl-es6 = "${libdir}/ES6*/libpvrPVR2D_LINUXFBWSEGL.so.*"
FILES_${PN}-linuxfbwsegl-es8 = "${libdir}/ES8*/libpvrPVR2D_LINUXFBWSEGL.so.*"
FILES_${PN}-linuxfbwsegl-es9 = "${libdir}/ES9*/libpvrPVR2D_LINUXFBWSEGL.so.*"
RRECOMMENDS_${PN}-linuxfbwsegl = " ${PN}-linuxfbwsegl-es3 ${PN}-linuxfbwsegl-es5 ${PN}-linuxfbwsegl-es6 ${PN}-linuxfbwsegl-es8 ${PN}-linuxfbwsegl-es9"

CONFFILES_${PN} = "${sysconfdir}/powervr.ini"

FILES_${PN} = " \
	${sysconfdir} \
	${libdir}/*.so* \
	${bindir}/${PVR_INIT} \
	${bindir}/cputype \
	${bindir}/gl* \
	${bindir}/pv* \
	${bindir}/s* \
"

FILES_${PN}-tests = "${bindir}/*test*"
FILES_${PN}-dbg = "${libdir}/.debug/* ${bindir}/.debug/* \
                   ${libdir}/ES*/.debug ${bindir}/*/.debug\
"

RDEPENDS_${PN} = "ti-sdk-pvr ${@base_contains('DISTRO_FEATURES', 'wayland', 'ti-omap3-sgx-wayland-wsegl', '', d)} fbset devmem2"

#HACK! These are binaries, so we can't guarantee that LDFLAGS match :(
INSANE_SKIP_${PN} = "ldflags dev-so already-stripped"
INSANE_SKIP_${PN}-es3 = "ldflags dev-so"
INSANE_SKIP_${PN}-es5 = "ldflags dev-so"
INSANE_SKIP_${PN}-es6 = "ldflags dev-so"
INSANE_SKIP_${PN}-es8 = "ldflags dev-so"
INSANE_SKIP_${PN}-es9 = "ldflags dev-so"
INSANE_SKIP_${PN}-blitwsegl = "ldflags"
INSANE_SKIP_${PN}-blitwsegl-es3 = "ldflags"
INSANE_SKIP_${PN}-blitwsegl-es5 = "ldflags"
INSANE_SKIP_${PN}-blitwsegl-es6 = "ldflags"
INSANE_SKIP_${PN}-blitwsegl-es8 = "ldflags"
INSANE_SKIP_${PN}-blitwsegl-es9 = "ldflags"
INSANE_SKIP_${PN}-flipwsegl = "ldflags"
INSANE_SKIP_${PN}-flipwsegl-es3 = "ldflags"
INSANE_SKIP_${PN}-flipwsegl-es5 = "ldflags"
INSANE_SKIP_${PN}-flipwsegl-es6 = "ldflags"
INSANE_SKIP_${PN}-flipwsegl-es8 = "ldflags"
INSANE_SKIP_${PN}-flipwsegl-es9 = "ldflags"
INSANE_SKIP_${PN}-frontwsegl = "ldflags"
INSANE_SKIP_${PN}-frontwsegl-es3 = "ldflags"
INSANE_SKIP_${PN}-frontwsegl-es5 = "ldflags"
INSANE_SKIP_${PN}-frontwsegl-es6 = "ldflags"
INSANE_SKIP_${PN}-frontwsegl-es8 = "ldflags"
INSANE_SKIP_${PN}-frontwsegl-es9 = "ldflags"
INSANE_SKIP_${PN}-linuxfbwsegl = "ldflags"
INSANE_SKIP_${PN}-linuxfbwsegl-es3 = "ldflags"
INSANE_SKIP_${PN}-linuxfbwsegl-es5 = "ldflags"
INSANE_SKIP_${PN}-linuxfbwsegl-es6 = "ldflags"
INSANE_SKIP_${PN}-linuxfbwsegl-es8 = "ldflags"
INSANE_SKIP_${PN}-linuxfbwsegl-es9 = "ldflags"
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

pkg_postinst_${PN}-blitwsegl() {
rm -f $D${sysconfdir}/powervr-esrev
}

pkg_postinst_${PN}-flipwsegl() {
rm -f $D${sysconfdir}/powervr-esrev
}

pkg_postinst_${PN}-frontwsegl() {
rm -f $D${sysconfdir}/powervr-esrev
}
pkg_postinst_${PN}-linuxfbwsegl() {
rm -f $D${sysconfdir}/powervr-esrev
}
