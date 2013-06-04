package org.eclipse.osee.ote.master.internal;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.master.OTELookup;
import org.eclipse.osee.ote.master.OTELookupServerEntry;

public class OTELookupImpl implements OTELookup {

   private final List<OTELookupServerEntry> servers;
  
   private ScheduledExecutorService executor;
   private ScheduledFuture<?> scheduleAtFixedRate;
   private int timeoutSeconds = 60*3;

   public OTELookupImpl() {
      servers = new CopyOnWriteArrayList<OTELookupServerEntry>();
   }

   @Override
   public List<OTELookupServerEntry> getAvailableServers() {
      return servers;
   }

   @Override
   public void addServer(OTELookupServerEntry server) {
      OTELookupServerEntry oldone = find(server);
      if (oldone == null) {
         server.setUpdateTime(new Date());
         servers.add(server);
      } else {
         oldone.setConnectedUsers(server.getConnectedUsers());
         oldone.setUpdateTime(new Date());
      }
   }

   @Override
   public void removeServer(OTELookupServerEntry server) {
      OTELookupServerEntry oldone = find(server);
      if (oldone != null) {
         servers.remove(oldone);
      }
   }

   private OTELookupServerEntry find(OTELookupServerEntry otherEntry) {
      for (OTELookupServerEntry entry : servers) {
         if (entry.equals(otherEntry)) {
            return entry;
         }
      }
      return null;
   }
   
   public void start(){
      executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTELookupServerUpdateMonitor");
            th.setDaemon(true);
            return th;
         }
      });
      scheduleAtFixedRate = executor.scheduleAtFixedRate(new LookupTimeoutMonitor(this, timeoutSeconds), 0, 5, TimeUnit.SECONDS);
   }
   
   public void stop(){
      scheduleAtFixedRate.cancel(true);
      executor.shutdown();
   }
   
   void setTimeoutSeconds(int timeoutSeconds){
      this.timeoutSeconds = timeoutSeconds;
   }

}
