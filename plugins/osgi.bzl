def osgi_jar_converter(name, arg, visibility=None ):
  native.genrule(
    name = name,
    outs = [arg+".jar"],
    srcs = [
	    arg,  # a filegroup with multiple files in it ==> $(locations)
    ],
    cmd = "export DATE=$$(date  +%d-%b-%y);echo date111 $$DATE;echo $(OUTS);cp $(SRCS) .;vat1=`echo $(OUTS) | awk -F/ '{print $$(NF-1)}'`;vat2=$${vat1}_dir;mkdir $$vat2;chmod 777 *$$vat1.jar;mv *$$vat1.jar $$vat2;chmod -R 777 $$vat2; cd  $$vat2; \
           jar xf *.jar;rm -rf bin META-INF;chmod -R 777 *;chmod 777 $$vat1/META-INF/MANIFEST.MF;sleep 2; mv $$vat1*/* .; rm -rf org.eclipse.osee.*; \
	   jar cfm *.jar META-INF/MANIFEST.MF *;cp *.jar ..; cd .. ;echo vat jar *$$vat1.jar;cp *$$vat1.jar  $@",
    #tools = [arg],
    visibility = visibility,
  )


def server_p2(name, arg, srcs, visibility=None ):
  native.genrule(
    name = name,
    outs = [arg],
    srcs = srcs,  # a filegroup with multiple files in it ==> $(locations)
    #cmd = "for FILE in $(SRCS); do echo $$FILE;done;ls > $@",$${FILE%.*}.1.0.0.v2024.jar
    cmd = "mkdir osee_plugins;for FILE in $(SRCS); do mv $$FILE  $${FILE%.*}.1.0.0.v2024.jar;mv $${FILE%.*}.1.0.0.v2024.jar osee_plugins;pwd;done;echo after move;chmod -R 777 osee_plugins; cp -r osee_plugins $@",
    #tools = srcs, 
    visibility = visibility,
)

def plugins(name, arg, srcs, visibility=None ):
  native.genrule(
    name = name,
    outs = [arg],
    srcs = srcs,  # a filegroup with multiple files in it ==> $(locations)
    #cmd = "for FILE in $(SRCS); do echo $$FILE;done;ls > $@",$${FILE%.*}.1.0.0.v2024.jar
    cmd = "mkdir plugins;for FILE in $(SRCS); do mv $$FILE  $${FILE%.*}.1.0.0.v2024.jar;mv $${FILE%.*}.1.0.0.v2024.jar plugins;pwd;done;echo after move;cp -r plugins $@",
    #tools = srcs,
    visibility = visibility,
)
  

