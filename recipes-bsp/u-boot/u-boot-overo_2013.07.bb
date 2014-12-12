require recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb \
                    file://README;beginline=1;endline=22;md5=78b195c11cb6ef63e6985140db7d7bab"

# SPL build
UBOOT_BINARY = "u-boot.img"
UBOOT_IMAGE = "u-boot-${MACHINE}-${PV}-${PR}.img"
UBOOT_SYMLINK = "u-boot-${MACHINE}.img"

PV = "2013.07"

COMPATIBLE_MACHINE = "overo"

SRC_URI = " \
	git://www.denx.de/git/u-boot.git;branch=master;protocol=git \
	file://0001-omap-overo-update-support-for-Micron-1GB-POP.patch \
	file://0002-cleanup_before_linux-void-Don-t-call-v7_outer_cache_.patch \
	file://0003-omap-overo-Use-200MHz-SDRC-timings-for-revision-1-2-.patch \
	file://0005-include-configs-omap3_overo.h-align-kernel-NAND-part.patch \
	file://0006-set-ext4-as-default-in-bootparams.patch \
	file://0007-omap3_overo.h-Add-consoleblank-0-to-default-paramete.patch \
	file://0008-add-bootz-command.patch \
"

SRCREV = "62c175fbb8a0f9a926c88294ea9f7e88eb898f6c"

LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"

S = "${WORKDIR}/git"

SPL_BINARY = "MLO"

do_deploy_append () {
	# ship also a version for good old x-loader
	install ${S}/u-boot.bin ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}-${PV}-${PR}.bin
	cd ${DEPLOY_DIR_IMAGE}
	ln -sf ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}-${PV}-${PR}.bin u-boot-${MACHINE}.bin
	ln -sf ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}-${PV}-${PR}.bin u-boot.bin
}

