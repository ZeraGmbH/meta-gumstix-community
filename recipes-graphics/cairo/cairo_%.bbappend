PACKAGECONFIG = " \
	${@base_contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
	${@base_contains('DISTRO_FEATURES', 'directfb', 'directfb', '', d)} \
	${@base_contains('DISTRO_FEATURES', 'wayland', 'gles2', '', d)} \
"

