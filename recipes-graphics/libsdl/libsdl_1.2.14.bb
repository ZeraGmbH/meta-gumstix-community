SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/ http://boards.openpandora.org/index.php/topic/6231-improved-sdl-for-pandora/"

COMPATIBLE_MACHINE = "(overo)"

SECTION = "libs"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=27818cd7fd83877a8e3ef82b82798ef4"

PROVIDES = "virtual/libsdl"

DEPENDS = "${@base_contains('DISTRO_FEATURES', 'directfb', 'directfb', '', d)} \
           ${@base_contains('DISTRO_FEATURES', 'opengl', 'virtual/libgl', '', d)} \
           ${@base_contains('DISTRO_FEATURES', 'x11', 'virtual/libx11 libxext libxrandr libxrender', '', d)} \
           tslib"

SRCREV = "ab66cdb4ccbec71ec4948a883510986189078dce"
SRC_URI = "git://notaz.gp2x.de/~notaz/sdl_omap.git;protocol=git;branch=master \
           file://0001-omapfb.h-apply-changes-to-kernel-3.5.patch \
           file://0002-omapdss-osdl_input.c-avoid-crash-in-case-of-no-touch.patch \
           file://debian_patches_neon-mixer.patch \
       "

S = "${WORKDIR}/git"

inherit autotools lib_package binconfig pkgconfig

EXTRA_OECONF = "--disable-static --disable-debug --enable-cdrom --enable-threads --enable-timers --enable-endian \
                --enable-file --disable-oss --disable-esd --disable-arts \
                --disable-diskaudio --disable-nas --disable-esd-shared --disable-esdtest \
                --disable-mintaudio --disable-nasm --disable-video-dga \
                --enable-video-fbcon --disable-video-ps2gs --disable-video-ps3 \
                --disable-video-xbios --disable-video-gem --disable-video-dummy \
                --enable-input-events --disable-input-tslib --enable-pthreads \
                ${@base_contains('DISTRO_FEATURES', 'directfb', '--enable-video-directfb', '--disable-video-directfb', d)} \
                ${@base_contains('DISTRO_FEATURES', 'opengl', '--enable-video-opengl', '--disable-video-opengl', d)} \
                ${@base_contains('DISTRO_FEATURES', 'x11', '--enable-video-x11', '--disable-video-x11', d)} \
                --disable-video-svga \
                --disable-video-picogui --disable-video-qtopia --enable-dlopen \
                --disable-rpath \
                --disable-pulseaudio"

PACKAGECONFIG ??= "${@base_contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa --disable-alsatest,--disable-alsa,alsa-lib,"

PARALLEL_MAKE = ""

EXTRA_AUTORECONF += "--include=acinclude --exclude=autoheader"

do_configure_prepend() {
        # Remove old libtool macros.
        MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
        for i in ${MACROS}; do
               rm -f acinclude/$i
        done
        export SYSROOT=$PKG_CONFIG_SYSROOT_DIR
}
