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
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.skynet.core.attribute.HttpAttributeTaggingListener;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.systems.EventManagerData;
import org.eclipse.osee.framework.skynet.core.event.systems.EventManagerFactory;
import org.eclipse.osee.framework.skynet.core.event.systems.InternalEventManager2;
import org.eclipse.osee.framework.skynet.core.event.systems.ResMessagingConnectionListener;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OseeEventSystemServiceRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class<?>[] {
      IOseeCachingService.class,
      IOseeCoreModelEventService.class};

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   private final EventManagerData eventManagerData;

   private ResMessagingConnectionListener connectionStatusListener;
   private IOseeCoreModelEventService coreModelEventService;
   private final Collection<IEventListener> coreListeners = new ArrayList<IEventListener>();

   public OseeEventSystemServiceRegHandler(EventManagerData eventManagerData) {
      this.eventManagerData = eventManagerData;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      coreModelEventService = getService(IOseeCoreModelEventService.class, services);

      EventManagerFactory factory = new EventManagerFactory();

      InternalEventManager2 eventManager = null;
      connectionStatusListener = new ResMessagingConnectionListener(eventManagerData.getPreferences());
      eventManager =
         factory.createNewEventManager(coreModelEventService, eventManagerData.getPreferences(),
            eventManagerData.getListeners(), eventManagerData.getPriorityListeners(), connectionStatusListener);

      if (eventManager != null) {
         eventManagerData.setMessageEventManager(eventManager);
         coreModelEventService.addConnectionListener(connectionStatusListener);
         eventManager.start();
         try {
            OseeEventManager.kickLocalRemEvent(eventManager, RemoteEventServiceEventType.Rem2_Connected);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.INFO, ex);
         }
         OseeLog.log(Activator.class, Level.INFO, "REM2: Enabled");
      } else {
         OseeLog.log(Activator.class, Level.INFO, "REM2: Disabled");
      }

      addCoreListeners();
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

   @Override
   public void onDeActivate() {
      removeCoreListeners();

      InternalEventManager2 eventManager = eventManagerData.getMessageEventManager();
      if (eventManager != null) {
         coreModelEventService.removeConnectionListener(connectionStatusListener);
         eventManager.stop();
         //         OseeEventManager.kickLocalRemEvent(eventManager, RemoteEventServiceEventType.Rem2_DisConnected);
         eventManagerData.setMessageEventManager(null);
      }
   }
}
