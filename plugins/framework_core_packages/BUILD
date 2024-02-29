
java_library(
    name = "framework_core_packages",
    exports = [
	    "//org.eclipse.osee.framework.jdk.core:osgi_jar",
               "//org.eclipse.osee.framework.core",
               "//org.eclipse.osee.framework.logging",
               "//org.eclipse.osee.logger"
              ],
    visibility = ["//visibility:public"]
)

java_import(
    name = "murus",
    deps = ["//org.eclipse.osee.framework.jdk.core:osgi_jar"],
    jars = [
            "//org.eclipse.osee.framework.jdk.core:osgi_jar",
	    "//org.eclipse.osee.framework.core:osgi_jar",
	    "//org.eclipse.osee.framework.logging:osgi_jar",
	    "//org.eclipse.osee.logger:osgi_jar"
              ],
    visibility = ["//visibility:public"]
)

