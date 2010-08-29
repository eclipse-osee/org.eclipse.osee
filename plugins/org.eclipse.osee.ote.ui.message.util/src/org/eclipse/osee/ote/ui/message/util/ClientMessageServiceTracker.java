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
package org.eclipse.osee.ote.ui.message.util;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 */
public class ClientMessageServiceTracker extends ServiceTracker {

   private final IOteMessageClientView viewer;

   public ClientMessageServiceTracker(BundleContext context, IOteMessageClientView viewer) {
      super(context, IOteMessageService.class.getName(), null);
      this.viewer = viewer;
   }

   @Override
   public synchronized Object addingService(ServiceReference reference) {
      IOteMessageService service = (IOteMessageService) super.addingService(reference);
      try {
         viewer.oteMessageServiceAcquired(service);
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE, "exception while notifying viewer of service", e);
      }
      return service;
   }

   @Override
   public synchronized void removedService(ServiceReference reference, Object service) {
      try {
         viewer.oteMessageServiceReleased();
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE,
            "exception while notifying viewer of service stop", e);
      } finally {
         super.removedService(reference, service);
      }
   }
}