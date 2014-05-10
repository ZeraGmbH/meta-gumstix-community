THISDIR := "${@os.path.dirname(bb.data.getVar('FILE', d, True))}"
FILESEXTRAPATHS_prepend = "${@base_set_filespath(["${THISDIR}/${PN}"], d)}:"
