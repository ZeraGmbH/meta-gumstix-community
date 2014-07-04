DESCRIPTION = "Userspace libraries for omap5 sgx"
HOMEPAGE = "http://downloads.ti.com/dsps/dsps_public_sw/gfxsdk"
LICENSE = "TI-TSPA"
LIC_FILES_CHKSUM = "file://OMAP5-Linux-Graphics-DDK-UM-Manifest.doc;md5=360d293df455e4f2d363bb4014a49603"

#REQUIRED_DISTRO_FEATURES = "wayland"

INHIBIT_PACKAGE_STRIP = "1"

SRC_URI = " \
    git://git.ti.com/graphics/omap5-sgx-ddk-um-linux.git;protocol=git \
    file://0001-eglplatform.h-add-missing-entries-for-wayland-and-gb.patch \
"
SRCREV = "994922a5655c63b05343a9238cb30e6ef61744be"

S = "${WORKDIR}/git"
SOPV = "1.9.6.0"

do_install () {
	oe_runmake install DESTDIR=${D}

	install -d ${D}${sysconfdir}
	echo "[default]" > ${D}${sysconfdir}/powervr.ini
	echo "WindowSystem=libpvrws_WAYLAND.so" >> ${D}${sysconfdir}/powervr.ini
}

PACKAGES += "${PN}-xorg"

FILES_${PN} += "${libdir}/gbm/"
FILES_${PN}-xorg = "${libdir}/xorg/"

INSANE_SKIP_${PN} = "useless-rpaths dev-so"
INSANE_SKIP_${PN}-dev = "useless-rpaths"

# keep out mesa (we don't provide gl..)
PROVIDES = "virtual/libgl virtual/libgles1 virtual/libgles2 virtual/egl"

# virtual/mesa means wayland-egl/libgbm in wayland context (see weston)
PROVIDES += "virtual/mesa"
DEPENDS += "libgbm"

RDEPENDS_${PN} += "ti-sdk-pvr libgbm"
