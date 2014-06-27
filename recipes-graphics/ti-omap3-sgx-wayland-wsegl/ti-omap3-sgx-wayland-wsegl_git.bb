DESCRIPTION = "Wayland WSEGL plugin for SGX drivers"
LICENSE = "LGPLv2.1 & proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09edfc0c5635d7762a26198a98a4f931 \
                    file://LICENSE.LGPL;md5=24a4036de5c39ff01ad4986c4870d8c0"

SRC_URI = " \
	git://github.com/schnitzeltony/ti-omap3-sgx-wayland-wsegl.git;protocol=git;branch=master \
	file://0001-load-libEGL-sgx.so-in-libdir-by-default.patch \
"
SRCREV = "5db0d98935ab9387e3cc9dd0fdf8ba167c2e4b1f"
PV = "0.1.3+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS = "gles-omap3 wayland"
# mesa builds wayland-egl and gbm - to make recipes happy requiring both we depend on gbm
# as we do not provide both
DEPENDS += "libgbm"

DEFAULT_PREFERENCE_omap3 = "99"
DEFAULT_PREFERENCE = "-1"

# mesa means wayland-egl in wayland context (see weston)
PROVIDES = "virtual/mesa"
PROVIDES += "virtual/egl"

# pin spec path to native - qtbase for target does not yet exist
OE_QMAKE_DEBUG_OUTPUT = "-spec ${QMAKE_MKSPEC_PATH_NATIVE}/mkspecs/linux-oe-g++"

FILES_${PN} += "${libdir}/*.so"
# eglinfo needs libEGL.so so pack dev package manually
FILES_${PN}-dev = "${libdir}/pkgconfig ${libdir}/libwayland-egl.so ${libdir}/*.la"
INSANE_SKIP_${PN} = "dev-so"

# we don't link statically
RDEPENDS_${PN} += "gles-omap3"
