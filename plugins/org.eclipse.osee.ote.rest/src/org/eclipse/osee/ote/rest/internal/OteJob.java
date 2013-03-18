package org.eclipse.osee.ote.rest.internal;

import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.rest.model.OteJobStatus;

public interface OteJob {
   
   String getId();
   void setId(String uuid);
   OteJobStatus getStatus() throws InterruptedException, ExecutionException;

}
