package org.eclipse.osee.ote.rest.client;

public class BasicProgress implements Progress {

   private String fail;
   private Throwable throwable;
   private boolean success = false;
   private int totalUnitsOfWork;
   private int unitsWorked;
   
   @Override
   public void fail(String fail) {
      this.fail = fail;
   }

   @Override
   public void fail(Throwable th) {
      this.throwable = th;
   }

   @Override
   public void setUnitsOfWork(int totalUnitsOfWork) {
      this.totalUnitsOfWork = totalUnitsOfWork;
   }

   @Override
   public void setUnitsWorked(int unitsWorked) {
      this.unitsWorked = unitsWorked;
   }

   @Override
   public void success() {
      success  = true;
   }
   
   public String getFail() {
      return fail;
   }

   public Throwable getThrowable() {
      return throwable;
   }

   public boolean isSuccess() {
      return success;
   }

   public int getTotalUnitsOfWork() {
      return totalUnitsOfWork;
   }

   public int getUnitsWorked() {
      return unitsWorked;
   }
}
