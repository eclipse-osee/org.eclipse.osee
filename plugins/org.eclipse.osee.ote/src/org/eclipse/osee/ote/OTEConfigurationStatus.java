package org.eclipse.osee.ote;

public class OTEConfigurationStatus {

   private OTEConfiguration configuration;
   private boolean success;
   private String message;

   public OTEConfigurationStatus(OTEConfiguration configuration, boolean success, String message) {
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
   
   public OTEConfiguration getConfiguration(){
      return configuration;
   }

}
