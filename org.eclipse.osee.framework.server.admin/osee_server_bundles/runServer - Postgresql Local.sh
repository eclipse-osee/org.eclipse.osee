java -Dorg.osgi.service.http.port=8089 \
-Dosgi.compatibility.bootdelegation=true \
-Xmx512m -Dosee.db.connection.id=postgresqlLocalhost \
-Dequinox.ds.debug=true \
-jar org.eclipse.osgi_3.4.0.v20080326.jar -console 