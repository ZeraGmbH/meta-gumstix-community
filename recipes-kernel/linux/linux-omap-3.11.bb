require linux.inc

COMPATIBLE_MACHINE = "(overo)"

DESCRIPTION = "Linux kernel for gumstix overo"

SRCREV_overo = "6e4664525b1db28f8c4e1130957f70a94c19213e"

FILESPATH =. "${FILE_DIRNAME}/splash:"
OVERO_BOOT_SPLASH ??= "file://logo_linux_clut224.ppm"

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "uImage"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

PV = "3.11.0"

FILESPATH =. "${FILE_DIRNAME}/linux-omap-3.11:${FILE_DIRNAME}/linux-omap-3.11/${MACHINE}:"

SRC_URI = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/tmlind/linux-omap.git;branch=master;protocol=git \
    \
    file://defconfig \
    ${OVERO_BOOT_SPLASH} \
    \
    file://board_init/0001-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://TWL4030/0001-drivers-rtc-rtc-twl.c-ensure-all-interrupts-are-disabled.patch \
    file://TWL4030/0002-omap2-twl-common-Add-default-power-configuration.patch \
    file://TWL4030/0003-board-overo.c-enable-common-power-data.patch \
    file://TWL4030/0004-board-overo.c-enable-TWL4030-power-off.patch \
    \
    file://dss/0001-DSS2-use-DSI-PLL-for-DPI-with-OMAP3.patch \
    \
    file://drm/0001-omap_irq.c-do-not-BUG-on-spin_is_locked.patch \
    \
    file://pm/0001-Add-opp-init-and-enable-higher-frequencies-for-OMAP-.patch \
    \
    file://ADS7846/0001-drivers-input-touchscreen-ads7846-return-ENODEV-if-d.patch \
    file://ADS7846/0002-board-overo.c-debounce-ADS7846.patch \
    \
    file://smsc/0001-drivers-net-smsc911x-return-ENODEV-if-device-is-not-.patch \
    \
    file://usb/0001-board-overo.c-set-correct-phy-vcc-data.patch \
"
