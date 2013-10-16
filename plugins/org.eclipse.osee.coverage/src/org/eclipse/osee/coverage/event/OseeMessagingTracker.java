/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.event;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Donald G. Dunne
 */
public class OseeMessagingTracker extends ServiceTracker<MessageService, MessageService> {

   private ConnectionNode connectionNode;

   public OseeMessagingTracker(BundleContext bundleContext) {
      super(bundleContext, MessageService.class, null);
   }

   @Override
   public MessageService addingService(ServiceReference<MessageService> reference) {
      MessageService service = super.addingService(reference);
      try {
         connectionNode = service.getDefault();
         CoverageEventManager.instance.addingRemoteEventService(connectionNode);
      } catch (OseeCoreException ex) {
         OseeLog.log(OseeMessagingTracker.class, Level.SEVERE, ex);
      }
      return service;
   }

   @Override
   public void removedService(ServiceReference<MessageService> reference, MessageService service) {
      super.removedService(reference, service);
   }

   @Override
   public MessageService getService() {
      return super.getService();
   }

}
