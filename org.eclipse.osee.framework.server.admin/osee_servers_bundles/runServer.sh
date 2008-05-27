java -Dorg.osgi.service.http.port=8089 \
-Dosgi.compatibility.bootdelegation=true \
-Xmx512m -DDefaultDbConnection=oracle7 \
-Dorg.eclipse.osee.framework.resource.provider.attribute.basepath=/lba_ws/osee_common/oseeAttributeProvider/ \
-jar org.eclipse.osgi_3.4.0.v20080326.jar -console 