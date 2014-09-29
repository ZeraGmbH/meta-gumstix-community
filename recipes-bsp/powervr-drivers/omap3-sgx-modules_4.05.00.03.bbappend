FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PRINC := "${@int(PRINC) + 5}"

KERNEL_CC += "-D CONFIG_ARCH_OMAP3"

SRC_URI += " \
    file://0001-bufferclass_ti-bc_cat.c-Add-plat-cpu.h-for-latest-ke.patch \
    file://0002-omaplfb-Latest-kernels-can-not-be-patched-to-support.patch \
    file://0003-sgxinit.c-fix-crash-for-DM3730-Overo-Fire-COMs.patch \
"

MAKE_TARGETS_overo = " BUILD=${PVRBUILD} TI_PLATFORM=omap3"
