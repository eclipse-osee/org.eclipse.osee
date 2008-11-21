java -Dorg.osgi.service.http.port=8015 \
-Dosgi.compatibility.bootdelegation=true \
-Xmx512m -Dosee.db.connection.id=oracle7 \
-Dequinox.ds.debug=true \
-Dosee.application.server.data=/lba_oseex/osee_backup/production_server_data \
-Dosee.check.tag.queue.on.startup=true \
-jar org.eclipse.osgi_3.4.0.v20080326.jar -console 