FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS_append_overo = " libgbm"

PACKAGECONFIG_overo = "wayland launch fbdev kms egl"

EXTRA_OECONF_append_overo = " WESTON_NATIVE_BACKEND=fbdev-backend.so --with-cairo=glesv2"

SRC_URI_append_overo = " \
	file://0001-force-fbdev-to-use-tty1-by-default.patch \
"
