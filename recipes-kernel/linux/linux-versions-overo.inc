# central kernel settings (included by overo.conf)
KERNEL_GIT_URI_MAINLINE_32        = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-3.2.y;protocol=git"
KERNEL_GIT_SRCREV_MAINLINE_32     = "563b3269570396946bc2b08e078c6556f9029a95"
KERNEL_PV_MAINLINE_32             = "3.2.19"

KERNEL_GIT_URI_MAINLINE_36        = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-3.6.y;protocol=git"
KERNEL_GIT_SRCREV_MAINLINE_36     = "3820288942d1c1524c3ee85cbf503fee1533cfc3"
KERNEL_PV_MAINLINE_36             = "3.6.6"

KERNEL_GIT_URI_MAINLINE_38        = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable.git;branch=linux-3.8.y;protocol=git"
KERNEL_GIT_SRCREV_MAINLINE_38     = "aa4cfdeb17e3559fe1e80175c7338e065553dce5"
KERNEL_PV_MAINLINE_38             = "3.8.5"

KERNEL_GIT_URI_SAKOMAN_32         = "git://www.sakoman.com/git/linux;branch=omap-3.2;protocol=git"
KERNEL_GIT_SRCREV_SAKOMAN_32      = "513770d80c0cfb26cc406c9ca3916df2e7afe46d"
KERNEL_PV_SAKOMAN_32              = "3.2.0"

KERNEL_GIT_URI_OMAP_35            = "git://git.kernel.org/pub/scm/linux/kernel/git/tmlind/linux-omap.git;branch=master;protocol=git"
KERNEL_GIT_SRCREV_OMAP_35         = "28a33cbc24e4256c143dce96c7d93bf423229f92"
KERNEL_PV_OMAP_35                 = "3.5.0"

# here our selection is made (don't forget first..)
PREF_KERNEL_PROVIDER              = "linux-omap-3.5"
PREF_KERNEL_GIT_URI               = "${KERNEL_GIT_URI_OMAP_35}"
PREF_KERNEL_GIT_SRCREV            = "${KERNEL_GIT_SRCREV_OMAP_35}"
PREF_KERNEL_PV                    = "${KERNEL_PV_OMAP_35}"
