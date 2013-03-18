package org.eclipse.osee.ote;


public interface OTEStatusCallback<V> {

   void complete(V done);
   
   void setTotalUnitsOfWork(int totalUnitsOfWork);
   
   void incrememtUnitsWorked(int count);

   void log(String message);

   void error(String message, Throwable th);
   
   void error(String message);
   
}
