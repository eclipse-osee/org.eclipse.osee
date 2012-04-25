/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.skynet.core.attribute.HttpAttributeTaggingListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.systems.EventManagerData;
import org.eclipse.osee.framework.skynet.core.event.systems.EventManagerFactory;
import org.eclipse.osee.framework.skynet.core.event.systems.InternalEventManager;
import org.eclipse.osee.framework.skynet.core.event.systems.ResMessagingConnectionListener;

/**
 * @author Roberto E. Escobar
 */
public class OseeEventAdmin {

   private IOseeCoreModelEventService coreModelEventService;

   private final EventManagerFactory factory = new EventManagerFactory();

   private ResMessagingConnectionListener connectionStatusListener;
   private final Collection<IEventListener> coreListeners = new ArrayList<IEventListener>();
   private Thread thread;

   public void setOseeCoreModelEventService(IOseeCoreModelEventService coreModelEventService) {
      this.coreModelEventService = coreModelEventService;
   }

   public void addEventListener(IEventListener listener) {
      OseeEventManager.addListener(listener);
   }

   public void removeEventListener(IEventListener listener) {
      OseeEventManager.removeListener(listener);
   }

   public void start() {
      final EventManagerData eventManagerData = OseeEventManager.getEventManagerData();

      connectionStatusListener = new ResMessagingConnectionListener(eventManagerData.getPreferences());

      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            InternalEventManager eventManager =
               factory.createNewEventManager(coreModelEventService, eventManagerData.getPreferences(),
                  eventManagerData.getListeners(), eventManagerData.getPriorityListeners(), connectionStatusListener);
            if (eventManager != null) {
               try {
                  eventManagerData.setMessageEventManager(eventManager);
                  coreModelEventService.addConnectionListener(connectionStatusListener);
                  eventManager.start();

                  try {
                     OseeEventManager.kickLocalRemEvent(eventManager, RemoteEventServiceEventType.Rem_Connected);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.INFO, ex);
                  }
                  addCoreListeners();
               } catch (Throwable th) {
                  OseeLog.log(Activator.class, Level.SEVERE, th);
               }
               OseeLog.log(Activator.class, Level.INFO, "Remote Event Service - Enabled");

            } else {
               OseeLog.log(Activator.class, Level.INFO, "Remote Event Service - Disabled");
            }
         }
      };
      thread = new Thread(runnable);
      thread.start();
   }

   private void addCoreListeners() {
      coreListeners.add(new HttpAttributeTaggingListener());
      for (IEventListener listener : coreListeners) {
         OseeEventManager.addListener(listener);
      }
   }

   private void removeCoreListeners() {
      for (IEventListener listener : coreListeners) {
         OseeEventManager.removeListener(listener);
      }
      coreListeners.clear();
   }

   public void stop() throws OseeCoreException {
      if (thread != null) {
         thread.interrupt();
         thread = null;
      }
      removeCoreListeners();

      InternalEventManager eventManager = OseeEventManager.getEventManagerData().getMessageEventManager();
      if (eventManager != null) {
         coreModelEventService.removeConnectionListener(connectionStatusListener);
         eventManager.stop();
         //         OseeEventManager.kickLocalRemEvent(eventManager, RemoteEventServiceEventType.Rem_DisConnected);
      }
   }
}
