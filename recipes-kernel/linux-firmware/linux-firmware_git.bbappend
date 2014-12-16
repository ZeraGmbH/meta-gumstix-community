do_install_append() {
	ln -sf libertas/lbtf_sdio.bin ${D}/lib/firmware/lbtf_sdio.bin
}

FILES_${PN}-sd8686 += " \
  /lib/firmware/lbtf_sdio.bin \
  /lib/firmware/libertas/lbtf_sdio.bin \
"
