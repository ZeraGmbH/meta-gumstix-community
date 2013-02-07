DESCRIPTION = "Linux kernel for gumstix overo"

DEPENDS += '${@base_conditional("FORCE_DYN_LINUX_LIBC", "1", "linux-libc-headers", "", d)}'
SRCREV_overo = "${PREF_KERNEL_GIT_SRCREV}"

FILESPATH =. "${FILE_DIRNAME}/splash:"
OVERO_BOOT_SPLASH ??= "file://logo_linux_clut224.ppm"

SRC_URI_COMMON = " \
    ${PREF_KERNEL_GIT_URI} \
    file://defconfig \
    ${OVERO_BOOT_SPLASH} \
"

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "uImage"
