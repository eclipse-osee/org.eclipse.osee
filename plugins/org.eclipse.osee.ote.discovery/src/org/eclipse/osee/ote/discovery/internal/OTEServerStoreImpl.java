package org.eclipse.osee.ote.discovery.internal;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

public class OTEServerStoreImpl implements OTEServerStore {

   private ConcurrentHashMap<String, ServiceHealth> data;
   
   private Condition condition;

   private ReentrantLock lock;
   
   public OTEServerStoreImpl(ReentrantLock lock, Condition condition){
      data = new ConcurrentHashMap<String, ServiceHealth>();
      this.condition = condition;
      this.lock = lock;
   }
   
   @Override
   public void add(ServiceHealth serviceHealth) {
      data.put(serviceHealth.getServiceUniqueId(), serviceHealth);
      lock.lock();
      try{
         condition.signalAll();
      } finally {
         lock.unlock();
      }
   }

   @Override
   public void remove(ServiceHealth serviceHealth) {
      data.remove(serviceHealth.getServiceUniqueId());
      lock.lock();
      try{
         condition.signalAll();
      } finally {
         lock.unlock();
      }
   }

   @Override
   public Collection<ServiceHealth> getAll(){
      return data.values();
   }
}
