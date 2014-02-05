FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PACKAGECONFIG_overo = "wayland launch fbdev"

EXTRA_OECONF_append_overo = " WESTON_NATIVE_BACKEND=fbdev-backend.so"

SRC_URI_append_overo = " \
	file://0001-force-fbdev-to-use-gl-by-default.patch \
	file://0002-force-fbdev-to-use-tty1-by-default.patch \
"
