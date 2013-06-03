package org.eclipse.osee.ote.master.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.osee.ote.master.OTELookup;
import org.eclipse.osee.ote.master.OTELookupServerEntry;

class LookupTimeoutMonitor implements Runnable {

   private final OTELookup oteLookup;
   private int timeoutSeconds;
   
   public LookupTimeoutMonitor(OTELookup oteLookup, int timeoutSeconds) {
      this.oteLookup = oteLookup;
      this.timeoutSeconds = timeoutSeconds;
   }

   @Override
   public void run() {
      try{
         List<OTELookupServerEntry> availableServers = oteLookup.getAvailableServers();
         Calendar cal = Calendar.getInstance();
         cal.setTime(new Date());
         cal.add(Calendar.SECOND, timeoutSeconds*-1);
         List<OTELookupServerEntry> toRemove = new ArrayList<OTELookupServerEntry>();
         for(OTELookupServerEntry entry: availableServers){
            if(entry.getUpdateTime().before(cal.getTime())){
               toRemove.add(entry);
            }
         }
         for(OTELookupServerEntry entry: toRemove){
            oteLookup.removeServer(entry);
         }
      } catch (Throwable th){
         th.printStackTrace();
      }
   }

}
