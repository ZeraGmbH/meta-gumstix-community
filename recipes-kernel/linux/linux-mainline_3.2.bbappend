# meta-ti does not support overo
COMPATIBLE_MACHINE_overo = "(overo)"

PRINC := "${@int(PRINC) + 1}"

PV_overo = "3.2.19"
SRCREV_overo = "563b3269570396946bc2b08e078c6556f9029a95"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-mainline-3.2:${THISDIR}/splash:"

OVERO_BOOT_LOGO ?= "file://logo_linux_clut224.ppm"

SRC_URI_append_overo = "\
    file://TWL4030/0001-rtc-twl-add-support-for-backup-battery-recharge.patch \
    file://TWL4030/0002-soc-codecs-Enable-audio-capture-by-default-for-twl4030.patch \
    file://TWL4030/0003-soc-codecs-twl4030-Turn-on-mic-bias-by-default.patch \
    file://TWL4030/0004-omap-mmc-twl4030-move-clock-input-selection-prior-to-vcc-test.patch \
    file://TWL4030/0005-Add-power-off-support-for-the-TWL4030-companion.patch \
    file://TWL4030/0006-Enabling-Hwmon-driver-for-twl4030-madc.patch \
    \
    file://board_init/0001-board-overo.c-use-sakoman-s-board-init-code.patch \
    file://board_init/0002-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://USB/0001-board-overo.c-call-usb_musb_init-with-host-mode-as-d.patch \
    \
    file://MMC/0001-mmc-omap-add-sdio-interrupt-support.patch \
    \
    file://I2C/0001-OMAP-I2C-Fix-timeout-problem-during-suspend.patch \
    \
    file://DSS/0001-OMAPDSS-use-sync-versions-of-pm_runtime_put.patch \
    \
    ${OVERO_BOOT_LOGO} \
"

# Overwrite meta-ti parallel make options on all tasks (install fails with
# parallel make). So we have at least compile with standard PARALLEL_MAKE
# options.
EXTRA_OEMAKE = ""
# TODO: Kernel currently only uses PARALLEL_MAKE (error occures on install)
PARALLEL_MAKE = ""
#PARALLEL_MAKEINST = ""
