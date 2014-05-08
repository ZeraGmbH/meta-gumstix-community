DESCRIPTION = "Kernel drivers for the PowerVR SGX chipset found in the omap3 SoCs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://MIT-COPYING;md5=8c2810fa6bfdc5ae5c15a0c1ade34054"

SRC_URI = "git://github.com/schnitzeltony/ti-sdk-pvr.git;protocol=git;branch=master"
SRCREV = "4281b0db20fae25010d239cb3ea671f9005731ed"
S = "${WORKDIR}/git/Graphics_SDK/GFX_Linux_KM/"

inherit module
PVRBUILD = "release"
export KERNELDIR = "${STAGING_KERNEL_DIR}"

TI_PLATFORM_omap3 = "omap3630"

MODULESLOCATION_omap3 = "dc_omapfb3_linux"

MAKE_TARGETS = " BUILD=${PVRBUILD} TI_PLATFORM=${TI_PLATFORM} SUPPORT_XORG=${@base_contains('DISTRO_FEATURES', 'x11', '1', '', d)}"

do_install() {
    mkdir -p ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/gpu/pvr
    cp  ${S}/pvrsrvkm.ko \
        ${S}/services4/3rdparty/${MODULESLOCATION}/omaplfb.ko  \
        ${S}/services4/3rdparty/bufferclass_ti/bufferclass_ti.ko \
        ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/gpu/pvr
}
