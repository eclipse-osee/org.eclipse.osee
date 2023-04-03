package(default_visibility = ["//visibility:public"])

java_import(
        name = "junit",
        jars = [
                "org.junit_4.13.0.v20200204-1500.jar",
       ],
)

java_import(
        name = "javax.annotation",
        jars = [
                "javax.annotation_1.3.5.v20200504-1837.jar",
       ],
)

java_import(
        name = "eclipse_core",
        jars = [
                "org.eclipse.core.runtime_3.18.0.v20200506-2143.jar",
                "org.eclipse.core.resources_3.13.700.v20200209-1624.jar",
                "org.eclipse.core.jobs_3.10.800.v20200421-0950.jar",
                "org.eclipse.core.commands_3.9.700.v20191217-1850.jar",
                "org.eclipse.debug.core_3.15.100.v20200514-0816.jar",
		"org.eclipse.core.filesystem_1.7.700.v20200110-1734.jar",
		"org.eclipse.core.filebuffers_3.6.1000.v20200409-1035.jar",
		"org.eclipse.compare_3.7.1000.v20200511-1203.jar",
		"org.eclipse.search_3.11.1000.v20200410-1852.jar",
		"org.eclipse.zest.core_1.5.300.201606061308.jar",
		"org.eclipse.zest.layouts_1.1.300.201606061308.jar"
       ],
)

java_import(
        name = "eclipse_osgi_services",
        jars = [
                "org.eclipse.osgi.services_3.8.0.v20190206-2147.jar",
               	"org.eclipse.osgi_3.15.300.v20200520-1959.jar"
       ],
)

java_import(
        name = "eclipse_equinox_common",
        jars = [
                "org.eclipse.equinox.common_3.12.0.v20200504-1602.jar",
                "org.eclipse.equinox.registry_3.8.800.v20200406-0956.jar",
                "org.eclipse.equinox.preferences_3.8.0.v20200422-1833.jar",
                "org.eclipse.equinox.app_1.4.500.v20200422-1833.jar",

       ],
)

java_import(
        name = "nebula",
        jars = [
                "org.eclipse.nebula.widgets.xviewer.core_1.0.0.202212191054.jar",
		"org.eclipse.nebula.widgets.xviewer_1.1.0.202302231924.jar"
       ],
)

java_import(
        name = "apache_commons_lang",
        jars = [
                "org.apache.commons.lang_2.6.0.v201404270220.jar",
                "org.apache.commons.io_2.6.0.v20190123-2029.jar",
		"org.apache.commons.dbcp_1.4.0.v201204271417.jar",
		"org.apache.commons.pool_1.6.0.v201204271246.jar"
       ],
)


java_import(
        name = "rmf",
        jars = [
                "org.eclipse.rmf.reqif10_0.13.0.201509161042.jar",
                "org.eclipse.rmf.reqif10.xhtml_0.13.0.201509161042.jar",
                "org.eclipse.rmf.reqif10.serialization_0.13.0.201509161042.jar"
       ],
)

java_import(
        name = "emf",
        jars = [
                "org.eclipse.emf.common_2.19.0.v20200324-0932.jar",
		"org.eclipse.emf.ecore_2.22.0.v20200519-1135.jar",
		"org.eclipse.emf.ecore.xmi_2.16.0.v20190528-0725.jar",
		"org.eclipse.emf.ecore.edit_2.13.0.v20190822-1451.jar",
		"org.eclipse.emf.ecore.change_2.14.0.v20190528-0725.jar",
       		"org.eclipse.sphinx.emf.serialization_0.13.1.202210010314.jar"
       ],
)

java_import(
        name = "jgit",
        jars = [
                "org.eclipse.jgit.ssh.jsch_5.8.0.202006091008-r.jar",
       ],
)

java_import(
        name = "swt",
        jars = 
               select ({

                   "@platforms//os:windows": ["org.eclipse.swt.win32.win32.x86_64_3.114.100.v20200604-0951.jar","org.eclipse.swt_3.114.100.v20200604-0951.jar"],
                   "@platforms//os:linux": ["org.eclipse.swt.gtk.linux.x86_64_3.114.100.v20200604-0951.jar","org.eclipse.swt_3.114.100.v20200604-0951.jar"],
                 
	           }
                )
       
)

java_import(
        name = "jface",
        jars = [
                "org.eclipse.jface_3.20.0.v20200505-1952.jar",
		"org.eclipse.jface.text_3.16.300.v20200526-0811.jar",
		"org.eclipse.text_3.10.200.v20200428-0633.jar",
		"com.lowagie.text_2.1.7.v201004222200.jar"
       ],
)

java_import(
        name = "eclipse_ui",
        jars = [
                "org.eclipse.ui.forms_3.9.100.v20200413-1417.jar",
                "org.eclipse.ui.workbench_3.119.0.v20200521-1247.jar",
		"org.eclipse.ui_3.117.0.v20200518-1705.jar",
		"org.eclipse.ui.ide_3.17.100.v20200530-0835.jar",
		"org.eclipse.ui.views_3.10.300.v20200311-1018.jar",
		"org.eclipse.ui.workbench.texteditor_3.14.200.v20200421-1954.jar",
		"org.eclipse.ui.console_3.9.200.v20200511-1122.jar",
		"org.eclipse.debug.ui_3.14.500.v20200520-0842.jar",
		"org.eclipse.ui.navigator_3.9.300.v20200513-0930.jar",
		"org.eclipse.e4.ui.workbench_1.11.300.v20200514-1625.jar"
       ],
)

java_import(
        name = "help",
        jars = [
                "org.eclipse.help_3.8.800.v20200525-0755.jar"
       ],
)

java_import(
        name = "eclipse_jdt",
        jars = [
		"org.eclipse.jdt.ui_3.21.100.v20200602-0959.jar"
       ],
)
