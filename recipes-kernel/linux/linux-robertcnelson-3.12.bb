require linux.inc

COMPATIBLE_MACHINE = "(overo)"

DESCRIPTION = "Linux kernel for gumstix overo"

SRCREV_linux = "97f15f187a315f6a563dd5724c0cc8cde9044963"
SRCREV_rcnelsonlinuxdev = "4ced73261b4d1e11fe58ed8156958d9f05791d69"

FILESPATH =. "${FILE_DIRNAME}/splash:"
OVERO_BOOT_SPLASH ??= "file://logo_linux_clut224.ppm"

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "zImage"
PV = "3.12.8"

KERNEL_DEVICETREE_overo = "omap3-tobi.dtb"

FILESPATH =. "${FILE_DIRNAME}/linux-robertcnelson-3.12:${FILE_DIRNAME}/linux-robertcnelson-3.12/${MACHINE}:"

SRC_URI = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-3.12.y;protocol=git;name=linux \
    git://github.com/RobertCNelson/linux-dev.git;branch=master;protocol=git;destsuffix=rcnelsonlinuxdev;name=rcnelsonlinuxdev \
    \
    file://defconfig \
    ${OVERO_BOOT_SPLASH} \
"

PATCHSCRIPT = "${WORKDIR}/rcnelsonlinuxdev/patch.sh"

do_configure_prepend() {
	# try to apply patches - see log.do_configure for more information
	# remove imx patches
	sed -i	-e 's:\.patch\":\.patch\" || git am --abort:g' \
		-e 's:^imx[a-zA-Z0-9_]*$::g' \
		${PATCHSCRIPT}
	chmod +x ${PATCHSCRIPT}
	DIR=${WORKDIR}/rcnelsonlinuxdev ${PATCHSCRIPT}
}

#PARALLEL_MAKEINST = ""
