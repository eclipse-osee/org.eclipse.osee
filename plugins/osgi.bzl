def osgi_jar_converter(name, arg, visibility=None ):
  native.genrule(
    name = name,
    outs = [arg+ ".jar"],
    srcs = [
	    arg,  # a filegroup with multiple files in it ==> $(locations)
    ],
    cmd = "cp $(SRCS) .;vat1=`echo $(OUTS) | awk -F/ '{print $$(NF-1)}'`;vat2=$${vat1}_dir;mkdir $$vat2;chmod 777 *$$vat1.jar;mv *$$vat1.jar $$vat2;chmod -R 777 $$vat2; cd  $$vat2; \
           jar xf *.jar;rm -rf bin META-INF;chmod -R 777 *; mv org.eclipse.osee*/* .; rm -rf org.eclipse.osee.*; \
	   jar cfm *.jar META-INF/MANIFEST.MF *;cp *.jar ..; cd .. ;cp *$$vat1.jar  $@",
    #tools = [arg],
    visibility = visibility,
  )


def server_p2(name, arg, srcs, visibility=None ):
  native.genrule(
    name = name,
    outs = [arg],
    srcs = 
            srcs,  # a filegroup with multiple files in it ==> $(locations)
    
	    cmd = "for FILE in $(SRCS).jar; do mv $$FILE  $${FILE%.*}.1.0.0.v2024.jar;done;pwd;ls $(SRCS);mkdir testdir;cd testdir;touch test.txt;cd -;cp -r testdir $@",
    #tools = [arg], 
    visibility = visibility,
  )


