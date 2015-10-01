package org.eclipse.osee.ote.properties;

public enum OtePropertiesCore implements OteProperties {

   batchFolderDays("ote.batchfolder.days"),
   brokerUriPort("ote.server.broker.uri.port"),
   endpointPort("ote.endpoint.port"),
   httpPort("org.osgi.service.http.port"),
   ioRedirectFile("ote.io.redirect.file"),
   ioRedirect("ote.io.redirect"),
   ioRedirectPath("ote.io.redirect.path"),
   javaIoTmpdir("java.io.tmpdir"),
   lineSeparator("line.separator"),
   masterURI("ote.master.uri"),
   noStacktraceFilter("org.eclipse.osee.ote.core.noStacktraceFilter"),
   outfilesLocation("osee.ote.outfiles"),
   pingTimeout("ote.client.pingTimeout"),
   serverConnectionTimeout("ote.server.connection.timeout"),
   serverFactoryClass("osee.ote.server.factory.class"),
   serverKeepalive("osee.ote.server.keepAlive"),
   serverTitle("osee.ote.server.title"),
   timeDebug("ote.time.debug", false),
   abortMultipleInterrupt("ote.abort.interrupt.multiple", false),
   timeDebugTimeout("ote.time.debug.timeout", 250000),
   userHome("user.home"),
   userName("user.name"),
   oteServerFolder("osee.ote.server.folder"),
   oteStationName("osee.ote.station.name"),
   useLookup("osee.ote.use.lookup");
      
   private String key;
   private long defaultLong;
   private boolean defaultBoolean;
   private String defaultString;
   
   private final boolean defaultValueSet;
   
   OtePropertiesCore(String key){
      this.key = key;
      this.defaultValueSet = false;
   }
   
   OtePropertiesCore(String key, long defaultLong){
      this.key = key;
      this.defaultLong = defaultLong;
      this.defaultValueSet = true;
   }
   
   OtePropertiesCore(String key, boolean defaultBoolean){
      this.key = key;
      this.defaultBoolean = defaultBoolean;
      this.defaultValueSet = true;
   }
   
   OtePropertiesCore(String key, String defaultString){
      this.key = key;
      this.defaultString = defaultString;
      this.defaultValueSet = true;
   }
   
   @Override
   public String getKey() {
      return key;
   }

   @Override
   public void setValue(String value) {
      System.setProperty(key, value);
   }

   @Override
   public String getValue() {
      return System.getProperty(key);
   }
   
   @Override
   public String getValue(String defaultValue) {
      return System.getProperty(key, defaultValue);
   }

   public long getLongValue(long defaultValue) {
      long value = defaultValue;
      String valueStr = System.getProperty(key);
      try{
         value = Long.parseLong(valueStr);
      } catch (Throwable th){
      }
      return value;
   }

   public long getLongValue() {
      if(!defaultValueSet){
         throw new IllegalStateException("No default value set.");
      }
      long value = defaultLong;
      String valueStr = System.getProperty(key);
      try{
         value = Long.parseLong(valueStr);
      } catch (Throwable th){
      }
      return value;
   }
   
   public boolean getBooleanValue(boolean defaultValue) {
      boolean value = defaultValue;
      String valueStr = System.getProperty(key);
      if(valueStr != null){
         try{
            value = Boolean.parseBoolean(valueStr);
         } catch (Throwable th){
         }
      }
      return value;
   }
   
   public boolean getBooleanValue() {
      if(!defaultValueSet){
         throw new IllegalStateException("No default value set.");
      }
      boolean value = defaultBoolean;
      String valueStr = System.getProperty(key);
      if(valueStr != null){
         try{
            value = Boolean.parseBoolean(valueStr);
         } catch (Throwable th){
         }
      }
      return value;
   }
}
