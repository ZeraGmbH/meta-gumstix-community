require linux.inc

COMPATIBLE_MACHINE = "(overo)"

DESCRIPTION = "Linux kernel for gumstix overo"

SRCREV_overo = "28a33cbc24e4256c143dce96c7d93bf423229f92"

FILESEXTRAPATHS_prepend = "${FILE_DIRNAME}/splash:"
OVERO_BOOT_SPLASH ??= "file://logo_linux_clut224.ppm"

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "uImage"
PR = "r126"
PV = "3.5.0"

FILESEXTRAPATHS_prepend = "${FILE_DIRNAME}/linux-omap-3.5:${FILE_DIRNAME}/linux-omap-3.5/${MACHINE}:"

SRC_URI = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/tmlind/linux-omap.git;branch=master;protocol=git \
    \
    file://defconfig \
    ${OVERO_BOOT_SPLASH} \
    \
    file://0001-Revert-ARM-OMAP-SoC-detection-remove-unused-cpu_is-m.patch \
    \
    file://board_init/0001-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://arm/0001-ARM-7668-1-fix-memset-related-crashes-caused-by-rece.patch \
    file://arm/0002-ARM-7670-1-fix-the-memset-fix.patch \
    \
    file://USB/0001-Revert-Fix-OMAP-EHCI-suspend-resume-failure-i693.patch \
    file://USB/0002-board-overo.c-call-usb_musb_init-with-host-mode-as-d.patch \
    file://USB/0001-ehci-omap.c-Don-t-soft-reset-PHY.patch \
    \
    file://TWL4030/0001-Add-power-off-support-for-the-TWL4030-companion.patch \
    file://TWL4030/0002-drivers-rtc-rtc-twl.c-ensure-all-interrupts-are-disabled.patch \
    \
    file://DSS/0001-OMAPDSS-OMAPFB-fix-framebuffer-console-colors.patch \
    \
    file://opp/0001-omap-overo-Add-opp-init.patch \
    file://opp/0002-omap3-Add-basic-support-for-720MHz-part.patch \
    file://opp/0003-board-overo.c-disable-1GHz-according-to-gumstix-reco.patch \
    \
    file://ADS7846/0001-drivers-input-touchscreen-ads7846-return-ENODEV-if-d.patch \
    file://ADS7846/0002-board-overo.c-debounce-ADS7846.patch \
    \
    file://smsc/0001-drivers-net-smsc911x-return-ENODEV-if-device-is-not-.patch \
    \
    file://mmc/0001-mmc-omap-add-sdio-interrupt-support.patch \
    file://mmc/0002-mmc-omap_hsmmc-remove-warning-message-for-debounce-c.patch \
    \
    file://core/0001-perf-Treat-attr.config-as-u64-in-perf_swevent_init.patch \
    file://core/0002-remove-inline-functions-for-__kfree_rcu-and-__is_kfr.patch \
    \
    file://wifi/0001-add-libertas_tf_sdio-module.patch \
    file://wifi/0002-mac80211-fix-crash-with-single-queue-drivers.patch \
"


PARALLEL_MAKEINST = ""
