DESCRIPTION = "Kernel drivers for the PowerVR SGX chipset found in the omap3 SoCs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://MIT-COPYING;md5=8c2810fa6bfdc5ae5c15a0c1ade34054"

# PVRBUILD = debug/release
PVRBUILD = "debug"

SRC_URI = " \
    git://github.com/schnitzeltony/ti-sdk-pvr.git;protocol=git;branch=master \
    file://0001-sgx_options-remove-debug-bit.patch \
"

SRCREV = "01ff96f8616c3d07d30d1943cdd5259e99b92f3a"
PV = "5.01.01.01+git${SRCPV}"
S = "${WORKDIR}/git/Graphics_SDK/GFX_Linux_KM/"

inherit module

export KERNELDIR = "${STAGING_KERNEL_DIR}"

TI_PLATFORM_omap3 = "omap3630"

MODULESLOCATION_omap3 = "dc_omapfb3_linux"

MAKE_TARGETS = "BUILD=${PVRBUILD} TI_PLATFORM=${TI_PLATFORM} PM_RUNTIME=no"
# MAKE_TARGETS += "SUPPORT_XORG=${@base_contains('DISTRO_FEATURES', 'x11', '1', '', d)}"

do_install() {
    mkdir -p ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/gpu/pvr
    cp  ${S}/pvrsrvkm.ko \
        ${S}/services4/3rdparty/${MODULESLOCATION}/omaplfb.ko  \
        ${S}/services4/3rdparty/bufferclass_ti/bufferclass_ti.ko \
        ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/gpu/pvr
}
