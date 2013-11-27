package org.eclipse.osee.ote.rest.client;

public interface ProgressWithCancel extends Progress {

   boolean cancelAll();

   boolean cancelSingle();
   
}
