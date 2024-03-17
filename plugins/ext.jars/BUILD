package(default_visibility = ["//visibility:public"])

java_import(
        name = "junit",
        jars = [
                "org.junit_4.13.0.v20200204-1500.jar",
       ],
)

java_import(
        name = "javax",
        jars = [
                "javax.annotation_1.3.5.v20200504-1837.jar",
		"javax.transaction_1.1.1.v201105210645.jar",
		"javax.ws.rs_2.1.6.v20221203-1659.jar",
                "javax.xml.bind_2.2.0.v201105210648.jar",
                "javax.xml_1.4.1.v20220503-2331.jar",
                "javax.activation_1.1.0.v201211130549.jar",
                "javax.xml.ws_2.1.0.v200902101523.jar",
                "javax.xml.soap_1.3.0.v201105210645.jar",
                "javax.servlet_3.1.0.v201410161800.jar",
                "javax.validation_1.1.0.v20221112-0806.jar",
                "javax.mail_1.4.0.v201005080615.jar"
                
       ],
)


java_import(
    name = "swagger",
    jars = [
	    "swagger-annotations_2.2.7.jar",
	    "swagger-core_2.2.7.jar",
            "swagger-integration_2.2.7.jar",
	    "swagger-jaxrs2_2.2.7.jar",
	    "swagger-models_2.2.7.jar"
     ]
)

java_import(
       name = "poi",
       jars = [
         	"org.apache.poi.ooxml.schemas_4.1.1.v20221112-0806.jar",
		"org.apache.poi.ooxml_4.1.1.v20221112-0806.jar",
		"org.apache.poi_4.1.1.v20221112-0806.jar" 
       ]
)

java_import(
        name = "graphiti",
        jars = [
                "org.eclipse.graphiti.ui_0.17.0.202005151449.jar",
                "org.eclipse.graphiti_0.17.0.202005151449.jar"
       ],
)


java_import(
        name = "nebula",
        jars = [
                "org.eclipse.nebula.widgets.xviewer.core_1.0.0.202212191054.jar",
		"org.eclipse.nebula.widgets.xviewer_1.1.0.202302231924.jar",
		"org.eclipse.nebula.widgets.ganttchart_1.0.0.202303072132.jar"
	],
)

java_import(
        name = "apache_commons_lang",
        jars = [
                "org.apache.commons.lang_2.6.0.v201404270220.jar",
                "org.apache.commons.io_2.6.0.v20190123-2029.jar",
		"org.apache.commons.dbcp_1.4.0.v201204271417.jar",
		"org.apache.commons.pool_1.6.0.v201204271246.jar",
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
        name = "misc",
        jars = [
                "com.google.guava_30.1.0.v20221112-0806.jar",
                "org.apache.commons.codec_1.14.0.v20221112-0806.jar",
		"org.slf4j.api_1.7.30.v20200204-2150.jar",
		"org.antlr.runtime_3.2.0.v201101311130.jar"
                
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
       		"org.eclipse.sphinx.emf.serialization_0.13.1.202210010314.jar",
       		"org.eclipse.emf.edit_2.16.0.v20190920-0401.jar",
       		"org.eclipse.emf.transaction_1.9.1.201805140824.jar"
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
                "org.eclipse.ui.workbench.texteditor_3.14.200.v20200421-1954.jar",
		"org.eclipse.ui_3.117.0.v20200518-1705.jar",
		"org.eclipse.ui.ide_3.17.100.v20200530-0835.jar",
		"org.eclipse.ui.views_3.10.300.v20200311-1018.jar",
		"org.eclipse.ui.views.properties.tabbed_3.8.900.v20200513-0930.jar",
		"org.eclipse.e4.ui.workbench_1.11.300.v20200514-1625.jar",
		"org.eclipse.ui.console_3.9.200.v20200511-1122.jar",
		"org.eclipse.debug.ui_3.14.500.v20200520-0842.jar",
		"org.eclipse.jdt.ui-3.21.100.jar",
		"org.eclipse.ui.navigator_3.9.300.v20200513-0930.jar",
       ],
       
)

java_import(
        name = "help",
        jars = [
                "org.eclipse.help_3.8.800.v20200525-0755.jar"
       ],
       
)

       

