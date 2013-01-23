require linux.inc
require linux-common.inc

COMPATIBLE_MACHINE = "(overo)"

PV = "${KERNEL_PV_MAINLINE_38}"

FILESPATH =. "${FILE_DIRNAME}/linux-mainline-3.8:${FILE_DIRNAME}/linux-mainline-3.8/${MACHINE}:"

SRC_URI = " \
    ${SRC_URI_COMMON} \
    \
    file://board_init/0001-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://ti_dspbridge/0001-staging_tidspbridge_fix_breakages_due_to_CM_reorganization.patch \
    \
    file://opp/0001-overo-Add-opp-init.patch \
    file://opp/0002-Add-basic-support-for-720MHz-part.patch \
    \
    file://ADS7846/0001-drivers-input-touchscreen-ads7846-return-ENODEV-if-d.patch \
    file://ADS7846/0002-board-overo.c-debounce-ADS7846.patch \
    \
    file://USB/0001-board-overo.c-call-usb_musb_init-with-host-mode-as-d.patch \
    \
    file://smsc/0001-drivers-net-smsc911x-return-ENODEV-if-device-is-not-.patch \
"


#    file://DSS/0001-OMAPDSS-fix-registering-the-vsync-isr-in-apply.patch


PARALLEL_MAKEINST = ""
