require libgles-omap3-no-x.inc

LICENSE = "TSPA"

BINLOCATION_omap3 = "${S}/gfx_rel_es3.x"
BINLOCATION_ti816x = "${S}/gfx_rel_es6.x"
BINLOCATION_ti814x = "${S}/gfx_rel_es6.x"
BINLOCATION_ti33x = "${S}/gfx_rel_es8.x"
BINLOCATION_ti43x = "${S}/gfx_rel_es9.x"

PLATFORM = "LinuxARMV7"
PVR_INIT = "pvrsrvctl"

SGXPV = "5_01_01_01"
IMGPV = "1.10.2359475"
BINFILE = "Graphics_SDK_setuplinux_hardfp_${SGXPV}.bin"

TI_BIN_UNPK_WDEXT := "/Graphics_SDK_${SGXPV}"

# For now we only have hardfp version
python __anonymous() {
    tunes = bb.data.getVar("TUNE_FEATURES", d, 1)
    if not tunes:
        return
    pkgn = bb.data.getVar("PN", d, 1)
    pkgv = bb.data.getVar("PV", d, 1)
    if "callconvention-hard" not in tunes:
        bb.warn("%s-%s ONLY supports hardfp mode for now" % (pkgn, pkgv))
        raise bb.parse.SkipPackage("%s-%s ONLY supports hardfp mode for now" % (pkgn, pkgv))
}

SRC_URI = "http://software-dl.ti.com/dsps/dsps_public_sw/gfxsdk/${SGXPV}/exports/${BINFILE} \
	file://cputype \
	file://rc.pvr \
	file://99-bufferclass.rules  \
	file://glesv2.pc.in \
	file://0001-eglext.h-add-EGL_WAYLAND_BUFFER_WL-to-prepare-ti-oma.patch \
"

PKGCONFIGS-SHIPPED = "glesv2"

SRC_URI[md5sum] := "94acdbd20152c905939c2448d5e80a72"
SRC_URI[sha256sum] := "7f647bf45a5ce8ba9aaa28c4afe85fced4275f9a4567a1886d4460b76c9051ae"

S = "${WORKDIR}/Graphics_SDK_${SGXPV}"

LIBGLESWINDOWSYSTEM ?= "libpvrPVR2D_FRONTWSEGL.so.1"