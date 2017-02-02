PR = "r1"
SUMMARY = "IoTivity Example"
DESCRIPTION = "Minimalist Iotivity Client/Server application that share a resource"
HOMEPAGE = "https://github.com/TizenTeam/iotivity-example"
SECTION = "apps"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "sandbox/pcoval/geolocation"
SRC_URI = "git://github.com/TizenTeam/iotivity-example.git/;protocol=http;nobranch=1"

S = "${WORKDIR}/git"

inherit systemd pkgconfig

LOCAL_OPT_DIR = "/opt"
LOCAL_OPT_DIR_D = "${D}${LOCAL_OPT_DIR}"

DEPENDS += " iotivity "
BDEPENDS += " iotivity-dev "

DEPENDS_${PN} += "iotivity-resource-dev iotivity-resource-thin-staticdev iotivity-service-dev iotivity-service-staticdev"

BBCLASSEXTEND = "native nativesdk"
RDEPENDS_${PN} += " iotivity-resource "

SYSTEMD_SERVICE_${PN} = "${PN}.service"
EXTRA_OEMAKE = " package=${PN} "

do_configure() {
}

do_compile_prepend() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export LD_FLAGS="${LD_FLAGS}"

    # TODO: remove this workaround for iotivity-1.1.1
    # https://gerrit.iotivity.org/gerrit/#/c/13721
    ln -fs /usr/include/assert.h src/stdassert.h
}

do_compile() {
    cd ${S}
    LANG=C
    export LANG
    unset DISPLAY
    LD_AS_NEEDED=1; export LD_AS_NEEDED;

    oe_runmake all \
        name=${PN} \
	#eol
}

do_install() {
    export RPM_BUILD_ROOT=${D}
    cd ${S}
    LANG=C
    export LANG
    unset DISPLAY
    rm -rf ${D}
    install -d ${D}

    oe_runmake install \
        DESTDIR=${D} \
        name=${PN} \
        #eol

    install -d ${D}${base_libdir}/systemd/system
    install ${S}/extra/iotivity-example.service \
        ${D}${base_libdir}/systemd/system/${PN}.service
}

FILES_${PN} += "${LOCAL_OPT_DIR}/${PN}/bin/*"
FILES_${PN}-dbg += "${LOCAL_OPT_DIR}/${PN}/bin/.debug"
