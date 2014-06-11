DESCRIPTION =  "OMAP3 fork of Kernel drivers for the PowerVR SGX chipset found in the omap5 SoCs"
HOMEPAGE = "http://git.ti.com"
LICENSE = "MIT | GPLv2"
LIC_FILES_CHKSUM = "file://README;beginline=17;endline=25;md5=a51d60e9ef023972177f480930267e85"

#SRC_URI = "git://github.com/schnitzeltony/ti-sdk-pvr-drm.git;protocol=git;branch=omap3"
SRC_URI = "git://${HOME}/git-projects/sgx/omap5-sgx-ddk-linux;protocol=file;branch=omap3"
SRCREV = "d7b40f0a6c9fa9f5e9f1a7bb503070eb442bc416"

PV = "1.9.2253347"
#PV = "1.9.2253347+git${SRCPV}"
S = "${WORKDIR}/git/eurasia_km"

inherit module

export KERNELDIR = "${STAGING_KERNEL_DIR}"

do_compile_prepend() {
    cd ${S}/eurasiacon/build/linux2/omap3630_linux
}

do_install() {
    mkdir -p ${D}/lib/modules/${KERNEL_VERSION}/extra/
    cp ${S}/eurasiacon/binary2_omap3630_linux_release/target/kbuild/omapdrm_pvr.ko \
    ${D}/lib/modules/${KERNEL_VERSION}/extra/
}
