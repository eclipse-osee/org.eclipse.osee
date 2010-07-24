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
package org.eclipse.osee.ote.message.internal;

import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.message.io.IMessageIoManagementService;
import org.eclipse.osee.ote.message.io.MessageIoManagementService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 *
 */
public class MessageIoManagementStarter extends ServiceTracker{

   private ServiceRegistration registration;
   /**
    * @param context
    * @param filter
    * @param customizer
    */
   public MessageIoManagementStarter(BundleContext context) {
      super(context, TestEnvironmentInterface.class.getName(), null);
   }

   @Override
   public synchronized TestEnvironmentInterface addingService(ServiceReference reference) {
      TestEnvironmentInterface manager = (TestEnvironmentInterface) super.addingService(reference);
      registration = context.registerService(IMessageIoManagementService.class.getName(), new MessageIoManagementService(), null);
      return manager;
   }

   @Override
   public synchronized void removedService(ServiceReference reference, Object service) {
      registration.unregister();
      registration = null;
      super.removedService(reference, service);
   }

   @Override
   public synchronized void close() {
      if (registration != null) {
         registration.unregister();
      }
      super.close();
   }
   
   
}
