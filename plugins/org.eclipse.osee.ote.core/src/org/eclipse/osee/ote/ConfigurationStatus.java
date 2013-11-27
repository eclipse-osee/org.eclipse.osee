package org.eclipse.osee.ote;

public class ConfigurationStatus {

   private Configuration configuration;
   private boolean success;
   private String message;

   public ConfigurationStatus(Configuration configuration, boolean success, String message) {
      this.configuration = configuration;
      this.success = success;
      this.message = message;
   }

   public void setFail(String message) {
      success = false;
      this.message = message;
   }
   
   public boolean isSuccess(){
      return success;
   }
   
   public String getMessage(){
      return message;
   }
   
   public Configuration getConfiguration(){
      return configuration;
   }

}
