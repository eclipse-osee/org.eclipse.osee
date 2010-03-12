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
package org.eclipse.osee.framework.messaging.event.skynet.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventService;
import org.eclipse.osee.framework.messaging.event.skynet.SkynetEventPlugin;

/**
 * @author Roberto E. Escobar
 */
public class EventDispatchRunnable implements Runnable {
   private final ISkynetEventService service;
   private final HashCollection<ISkynetEventListener, ISkynetEvent> toSend;

   public EventDispatchRunnable(final ISkynetEventService service, final HashCollection<ISkynetEventListener, ISkynetEvent> toSend) {
      this.service = service;
      this.toSend = toSend;
   }

   @Override
   public void run() {
      for (ISkynetEventListener listener : toSend.keySet()) {
         if (listener != null) {
            try {
               Collection<ISkynetEvent> values = toSend.getValues(listener);
               if (values != null && !values.isEmpty()) {
                  listener.onEvent(values.toArray(new ISkynetEvent[values.size()]));
               }
            } catch (IOException ex) {
               try {
                  service.deregister(listener);
                  OseeLog.log(SkynetEventPlugin.class, Level.WARNING,
                        "Listener unavailable - removing it from lookup:\n" + ex.getLocalizedMessage());
               } catch (RemoteException ex1) {
                  // Do Nothing - this should never happen
               } finally {
                  NumberFormat numFormat = NumberFormat.getInstance();
                  long totalMem = Runtime.getRuntime().totalMemory();
                  long freeMem = Runtime.getRuntime().freeMemory();
                  String totalMemory = numFormat.format(totalMem);
                  String usedMemory = numFormat.format(totalMem - freeMem);
                  String message =
                        String.format("JVM Heap space allocated: %s\nJVM Heap space used: %s\n", totalMemory,
                              usedMemory);
                  OseeLog.log(SkynetEventPlugin.class, Level.INFO, message);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetEventPlugin.class, Level.SEVERE, ex);
            }
         }
      }

   }
}
