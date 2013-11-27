package org.eclipse.osee.ote.rest.internal;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public interface OteJobStore {

   OTEJobStatus get(String uuid) throws InterruptedException, ExecutionException;

   Collection<String> getAll();

   void add(OteJob job);

}
