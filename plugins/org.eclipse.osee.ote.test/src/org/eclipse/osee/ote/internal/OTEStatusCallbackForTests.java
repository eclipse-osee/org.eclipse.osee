package org.eclipse.osee.ote.internal;

import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEStatusCallback;

public class OTEStatusCallbackForTests<T> implements OTEStatusCallback<OTEConfigurationStatus> {

   @Override
   public void complete(OTEConfigurationStatus done) {
      System.out.println("done");
   }

   @Override
   public void setTotalUnitsOfWork(int totalUnitsOfWork) {
      System.out.println("units " + totalUnitsOfWork);
   }

   @Override
   public void incrememtUnitsWorked(int count) {
      System.out.println("units " + count);
   }

   @Override
   public void log(String string) {
      System.out.println(string);
   }

   @Override
   public void error(String message, Throwable th) {
      System.out.println(message);
      th.printStackTrace();
   }

   @Override
   public void error(String message) {
      System.out.println(message);
   }

}
