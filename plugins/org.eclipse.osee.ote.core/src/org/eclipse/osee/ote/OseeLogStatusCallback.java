package org.eclipse.osee.ote;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public class OseeLogStatusCallback implements OTEStatusCallback<ConfigurationStatus> {

   @Override
   public void complete(ConfigurationStatus done) {
      OseeLog.log(getClass(), Level.INFO, done.getMessage());
   }

   @Override
   public void setTotalUnitsOfWork(int totalUnitsOfWork) {
      
   }

   @Override
   public void incrememtUnitsWorked(int count) {
      
   }

   @Override
   public void log(String message) {
      OseeLog.log(getClass(), Level.INFO, message);
   }

   @Override
   public void error(String message, Throwable th) {
      OseeLog.log(getClass(), Level.SEVERE, message, th);      
   }

   @Override
   public void error(String message) {
      OseeLog.log(getClass(), Level.SEVERE, message);
   }

}
