require linux.inc
require linux-common.inc

COMPATIBLE_MACHINE = "(overo)"

PR = "r124"
PV = "${KERNEL_PV_OMAP_35}"

FILESPATH =. "${FILE_DIRNAME}/linux-omap-3.5:${FILE_DIRNAME}/linux-omap-3.5/${MACHINE}:"

SRC_URI = " \
    ${SRC_URI_COMMON} \
    \
    file://0001-Revert-ARM-OMAP-SoC-detection-remove-unused-cpu_is-m.patch \
    \
    file://board_init/0001-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://USB/0001-Revert-Fix-OMAP-EHCI-suspend-resume-failure-i693.patch \
    file://USB/0002-board-overo.c-call-usb_musb_init-with-host-mode-as-d.patch \
    \
    file://TWL4030/0001-Add-power-off-support-for-the-TWL4030-companion.patch \
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
"

PARALLEL_MAKEINST = ""
