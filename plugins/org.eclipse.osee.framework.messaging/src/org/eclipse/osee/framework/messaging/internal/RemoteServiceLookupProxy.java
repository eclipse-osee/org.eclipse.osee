/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.services.RegisteredServiceReference;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.RemoteServiceRegistrar;
import org.eclipse.osee.framework.messaging.services.ServiceInfoPopulator;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.internal.RemoteServiceLookupImpl;
import org.eclipse.osee.framework.messaging.services.internal.RemoteServiceRegistrarImpl;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;

/**
 * @author Roberto E. Escobar
 */
public class RemoteServiceLookupProxy implements RemoteServiceLookup, RemoteServiceRegistrar {

   private static final long SERVICE_LOOKUP_TIMEOUT = 30;

   private ClassLoader contextClassLoader;
   private RemoteServiceRegistrarImpl registrar;
   private RemoteServiceLookupImpl lookup;
   private ScheduledExecutorService executor;

   private MessageService messageService;

   public void setMessageService(MessageService messageService) {
      this.messageService = messageService;
   }

   public void start() throws OseeCoreException {
      OseeLog.log(Activator.class, Level.INFO, "Remote Service Lookup - Starting");
      executor = Executors.newScheduledThreadPool(2);

      contextClassLoader = ExportClassLoader.getInstance();
      Thread.currentThread().setContextClassLoader(contextClassLoader);

      registrar = new RemoteServiceRegistrarImpl(messageService.getDefault(), executor);
      registrar.start();

      lookup =
         new RemoteServiceLookupImpl(messageService.getDefault(), executor, SERVICE_LOOKUP_TIMEOUT, TimeUnit.SECONDS);
      lookup.start();
      OseeLog.log(Activator.class, Level.INFO, "Remote Service Lookup - Started");
   }

   public void stop() {
      OseeLog.log(Activator.class, Level.INFO, "Remote Service Lookup - Stopping");
      if (lookup != null) {
         lookup.stop();
         lookup = null;
      }
      if (registrar != null) {
         registrar.stop();
         registrar = null;
      }

      if (executor != null) {
         executor.shutdown();
         executor = null;
      }
      contextClassLoader = null;
      OseeLog.log(Activator.class, Level.INFO, "Remote Service Lookup - Stopped");
   }

   @Override
   public void sendOutRequestsForServiceHealth() {
      lookup.sendOutRequestsForServiceHealth();
   }

   @Override
   public void register(String serviceId, String serviceVersion, ServiceNotification notification) {
      lookup.register(serviceId, serviceVersion, notification);
   }

   @Override
   public boolean unregister(String serviceId, String serviceVersion, ServiceNotification notification) {
      return lookup.unregister(serviceId, serviceVersion, notification);
   }

   @Override
   public RegisteredServiceReference registerService(String serviceName, String serviceVersion, String serviceUniqueId, URI broker, ServiceInfoPopulator infoPopulator, int refreshRateInSeconds) {
      return registrar.registerService(serviceName, serviceVersion, serviceUniqueId, broker, infoPopulator,
         refreshRateInSeconds);
   }

   @Override
   public boolean unregisterService(String serviceName, String serviceVersion, String serviceUniqueId) {
      return registrar.unregisterService(serviceName, serviceVersion, serviceUniqueId);
   }

}
