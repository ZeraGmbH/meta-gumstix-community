DESCRIPTION = "Linux Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel siteinfo
require recipes-kernel/linux/linux-dtb.inc

# Specify the commandline for your device

# Set the verbosity of kernel messages during runtime
# You can define CMDLINE_DEBUG in your local.conf or distro.conf to override this behaviour  
CMDLINE_DEBUG ?= '${@base_conditional("DISTRO_TYPE", "release", "quiet", "debug", d)}'
CMDLINE_append = " ${CMDLINE_DEBUG} "

# Kernel bootlogo is distro-specific (default is OE logo).
# Logo resolution (qvga, vga, ...) is machine-specific.
LOGO_SIZE ?= "."

do_configure_prepend() {

        echo "" > ${B}/.config

        #
        # logo support, if you supply logo_linux_clut224.ppm in SRC_URI, then it's going to be used
        #
        if [ -e ${WORKDIR}/${LOGO_SIZE}/logo_linux_clut224.ppm ]; then
                install -m 0644 ${WORKDIR}/${LOGO_SIZE}/logo_linux_clut224.ppm ${S}/drivers/video/logo/logo_linux_clut224.ppm
                echo "CONFIG_LOGO=y"                    >> ${B}/.config
                echo "CONFIG_LOGO_LINUX_CLUT224=y"      >> ${B}/.config
        fi

	# Enable thumb2 fixup for specific issue in angstrom toolchains when used on A8 r1p[012] silicon
        if [ "${CORTEXA8FIXUP}" = "yes" ] ; then
            sed -i -e /CONFIG_ARM_ERRATA_430973/d ${WORKDIR}/defconfig
            echo "CONFIG_ARM_ERRATA_430973=y" >> ${B}/.config
        fi

	#
	# endian support
	#
        if [ "${SITEINFO_ENDIANNESS}" = "be" ]; then
                echo "CONFIG_CPU_BIG_ENDIAN=y"          >> ${B}/.config
        fi

        echo "CONFIG_CMDLINE=\"${CMDLINE}\"" >> ${B}/.config

        sed -e '/CONFIG_OABI_COMPAT=/d' \
            -e '/CONFIG_CMDLINE=/d' \
            -e '/CONFIG_CPU_BIG_ENDIAN/d' \
            -e '/CONFIG_LOGO=/d' \
            -e '/CONFIG_LOGO_LINUX_CLUT224=/d' \
            -e '/CONFIG_LOCALVERSION/d' \
            -e '/CONFIG_LOCALVERSION_AUTO/d' \
	    < '${WORKDIR}/defconfig' >>'${B}/.config'

        echo 'CONFIG_LOCALVERSION=""' >>${B}/.config
        echo '# CONFIG_LOCALVERSION_AUTO is not set' >>${B}/.config

        #
        # Udev quirks
        #

        # Newer versions of udev mandate that sysfs doesn't have deprecated entries
        sed -e /CONFIG_SYSFS_DEPRECATED/d \
            -e /CONFIG_SYSFS_DEPRECATED_V2/d \
            -e /CONFIG_HOTPLUG/d \
            -e /CONFIG_UEVENT_HELPER_PATH/d \
            -e /CONFIG_UNIX/d \
            -e /CONFIG_SYSFS/d \
            -e /CONFIG_PROC_FS/d \
            -e /CONFIG_TMPFS/d \
            -e /CONFIG_INOTIFY_USER/d \
            -e /CONFIG_SIGNALFD/d \
            -e /CONFIG_TMPFS_POSIX_ACL/d \
            -e /CONFIG_BLK_DEV_BSG/d \
            -e /CONFIG_AUTOFS4_FS/d \
            -i '${B}/.config'

        echo '# CONFIG_SYSFS_DEPRECATED is not set' >> ${B}/.config
        echo '# CONFIG_SYSFS_DEPRECATED_V2 is not set' >> ${B}/.config
        echo 'CONFIG_HOTPLUG=y' >> ${B}/.config
        echo 'CONFIG_UEVENT_HELPER_PATH=""' >> ${B}/.config
        echo 'CONFIG_UNIX=y' >> ${B}/.config
        echo 'CONFIG_SYSFS=y' >> ${B}/.config
        echo 'CONFIG_PROC_FS=y' >> ${B}/.config
        echo 'CONFIG_TMPFS=y' >> ${B}/.config
        echo 'CONFIG_INOTIFY_USER=y' >> ${B}/.config
        echo 'CONFIG_SIGNALFD=y' >> ${B}/.config
        echo 'CONFIG_TMPFS_POSIX_ACL=y' >> ${B}/.config
        echo 'CONFIG_BLK_DEV_BSG=y' >> ${B}/.config
        echo 'CONFIG_DEVTMPFS=y' >> ${B}/.config
        echo 'CONFIG_DEVTMPFS_MOUNT=y' >> ${B}/.config
        echo 'CONFIG_AUTOFS4_FS=y' >> ${B}/.config

        # Newer inits like systemd need cgroup support
        sed -e /CONFIG_CGROUP_SCHED/d \
            -e /CONFIG_CGROUPS/d \
            -i '${B}/.config'
            
        echo 'CONFIG_CGROUP_SCHED=y' >> ${B}/.config
        echo 'CONFIG_CGROUPS=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_NS=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_FREEZER=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_DEVICE=y' >> ${B}/.config
        echo 'CONFIG_CPUSETS=y' >> ${B}/.config
        echo 'CONFIG_PROC_PID_CPUSET=y' >> ${B}/.config
        echo 'CONFIG_CGROUP_CPUACCT=y' >> ${B}/.config
        echo 'CONFIG_RESOURCE_COUNTERS=y' >> ${B}/.config

        #
        # root-over-nfs-over-usb-eth support. Limited, but should cover some cases.
        # Enable this by setting a proper CMDLINE_NFSROOT_USB.
        #
        if [ ! -z "${CMDLINE_NFSROOT_USB}" ]; then
                oenote "Configuring the kernel for root-over-nfs-over-usb-eth with CMDLINE ${CMDLINE_NFSROOT_USB}"
                sed -e '/CONFIG_INET/d' \
                    -e '/CONFIG_IP_PNP=/d' \
                    -e '/CONFIG_USB_GADGET=/d' \
                    -e '/CONFIG_USB_GADGET_SELECTED=/d' \
                    -e '/CONFIG_USB_ETH=/d' \
                    -e '/CONFIG_NFS_FS=/d' \
                    -e '/CONFIG_ROOT_NFS=/d' \
                    -e '/CONFIG_CMDLINE=/d' \
                    -i ${B}/.config
                echo "CONFIG_INET=y"                     >> ${B}/.config
                echo "CONFIG_IP_PNP=y"                   >> ${B}/.config
                echo "CONFIG_USB_GADGET=y"               >> ${B}/.config
                echo "CONFIG_USB_GADGET_SELECTED=y"      >> ${B}/.config
                echo "CONFIG_USB_ETH=y"                  >> ${B}/.config
                echo "CONFIG_NFS_FS=y"                   >> ${B}/.config
                echo "CONFIG_ROOT_NFS=y"                 >> ${B}/.config
                echo "CONFIG_CMDLINE=\"${CMDLINE_NFSROOT_USB}\"" >> ${B}/.config
        fi

        echo 'CONFIG_FHANDLE=y' >> ${B}/.config

        yes '' | oe_runmake -C ${S} O=${B} oldconfig
        oe_runmake -C ${S} O=${B} savedefconfig && cp ${B}/defconfig ${WORKDIR}/defconfig.saved
}

do_configure_append() {
        if test -e scripts/Makefile.fwinst ; then
		sed -i -e "s:-m0644:-m 0644:g" scripts/Makefile.fwinst
	fi
}

do_install_append() {
        oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}/src/linux-${KERNEL_VERSION} ARCH=$ARCH
        # we need drm source code - later versions of omap3-sgx-modules needs it
        mkdir -p ${D}${exec_prefix}/src/linux-${KERNEL_VERSION}/drivers/gpu/drm
        cp -f ${S}/drivers/gpu/drm/*.c ${D}${exec_prefix}/src/linux-${KERNEL_VERSION}/drivers/gpu/drm
}

PACKAGES =+ "kernel-headers"
FILES_kernel-headers = "${exec_prefix}/src/linux*"

# Automatically depend on lzop/lz4-native if CONFIG_KERNEL_LZO/LZ4 is enabled
python () {
    try:
        defconfig = bb.fetch2.localpath('file://defconfig', d)
    except bb.fetch2.FetchError:
        return

    try:
        configfile = open(defconfig)
    except IOError:
        return

    if 'CONFIG_KERNEL_LZO=y\n' in configfile.readlines():
        depends = d.getVar('DEPENDS', False)
        d.setVar('DEPENDS', depends + ' lzop-native')

    if 'CONFIG_KERNEL_LZ4=y\n' in configfile.readlines():
        depends = d.getVar('DEPENDS', False)
        d.setVar('DEPENDS', depends + ' lz4-native')

    configfile.close()
}
