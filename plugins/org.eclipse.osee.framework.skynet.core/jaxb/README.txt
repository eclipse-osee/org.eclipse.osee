xjc frameworkMessages.xsd -extension -d ../src-gen -p org.eclipse.osee.framework.skynet.core.event.msgs

After regeration of messages:
1) Delete DefaultBasicGuidArtifact (we want to use skynet.core's version)
2) Delete constructor from ObjectFactory.java