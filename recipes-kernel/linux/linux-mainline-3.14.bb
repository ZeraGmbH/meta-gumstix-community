require linux.inc

COMPATIBLE_MACHINE = "(overo)"

DESCRIPTION = "Linux kernel for gumstix overo"

SRCREV_overo = "bbae7add628cfe96a1facd578dd1eddcd1030de7"

FILESEXTRAPATHS_prepend = "${FILE_DIRNAME}/splash:"
OVERO_BOOT_SPLASH ??= "file://logo_linux_clut224.ppm"

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "uImage"
KERNEL_EXTRA_ARGS += "LOADADDR=${UBOOT_ENTRYPOINT}"

PV = "3.14.10"

FILESEXTRAPATHS_prepend = "${FILE_DIRNAME}/linux-mainline-3.14:${FILE_DIRNAME}/linux-mainline-3.14/${MACHINE}:"

SRC_URI = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-3.14.y;protocol=git \
    \
    file://defconfig \
    ${OVERO_BOOT_SPLASH} \
    \
    file://core/0001-OMAP3-output-further-revision-information.patch \
    \
    file://board_init/0001-board-overo.c-double-NAND-partition-for-kernel-to-8M.patch \
    \
    file://TWL4030/0001-drivers-rtc-rtc-twl.c-ensure-all-interrupts-are-disabled.patch \
    file://TWL4030/0002-omap2-twl-common-Add-default-power-configuration.patch \
    file://TWL4030/0003-board-overo.c-enable-common-power-data.patch \
    file://TWL4030/0004-board-overo.c-enable-TWL4030-power-off.patch \
    \
    file://dss/0001-DSS2-use-DSI-PLL-for-DPI-with-OMAP3.patch \
    file://dss/0002-board-overo.c-rework-overo_display_init-and-give-a-b.patch \
    file://dss/0003-OMAPDSS-Fix-DSS-clock-multiplier-issue-on-3703-and-probably-3630.patch \
    \
    file://nand/0001-mtd-nand-omap2-Fix-device-detection-path.patch \
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
    file://usb/0002-usb-musb-omap-remove-using-PLATFORM_DEVID_AUTO-in-omap2430-c.patch \
    file://usb/0003-arm-omap-remove-auto-from-device-names-given-in-usb_bind_phy.patch \
    \
    file://wifi/0001-board-overo.c-make-wifi-work.patch \
    file://wifi/0002-libertas-don-t-return-ENOSYS-to-make-supend-work.patch \
    file://wifi/0003-add-libertas_tf_sdio-module.patch \
    file://wifi/0004-add-sd8686_uap-module.patch \
    file://wifi/0005-mmc-omap_hsmmc-Add-support-for-quirky-omap3-hsmmc-co.patch \
    file://wifi/0006-mmc-omap_hsmmc-save-clock-rate-to-use-in-interrupt-c.patch \
    file://wifi/0007-mmc-omap_hsmmc-fix-request-done-for-sbc-error-case.patch \
    file://wifi/0008-mmc-omap_hsmmc-split-dma-setup.patch \
    file://wifi/0009-mmc-omap_hsmmc-add-cmd23-support.patch \
    file://wifi/0010-mmc-omap_hsmmc-add-autocmd23-support.patch \
    file://wifi/0011-mmc-omap_hsmmc-Enable-SDIO-interrupts.patch \
    \
    file://powervr/0001-export-symbols-required-by-powervr.patch \
    file://powervr/0002-re-add-driver_private-to-struct-drm_gem_object-pvr-n.patch \
    file://powervr/0003-re-add-priv-to-struct-omap_gem_object-pvr-needs-it-t.patch \
    file://powervr/0004-omap_drm-open-door-for-pvr.patch \
    file://powervr/0005-omap_drm-open-door-for-pvr-II.patch \
"
