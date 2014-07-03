DESCRIPTION = "Kernel drivers for the PowerVR SGX chipset found in the omap3 SoCs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://MIT-COPYING;md5=8c2810fa6bfdc5ae5c15a0c1ade34054"

# PVRBUILD = debug/release
PVRBUILD = "debug"

SRC_URI = " \
    git://github.com/schnitzeltony/ti-sdk-pvr.git;protocol=git;branch=for-omap3-ddk \
    file://0001-sgx_options-remove-debug-bit.patch \
"

SRCREV = "3af0cc3dc06968d14712ac7548bcd08eab90dedf"
PV = "5.01.01.01+git${SRCPV}"
S = "${WORKDIR}/git/Graphics_SDK/GFX_Linux_KM/"

inherit module

export KERNELDIR = "${STAGING_KERNEL_DIR}"

TI_PLATFORM_omap3 = "omap3630"

#SUPPORT_SECURE_DRM_AUTH_EXPORT=1
MAKE_TARGETS = "BUILD=${PVRBUILD} TI_PLATFORM=${TI_PLATFORM} SUPPORT_DRI_DRM=1 SUPPORT_DRI_DRM_EXTERNAL=1"

do_install() {
    mkdir -p ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/gpu/pvr
    cp  ${S}/omapdrm_pvr.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/gpu/pvr
}
