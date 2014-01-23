DESCRIPTION = "Wayland WSEGL plugin for SGX drivers"
LICENSE = "LGPLv2.1 & proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86515e83f3ce1048848e1122b9b1ef9c \
                    file://LICENSE.LGPL;md5=24a4036de5c39ff01ad4986c4870d8c0"

SRC_URI = " \
	git://github.com/nemomobile/ti-omap3-sgx-wayland-wsegl.git;protocol=git;branch=master \
	file://0001-load-libEGL-sgx.so-in-libdir-by-default.patch \
	file://0002-updates-for-1.10-Linux-DDK.patch \
	file://0003-waylandwsegl-do-manual-update-only-for-devices-suppo.patch \
	file://0004-waylandwsegl-do-not-pin-numFlipBuffers-to-zero.patch \
	file://0005-close-framebuffer-in-case-it-is-not-used.patch \
	file://0006-remove-flip-buffer-limits-pointers-and-share-back-bu.patch \
	file://0007-Implement-EGL_EXT_buffer_age.patch \
	file://0008-eglCreateImageKHR-remove-EGL_WAYLAND_PLANE_WL-attrib.patch \
	file://wayland-egl.pc.in \
	file://egl.pc.in \
"
SRCREV = "5c56c8f809f1e08e5b774e3b728efd3d0a756dd3"
PV = "0.1.3+git${SRCPV}"

S = "${WORKDIR}/git"

inherit qmake5

DEPENDS += "libgles-omap3-wayland wayland"

DEFAULT_PREFERENCE_omap3 = "99"
DEFAULT_PREFERENCE = "-1"

# mesa means wayland-egl in wayland context (see weston)
PROVIDES = "virtual/mesa"
PROVIDES += "virtual/egl"

# weston requires egl >= 7.10 currently
VERSION = "9.0.0"

do_configure_prepend() {
	# shame on me for this ugly HACK but qtbase was only built native so far so we have no
	# mkspecs. So get them from qtbase-native...
	if [ ! -e  ${WORKDIR}/mkspecs ] ; then
		mkdir ${WORKDIR}/mkspecs
		cp -rf ${QMAKE_MKSPEC_PATH_NATIVE}/mkspecs/* ${WORKDIR}/mkspecs
		sed -i 's,^TartgetSpec.*,TartgetSpec = ${WORKDIR}/mkspecs/linux-oe-g++,' ${WORKDIR}/qt.conf
	fi
}

do_install_append() {
	# install pkconfigs
	install -d ${D}${libdir}/pkgconfig
	for pkgconfig_name in egl wayland-egl ; do
		sed \
			-e 's:%PREFIX%:${prefix}:g' \
			-e 's:%LIBDIR%:${libdir}:g' \
			-e 's:%INCDIR%:${includedir}:g' \
			-e 's:%VERSION%:${VERSION}:g' \
			< "${WORKDIR}/${pkgconfig_name}.pc.in" > "${D}/${libdir}/pkgconfig/${pkgconfig_name}.pc"
	done

}

FILES_${PN} += "${libdir}/*.so"
# eglinfo needs libEGL.so so pack dev package manually
FILES_${PN}-dev = "${libdir}/pkgconfig ${libdir}/libwayland-egl.so"
INSANE_SKIP_${PN} = "dev-so"

# we don't link statically
RDEPENDS_${PN} += "libgles-omap3-wayland"
