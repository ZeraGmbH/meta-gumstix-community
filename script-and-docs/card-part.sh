#! /bin/bash

# part-card.sh
# (c) Copyright 2013 Andreas Müller <schnitzeltony@googlemail.com>
# Licensed under terms of GPLv2
#
# This script prepares partitions on SDCards. It wraps
# http://omappedia.org/wiki/Minimal-FS_SD_Configuration by dialog based GUI.

# for debugging set DEBUG=echo
DEBUG=

# SelectRootFileSystem() creates a dialog to select one of all available removable
# devices. The path to the selected device is stored in the variale DevicePath.
SelectRootFileSystem() {
	strSelection="0 ext4 1 ext3"
	dialog --title 'Select rootfs type'\
	--menu 'Move using [UP] [DOWN],[Enter] to select' 10 100 2\
	${strSelection}\
	2>/tmp/menuitem.$$

	# get OK/Cancel
	sel=$?
	# get selected menuitem
	menuitem=`cat /tmp/menuitem.$$`
	rm -f /tmp/menuitem.$$

	# Cancel Button or <ESC>
	if [ $sel -eq 1 -o $sel -eq 255 ] ; then
		echo 'Cancel selected SelectRootFileSystem()'
		return 1
	fi
	# Selection
	case $menuitem in
		0) RootfsType='ext4';;
		1) RootfsType='ext3';;
		*) echo "Unknown type"; exit 1;;
	esac
}

run_user() {
	if [ -z $DevicePath ]; then
		SelectCardDevice || exit 1
	fi
	if [ -z $RootfsType ]; then
		SelectRootFileSystem || exit 1
	fi
	if [ $# -gt 2 ]; then
		echo "Usage: $0 [Card device path] [Rootfs type]"
		exit 1;
	fi
	RootParams="$DevicePath $RootfsType"
}

run_root() {
	# device node valid?
	if [ ! -b $DevicePath ] ; then
		echo "$DevicePath is not a valid block device!"
		exit 1
	fi

	# file system valid?
	case $RootfsType in
		ext3) ;;
		ext4) ;;
		*) echo "Unknown rootfs type"; exit 1;;
	esac

	# check if the card is currently mounted
	MOUNTSTR=$(mount | grep $DevicePath)
	if [ -n "$MOUNTSTR" ] ; then
	    echo -e "\n$DevicePath is currenly mounted. Needs unmounting..."
	    $DEBUG umount -f ${DevicePath}?*
	fi

	# kill u-boot environment
	$DEBUG dd if=/dev/zero of=$DevicePath bs=1024 count=1024

	# Create the FAT partition of 64MB and make it bootable
	$DEBUG parted -s $DevicePath mklabel msdos
	$DEBUG parted -s $DevicePath mkpart primary fat32 63s 64MB
	$DEBUG parted -s $DevicePath toggle 1 boot

	# Create the rootfs partition until end of device
	$DEBUG parted -s $DevicePath -- mkpart primary $RootfsType 64MB -0

	# write partitions
	$DEBUG mkfs.vfat -F 32 -n "boot" -I ${DevicePath}1
	$DEBUG mke2fs -F -j -t $RootfsType -L "rootfs" ${DevicePath}2
}


. `dirname $0`/tools.inc
DevicePath=$1
RootfsType=$2
# On the 1st call: run user
# After the 2nd call: run root
chk_root && run_root
