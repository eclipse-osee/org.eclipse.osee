package org.eclipse.osee.ote.master.rest.client;

public class OTEMasterServerResult {

   private Throwable th = null;
   private boolean success = true;
   
   public Throwable getThrowable() {
      return th;
   }
   public void setThrowable(Throwable th) {
      this.th = th;
   }
   public boolean isSuccess() {
      return success;
   }
   public void setSuccess(boolean success) {
      this.success = success;
   }
   
}
