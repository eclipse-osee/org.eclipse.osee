java -server \
-Xmx3G \
-Dorg.osgi.service.http.port=8089 \
-Dlogback.configurationFile=logback-dev.xml \
-Dorg.eclipse.equinox.http.jetty.context.sessioninactiveinterval=3600 \
-Dcm.config.uri="etc/osee.postgresql.json" \
-Dosee.authentication.protocol=trustAll \
-jar plugins/org.eclipse.equinox.launcher_1.5.700.v20200207-2156.jar -console -consoleLog
