package org.eclipse.osee.ote.discovery.internal;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;
import org.eclipse.osee.ote.discovery.OTEServerDiscovery;
import org.eclipse.osee.ote.discovery.OTEServerLocation;

public class OTEServerDiscoveryImpl implements OTEServerDiscovery {

   private RemoteServiceLookup remoteMessageServiceLookup;
//   private MessageService messageService;
   private OTEServerStore store;
   private OteServerNotification notification;
   private ReentrantLock lock;
   private Condition condition;
   
   public OTEServerDiscoveryImpl(){
      lock = new ReentrantLock();
      condition = lock.newCondition();
      store = new OTEServerStoreImpl(lock, condition);
      notification = new OteServerNotification(store);
   }
   
   public void start(){
      new Thread(new Runnable() {
         @Override
         public void run() {
            remoteMessageServiceLookup.register("osee.ote.server", "1.0", notification);
            remoteMessageServiceLookup.sendOutRequestsForServiceHealth();
         }
      }).start();
   }
   
   public void stop(){
      new Thread(new Runnable() {
         @Override
         public void run() {
            remoteMessageServiceLookup.unregister("osee.ote.server", "1.0", notification);
         }
      }).start();
   }
   
   public void bindRemoteServiceLookup(RemoteServiceLookup remoteMessageServiceLookup){
      this.remoteMessageServiceLookup = remoteMessageServiceLookup;
   }
   
   public void unbindRemoteServiceLookup(RemoteServiceLookup remoteMessageServiceLookup){
      this.remoteMessageServiceLookup = null;
   }
   
//   public void bindMessageService(MessageService messageService){
//      this.messageService = messageService;
//   }
//   
//   public void unbindMessageService(MessageService messageService){
//      this.messageService = null;
//   }
   
   @Override
   public List<OTEServerLocation> findServerByTitle(String regex, long timeoutMs) throws URISyntaxException {
      lock.lock();
      List<OTEServerLocation> locations = findServerByTitle(regex);
      try{
         long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);
         while(nanos > 0 && locations.size() == 0) {
            try {
               nanos = condition.awaitNanos(nanos);
               locations = findServerByTitle(regex);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      } finally {
         lock.unlock();
      }
      return locations;
   }

   @Override
   public List<OTEServerLocation> findServerByMachine(String regex, long timeoutMs) throws URISyntaxException {
      lock.lock();
      List<OTEServerLocation> locations = findServerByMachine(regex);
      try{
         long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);
         while(nanos > 0 && locations.size() == 0) {
            try {
               nanos = condition.awaitNanos(nanos);
               locations = findServerByMachine(regex);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      } finally {
         lock.unlock();
      }
      return locations;
   }
   
   @Override
   public List<OTEServerLocation> findServerByTitle(String regex) throws URISyntaxException {
      Collection<ServiceHealth> healths = store.getAll();
      List<OTEServerLocation> locations = new ArrayList<OTEServerLocation>();
      for(ServiceHealth health:healths){
         OTEServerLocationServiceHealth item = new OTEServerLocationServiceHealth(health);
         if(item.isValid() && item.getTitle().matches(regex)){
            locations.add(item);
         }
      }
      return locations;
   }
   
   @Override
   public List<OTEServerLocation> findServerByMachine(String regex) throws URISyntaxException {
      Collection<ServiceHealth> healths = store.getAll();
      List<OTEServerLocation> locations = new ArrayList<OTEServerLocation>();
      for(ServiceHealth health:healths){
         OTEServerLocationServiceHealth item = new OTEServerLocationServiceHealth(health);
         if(item.isValid() && item.getMachineName().matches(regex)){
            locations.add(item);
         }
      }
      return locations;
   }

   @Override
   public List<OTEServerLocation> findServerByMachineAndTitle(String regexMachine, String regexTitle) throws URISyntaxException {
      Collection<ServiceHealth> healths = store.getAll();
      List<OTEServerLocation> locations = new ArrayList<OTEServerLocation>();
      for(ServiceHealth health:healths){
         OTEServerLocationServiceHealth item = new OTEServerLocationServiceHealth(health);
         if(item.isValid() && item.getTitle().matches(regexTitle) && item.getMachineName().matches(regexMachine)){
            locations.add(item);
         }
      }
      return locations;
   }

   @Override
   public List<OTEServerLocation> getAll() throws URISyntaxException {
      Collection<ServiceHealth> healths = store.getAll();
      List<OTEServerLocation> locations = new ArrayList<OTEServerLocation>();
      for(ServiceHealth health:healths){
         OTEServerLocationServiceHealth item = new OTEServerLocationServiceHealth(health);
         if(item.isValid()){
            locations.add(item);
         }
      }
      return locations;
   }

   @Override
   public List<OTEServerLocation> findServerByMachineAndTitle(String regexMachine, String regexTitle, long timeoutMs) throws URISyntaxException {
      lock.lock();
      List<OTEServerLocation> locations = findServerByMachineAndTitle(regexMachine, regexTitle);
      try{
         long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);
         while(nanos > 0 && locations.size() == 0) {
            try {
               nanos = condition.awaitNanos(nanos);
               locations = findServerByMachineAndTitle(regexMachine, regexTitle);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      } finally {
         lock.unlock();
      }
      return locations;
   }

}
