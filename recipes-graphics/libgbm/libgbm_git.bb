DESCRIPTION = "Dummy gbm frontend"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://common.h;md5=4a0a5c8ce7a1b940bfcf501ab010ef54"

SRC_URI = " \
	git://github.com/robclark/libgbm.git;protocol=git;branch=master \
"
SRCREV = "c2b7a7783bbcd216db76b5dbea152e5a25f1b104"
PV = "0.0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS = "libdrm udev"
