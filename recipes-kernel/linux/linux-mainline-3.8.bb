require linux.inc
require linux-common.inc

COMPATIBLE_MACHINE = "(overo)"

PV = "${KERNEL_PV_MAINLINE_38}"
PR = "r1"

FILESPATH =. "${FILE_DIRNAME}/linux-mainline-3.8:${FILE_DIRNAME}/linux-mainline-3.8/${MACHINE}:"

SRC_URI = " \
    ${SRC_URI_COMMON} \
    \
    file://board_init/0001-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://ti_dspbridge/0001-staging_tidspbridge_fix_breakages_due_to_CM_reorganization.patch \
    \
    file://ADS7846/0001-drivers-input-touchscreen-ads7846-return-ENODEV-if-d.patch \
    file://ADS7846/0002-board-overo.c-debounce-ADS7846.patch \
    \
    file://USB/0001-board-overo.c-call-usb_musb_init-with-host-mode-as-d.patch \
    \
    file://USB/0001-USB-ehci-omap-Don-t-free-gpios-that-we-didn-t-reques.patch \
    file://USB/0002-mfd-omap-usb-host-Consolidate-OMAP-USB-HS-platform-d.patch \
    file://USB/0003-mfd-omap-usb-tll-Fix-channel-count-detection.patch \
    file://USB/0004-mfd-omap-usb-tll-Use-devm_kzalloc-ioremap-and-clean-.patch \
    file://USB/0005-mfd-omap-usb-tll-Clean-up-clock-handling.patch \
    file://USB/0006-mfd-omap-usb-tll-introduce-and-use-mode_needs_tll.patch \
    file://USB/0007-mfd-omap-usb-tll-Check-for-missing-platform-data-in-.patch \
    file://USB/0008-mfd-omap-usb-tll-Fix-error-message.patch \
    file://USB/0009-mfd-omap-usb-tll-serialize-access-to-TLL-device.patch \
    file://USB/0010-mfd-omap-usb-tll-Add-OMAP5-revision-and-HSIC-support.patch \
    file://USB/0011-mfd-omap_usb_host-Avoid-missing-platform-data-checks.patch \
    file://USB/0012-mfd-omap-usb-host-Use-devm_kzalloc-and-devm_request_.patch \
    file://USB/0013-mfd-omap-usb-host-know-about-number-of-ports-from-re.patch \
    file://USB/0014-mfd-omap-usb-host-override-number-of-ports-from-plat.patch \
    file://USB/0015-mfd-omap-usb-host-cleanup-clock-management-code.patch \
    file://USB/0016-mfd-omap-usb-host-Manage-HSIC-clocks-for-HSIC-mode.patch \
    file://USB/0017-mfd-omap-usb-host-Get-rid-of-unnecessary-spinlock.patch \
    file://USB/0018-mfd-omap-usb-host-clean-up-omap_usbhs_init.patch \
    file://USB/0019-mfd-omap-usb-host-Don-t-spam-console-on-clk_set_pare.patch \
    file://USB/0020-mfd-omap-usb-host-get-rid-of-build-warning.patch \
    \
    file://USB/0001-ehci-omap.c-Don-t-soft-reset-PHY.patch \
    \
    file://smsc/0001-drivers-net-smsc911x-return-ENODEV-if-device-is-not-.patch \
    \
    file://opp/0001-omap3-Add-basic-support-for-720MHz-part.patch \
    file://opp/0002-overo-Add-opp-init.patch \
    \
    file://TWL4030/0001-board-overo.c-enable-TWL4030-power-off.patch \
"


#    file://DSS/0001-OMAPDSS-fix-registering-the-vsync-isr-in-apply.patch


PARALLEL_MAKEINST = ""
