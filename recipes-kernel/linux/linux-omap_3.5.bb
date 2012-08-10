require linux.inc

DESCRIPTION = "Linux kernel gumstix overo"
KERNEL_IMAGETYPE = "uImage"

COMPATIBLE_MACHINE = "(overo)"

PV = "3.5"
SRCREV_pn-${PN} = "28a33cbc24e4256c143dce96c7d93bf423229f92"

# The main PR is now using MACHINE_KERNEL_PR, for omap3 see conf/machine/include/omap3.inc
MACHINE_KERNEL_PR_append = "e"

FILESPATH =. "${FILE_DIRNAME}/linux-omap:${FILE_DIRNAME}/linux-omap/${MACHINE}:"

OVERO_BOOT_LOGO ?= "file://logo_linux_clut224.ppm"

SRC_URI += " \
    git://git.kernel.org/pub/scm/linux/kernel/git/tmlind/linux-omap.git;branch=master;protocol=git \
    \
    file://0001-Revert-ARM-OMAP-SoC-detection-remove-unused-cpu_is-m.patch \
    \
    file://board_init/0001-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://USB/0001-board-overo.c-call-usb_musb_init-with-host-mode-as-d.patch \
    \
    file://TWL4030/0001-Add-power-off-support-for-the-TWL4030-companion.patch \
    \
    file://defconfig \
    ${OVERO_BOOT_LOGO} \
"

SRC_URI_append_beagleboard = " file://logo_linux_clut224.ppm \
"

S = "${WORKDIR}/git"
