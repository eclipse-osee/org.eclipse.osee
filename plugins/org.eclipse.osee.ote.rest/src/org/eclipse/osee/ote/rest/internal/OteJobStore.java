package org.eclipse.osee.ote.rest.internal;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.rest.model.OteJobStatus;

public interface OteJobStore {

   OteJobStatus get(String uuid) throws InterruptedException, ExecutionException;

   Collection<String> getAll();

   void add(OteJob job);

}
