DESCRIPTION = "Wayland WSEGL plugin for SGX drivers"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09edfc0c5635d7762a26198a98a4f931 \
                    file://LICENSE.LGPL;md5=24a4036de5c39ff01ad4986c4870d8c0"

SRC_URI = " \
	git://github.com/schnitzeltony/ti-omap3-sgx-wayland-wsegl.git;protocol=git;branch=drm-gbm \
	file://0001-load-libEGL-sgx.so-in-libdir-by-default.patch \
"
SRCREV = "748d77e5a7e453d3b4563d05c60f596ea6656ef6"
PV = "0.2.0"
#PV = "0.1.3+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS = "gles-omap3 wayland udev libdrm"

DEFAULT_PREFERENCE_omap3 = "99"
DEFAULT_PREFERENCE = "-1"

# mesa means wayland-egl/libgbm in wayland context (see weston)
PROVIDES = "virtual/mesa virtual/egl"

FILES_${PN} += "${libdir}/gbm/*.so"
FILES_${PN}-dbg += "${libdir}/gbm/.debug"
FILES_${PN}-dev += "${libdir}/gbm/*.la"
FILES_${PN}-staticdev += "${libdir}/gbm/*.a"

# eglinfo needs libEGL.so so pack in extra package
PACKAGES =+ "${PN}-so"
FILES_${PN}-so = "${libdir}/libEGL.so"
INSANE_SKIP_${PN}-so = "dev-so"
RDEPENDS_${PN} += "${PN}-so"

# our egl links during runtime
RDEPENDS_${PN} += "gles-omap3"

RRECOMMENDS_${PN} = "libdrm-omap"


