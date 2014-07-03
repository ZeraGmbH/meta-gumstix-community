DESCRIPTION = "libGLES for the omap3"
LICENSE = "proprietary-binary"
# 'TSPA.txt' might not be the best file to md5sum
LIC_FILES_CHKSUM = "file://TSPA.txt;md5=c0d5d9c1e38b41677144c4e24d6ddee1"

EXCLUDE_FROM_WORLD = "1"

COMPATIBLE_MACHINE = "(omap3|ti814x|ti816x|ti33x|ti43x)"

PVR_INIT ?= "pvrsrvctl"

PROVIDES = "virtual/egl virtual/libgles1 virtual/libgles2 virtual/mesa"
PROVIDES += "${@base_contains('DISTRO_FEATURES', 'wayland', 'virtual/libgl', '', d)}"

LICENSE = "TSPA"

# Logic to unpack installjammer file
TI_BIN_UNPK_CMDS="Y: qY:workdir:Y"
require ti-eula-unpack.inc

# GLESTYPE gfx_rel / gfx_dbg (debug crashed here..)
GLESTYPE = "gfx_rel"

BINLOCATION_omap3 = "${S}/gfx_rel_es5.x"
BINLOCATION_ti816x = "${S}/gfx_rel_es6.x"
BINLOCATION_ti814x = "${S}/gfx_rel_es6.x"
BINLOCATION_ti33x = "${S}/gfx_rel_es8.x"
BINLOCATION_ti43x = "${S}/gfx_rel_es9.x"

PLATFORM = "LinuxARMV7"
PVR_INIT = "pvrsrvctl"

SGXPV = "4_09_00_01"
IMGPV = "1.9.2188537"
BINFILE = "Graphics_SDK_setuplinux_${SGXPV}_hardfp_minimal_demos.bin"

TI_BIN_UNPK_WDEXT := "/Graphics_SDK_${SGXPV}"

# we provide virtual/mesa but no gbm
DEPENDS += "libgbm"
RDEPENDS_${PN} = "ti-sdk-pvr libgbm fbset devmem2"

# For now we only have hardfp version
python __anonymous() {
    tunes = bb.data.getVar("TUNE_FEATURES", d, 1)
    if not tunes:
        return
    pkgn = bb.data.getVar("PN", d, 1)
    pkgv = bb.data.getVar("PV", d, 1)
    if "callconvention-hard" not in tunes:
        bb.warn("%s-%s ONLY supports hardfp mode for now" % (pkgn, pkgv))
        raise bb.parse.SkipPackage("%s-%s ONLY supports hardfp mode for now" % (pkgn, pkgv))
}

SRC_URI = " \
	http://software-dl.ti.com/dsps/dsps_public_sw/gfxsdk/${SGXPV}/exports/${BINFILE};name=for-omap3 \
	git://git.ti.com/graphics/omap5-sgx-ddk-um-linux.git;protocol=git;name=for-omap5 \
	file://cputype \
	file://rc.pvr \
	file://glesv2.pc.in \
	file://pvr2d.pc.in \
	file://0001-eglext.h-add-EGL_WAYLAND_BUFFER_WL-to-prepare-ti-oma.patch \
	file://0002-eglplatform.h-add-missing-entries-for-wayland-and-gb.patch \
"

PKGCONFIGS-SHIPPED = "glesv2 pvr2d"

SRC_URI[for-omap3.md5sum] := "c9f656dce062d1ab10afffd4dfb71b67"
SRC_URI[for-omap3.sha256sum] := "dbfeba8e1298f139495816334edec1455e6b49b1e11bd1b2aa0a888e5788bb6b"

S = "${WORKDIR}/Graphics_SDK_${SGXPV}"

SRCREV_for-omap5 = "994922a5655c63b05343a9238cb30e6ef61744be"


do_configure() {
	# Attempt to fix up the worst offenders for file permissions
	for i in $(find ${S} -name "*.h") $(find ${S} -name "*.c") $(find ${S} -name "Make*") ; do
		chmod 0644 $i
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
	##############
	# omap3

	BINLOCATION="${S}/${GLESTYPE}"
	ES3_0_LOCATION="${S}/${GLESTYPE}_es3.x"
	ES5_0_LOCATION="${S}/${GLESTYPE}_es5.x"
	ES6_0_LOCATION="${S}/${GLESTYPE}_es6.x"
	ES8_0_LOCATION="${S}/${GLESTYPE}_es8.x"
	ES9_0_LOCATION="${S}/${GLESTYPE}_es9.x"

	install -d ${D}${libdir}
	cp -pPR ${BINLOCATION}/*.so* ${D}${libdir}

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
	echo "WindowSystem=libpvrws_WAYLAND.so" >> ${D}${sysconfdir}/powervr.ini

	shared_prog="eglinfo pvr2d_test ${PVR_INIT} services_test sgx_blit_test sgx_clipblit_test sgx_flip_test sgx_init_test sgx_render_flip_test"
	raw_prog="gles1test1 gles2test1"

	for esrev in '3.0' '5.0' '6.0' '8.0' '9.0' ; do
		
		TARGET=$(echo $esrev | sed $'s:\.:_:')
		ESLOCATION=$(eval echo $(echo \$\{ES${TARGET}_LOCATION\}))
		if [ -e ${ESLOCATION} ] ; then
			# Create different folders for ease of installing into different platforms with different display drivers/ SGX core
			install -d ${D}${libdir}/ES${esrev}
			install -d ${D}${bindir}/ES${esrev}
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

	# additional pkgconfigs
	install -d ${D}${libdir}/pkgconfig
	for pkgconfig_name in ${PKGCONFIGS-SHIPPED} ; do
		sed \
			-e 's:%PREFIX%:${prefix}:g' \
			-e 's:%LIBDIR%:${libdir}:g' \
			-e 's:%INCDIR%:${includedir}:g' \
			-e 's:%VERSION%:${IMGPV}:g' \
			< "${WORKDIR}/${pkgconfig_name}.pc.in" > "${D}/${libdir}/pkgconfig/${pkgconfig_name}.pc"
	done

	##############
	# omap5

	# remove libsrv_um.so / wsegl->libIMGegl.so is shipped by omap5
	for sofile in $(find ${D} -name 'libsrv_um*.so*') $(find ${D} -name 'libpvrPVR2D_*WSEGL.so*') $(find ${D} -name 'libIMGegl.so*'); do
		rm -f $sofile
	done


	install -m 0644 ${WORKDIR}/git/targetfs/lib/pkgconfig/wayland-egl.pc ${D}${libdir}/pkgconfig
	install -m 0644 ${WORKDIR}/git/targetfs/lib/pkgconfig/egl.pc ${D}${libdir}/pkgconfig
	# keep links
	cp -pP ${WORKDIR}/git/targetfs/lib/libsrv_um.so* ${D}${libdir}
	cp -pP ${WORKDIR}/git/targetfs/lib/libpvr_wlegl.so* ${D}${libdir}
	cp -pP ${WORKDIR}/git/targetfs/lib/libpvrws_WAYLAND.so* ${D}${libdir}
	cp -pP ${WORKDIR}/git/targetfs/lib/libIMGegl.so* ${D}${libdir}
	install -d ${D}${libdir}/gbm
	cp -pP ${WORKDIR}/git/targetfs/lib/gbm/gbm_pvr.so* ${D}${libdir}/gbm
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

CONFFILES_${PN} = "${sysconfdir}/powervr.ini"

FILES_${PN} = " \
	${sysconfdir} \
	${libdir}/*.so* \
	${libdir}/gbm \
	${bindir}/${PVR_INIT} \
	${bindir}/cputype \
	${bindir}/gl* \
	${bindir}/pv* \
	${bindir}/s* \
"

FILES_${PN}-tests = "${bindir}/*test*"
FILES_${PN}-dbg = " \
	${libdir}/.debug/* \
	${libdir}/ES*/.debug \
	${libdir}/gbm/.debug \
	${bindir}/.debug/* \
	${bindir}/*/.debug\
"

#HACK! These are binaries, so we can't guarantee that LDFLAGS match :(
INSANE_SKIP_${PN} = "ldflags dev-so already-stripped useless-rpaths"
INSANE_SKIP_${PN}-es3 = "ldflags dev-so"
INSANE_SKIP_${PN}-es5 = "ldflags dev-so"
INSANE_SKIP_${PN}-es6 = "ldflags dev-so"
INSANE_SKIP_${PN}-es8 = "ldflags dev-so"
INSANE_SKIP_${PN}-es9 = "ldflags dev-so"
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
