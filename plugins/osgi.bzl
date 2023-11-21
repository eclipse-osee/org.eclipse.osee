def osgi_jar_converter(name, arg, visibility=None ):
  native.genrule(
    name = name,
    outs = [arg+ ".jar"],
    srcs = [
	    arg,  # a filegroup with multiple files in it ==> $(locations)
    ],
    cmd = "cp $(SRCS) .; pwd;mkdir amadeus;chmod 777 *; mv *.jar amadeus; cd  amadeus;chmod 777 *; \
           jar xf *.jar; ls;rm -rf bin META-INF; pwd; mv org.eclipse.osee*/* .; rm -rf org.eclipse.osee.*; \
	   jar cfm *.jar META-INF/MANIFEST.MF *;chmod 777 *;cp *.jar ..; chmod 777 *; cd .. ;ls;cp *.jar  $@",
    #tools = [arg],
    visibility = visibility,
  )

