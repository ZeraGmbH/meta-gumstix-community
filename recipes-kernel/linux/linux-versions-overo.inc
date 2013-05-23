# central kernel settings (included by overo.conf)
KERNEL_GIT_URI_MAINLINE_32        = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-3.2.y;protocol=git"
KERNEL_GIT_SRCREV_MAINLINE_32     = "563b3269570396946bc2b08e078c6556f9029a95"
KERNEL_PV_MAINLINE_32             = "3.2.19"

KERNEL_GIT_URI_MAINLINE_38        = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-3.8.y;protocol=git"
KERNEL_GIT_SRCREV_MAINLINE_38     = "9c9ab76c48ffc48648c8e5da40d57965fc65c030"
KERNEL_PV_MAINLINE_38             = "3.8.12"

KERNEL_GIT_URI_OMAP_35            = "git://git.kernel.org/pub/scm/linux/kernel/git/tmlind/linux-omap.git;branch=master;protocol=git"
KERNEL_GIT_SRCREV_OMAP_35         = "28a33cbc24e4256c143dce96c7d93bf423229f92"
KERNEL_PV_OMAP_35                 = "3.5.0"

# here our selection is made (don't forget first..)
PREF_KERNEL_PROVIDER              = "linux-omap-3.5"
PREF_KERNEL_GIT_URI               = "${KERNEL_GIT_URI_OMAP_35}"
PREF_KERNEL_GIT_SRCREV            = "${KERNEL_GIT_SRCREV_OMAP_35}"
PREF_KERNEL_PV                    = "${KERNEL_PV_OMAP_35}"
