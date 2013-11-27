package org.eclipse.osee.ote.rest.internal;

import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public interface OteJob {
   
   String getId();
   void setId(String uuid);
   OTEJobStatus getStatus() throws InterruptedException, ExecutionException;

}
