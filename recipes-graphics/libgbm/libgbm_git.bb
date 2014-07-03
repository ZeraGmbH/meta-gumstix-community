DESCRIPTION = "Dummy gbm frontend"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://common.h;md5=4a0a5c8ce7a1b940bfcf501ab010ef54"

SRC_URI = " \
	git://github.com/schnitzeltony/libgbm.git;protocol=git;branch=for-omap5-sdk \
"
SRCREV = "161d17e94896688504847786553dd3ba696354b9"
PV = "1.0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS = "libdrm udev"
RRECOMMENDS_${PN} = "libdrm-omap"
