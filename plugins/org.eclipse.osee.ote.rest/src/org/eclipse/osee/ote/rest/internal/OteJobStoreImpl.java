package org.eclipse.osee.ote.rest.internal;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public class OteJobStoreImpl implements OteJobStore {

   ConcurrentHashMap<String, OteJob> jobs;
   
   public OteJobStoreImpl(){
      jobs = new ConcurrentHashMap<String, OteJob>();
   }
   
   @Override
   public OTEJobStatus get(String uuid) throws InterruptedException, ExecutionException {
      OteJob job = jobs.get(uuid);
      if(job != null){
         return job.getStatus(); 
      }
      return null;
   }

   @Override
   public Collection<String> getAll() {
      return jobs.keySet();
   }

   @Override
   public void add(OteJob job) {
      jobs.put(job.getId(), job);
   }

}
