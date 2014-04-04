DESCRIPTION = "Admin utility for Marvell uAP wireless driver"

SRC_URI = "${DEBIAN_MIRROR}/main/u/uaputl/uaputl_${PV}.orig.tar.gz"
SRC_URI[md5sum] = "1925d1f8e2507f92d603e202e213b857"
SRC_URI[sha256sum] = "50d73ca5457a59944b0eda6abf3df34f545e15a0fd6fa9c0567c2fb1c5fb6db6"

S = "${WORKDIR}/${PN}"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://uapcmd.c;beginline=5;endline=18;md5=b9860195196a7a0d4235ecb5fab8fb17"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/uaputl ${D}${bindir}/
}
