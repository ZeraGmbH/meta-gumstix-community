require u-boot.inc

# SPL build
UBOOT_BINARY = "u-boot.img"
UBOOT_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}.img"
UBOOT_SYMLINK = "u-boot-${MACHINE}.img"

PR = "r1"
PV = "2012.07"

COMPATIBLE_MACHINE = "overo"

SRC_URI = " \
	git://github.com/u-boot/u-boot.git;branch=master;protocol=ssh \
	file://0002-cleanup_before_linux-void-Don-t-call-v7_outer_cache_.patch \
	file://0003-config-Always-use-GNU-ld.patch \
	file://0004-overo-fix-crashes-for-boards-revision-1.patch \
	file://0005-include-configs-omap3_overo.h-align-kernel-NAND-part.patch \
	file://0006-set-ext4-as-default-in-bootparams.patch \
	file://0007-omap3_overo.h-Add-consoleblank-0-to-default-paramete.patch \
    file://0008-Add-stdint.h-include.patch \
"

SRCREV = "190649fb4309d1bc0fe7732fd0f951cb6440f935"

LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"

S = "${WORKDIR}/git"

do_deploy_append () {
	# ship also a version for good old x-loader
	install ${S}/u-boot.bin ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}-${PV}-${PR}.bin
	cd ${DEPLOY_DIR_IMAGE}
	ln -sf ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}-${PV}-${PR}.bin u-boot-${MACHINE}.bin
	ln -sf ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}-${PV}-${PR}.bin u-boot.bin
}

