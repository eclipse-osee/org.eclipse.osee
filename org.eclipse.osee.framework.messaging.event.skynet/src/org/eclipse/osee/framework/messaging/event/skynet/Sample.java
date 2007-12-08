/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.skynet;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import org.eclipse.osee.framework.jini.discovery.IServiceLookupListener;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.eclipse.osee.framework.jini.util.OseeJini;
import net.jini.core.lookup.ServiceItem;

public class Sample implements IServiceLookupListener, Serializable {
   private static final long serialVersionUID = 8195127334711471268L;

   private ISkynetEventService ses;
   private ASkynetEventListener listener;

   public Sample() {
      super();

      listener = new EventEchoer();

      // TODO this may take a very long time ... like FOREVER ... perform timeout ~5s if it is an absolutely necessary service
      //      ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).addListener(this, ISkynetEventService.class);
      ServiceDataStore.getNonEclipseInstance().addListener(this, ISkynetEventService.class);

      for (;;) {
         try {
            Thread.sleep(5000);
            System.out.print(new Date() + ":");
            if (ses == null) {
               System.out.println("Service not connected");
            } else {
               System.out.println("kick");
               try {
                  //                  ses.kick(new ISkynetEvent[]{new RemoteArtifactModifiedEvent(1, 2, 3, 4, "bob")}, listener);
               } catch (Exception e) {
                  e.printStackTrace();
                  ses = null;
               }
            }
         } catch (InterruptedException e) {
            break;
         }
      }
   }

   public void serviceAdded(ServiceItem serviceItem) {
      ses = (ISkynetEventService) serviceItem.service;
      try {
         ses.register((ISkynetEventListener) OseeJini.getRemoteReference(listener));
      } catch (RemoteException e) {
         e.printStackTrace();
      }
   }

   public void serviceChanged(ServiceItem serviceItem) {
      // like when the Entry[] changes
      System.out.println("***Service changed");
      serviceAdded(serviceItem);
   }

   public void serviceRemoved(ServiceItem serviceItem) {
      // Ensure the lookup server didn't just lose contact
      //      try {
      //         ses.someStupidCallLikeGetId();
      //      } catch(RemoteException ex) {
      ses = null;
      //      }

   }

   public static void main(String[] args) {
      new Sample();
   }

   private class EventEchoer extends ASkynetEventListener {
      private static final long serialVersionUID = 2251382843127874925L;

      @Override
      public void onEvent(ISkynetEvent[] events) throws RemoteException {
         System.out.println("Events received at " + new Date());

         for (ISkynetEvent event : events)
            System.out.println("\t" + event);
      }
   }
}
