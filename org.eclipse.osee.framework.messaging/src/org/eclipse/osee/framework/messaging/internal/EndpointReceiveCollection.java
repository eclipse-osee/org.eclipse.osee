/*
 * Created on May 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.messaging.EndpointReceive;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class EndpointReceiveCollection {

   private final List<EndpointReceive> receivers;
   
   public EndpointReceiveCollection(){
      receivers = new CopyOnWriteArrayList<EndpointReceive>();
   }
   
   public synchronized boolean add(EndpointReceive endpoint) {
      if(receivers.contains(endpoint)){
         return false;
      } else {
         return receivers.add(endpoint);
      }
   }

   public synchronized boolean remove(EndpointReceive endpoint) {
      return receivers.remove(endpoint);
   }

   public synchronized Collection<EndpointReceive> getAll() {
      return receivers;
   }

   public synchronized void dispose() {
      receivers.clear();
   }

}
