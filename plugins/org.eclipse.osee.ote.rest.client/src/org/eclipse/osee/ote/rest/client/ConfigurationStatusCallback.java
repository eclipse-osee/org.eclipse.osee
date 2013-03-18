package org.eclipse.osee.ote.rest.client;

public interface ConfigurationStatusCallback {

   void fail(String string);
   void fail(Throwable th);

   void setUnitsOfWork(int totalUnitsOfWork);
   void setUnitsWorked(int unitsWorked);
   void success();

}
