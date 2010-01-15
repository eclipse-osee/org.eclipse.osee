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
package org.eclipse.osee.framework.messaging.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.osee.framework.messaging.MessagingGateway;
import org.eclipse.osee.framework.messaging.future.MessageService;
import org.eclipse.osee.framework.messaging.internal.old.MessagingGatewayImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Andrew M. Finkbeiner
 */
public class Activator implements BundleActivator {
   private static Activator me;
   private BundleContext context;

   private ServiceRegistration msgServiceRegistration;
   private MessageService messageService;
   private CamelContext camelContext;
   private ExecutorService executor;

   // old
   private ServiceRegistration registration;
   private MessagingGatewayImpl messaging;

   public void start(BundleContext context) throws Exception {
      this.context = context;
      me = this;

      camelContext = new DefaultCamelContext();
      executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

      messageService = createMessageService();
      msgServiceRegistration = context.registerService(MessageService.class.getName(), messageService, null);
      camelContext.start();

      //old
      messaging = new MessagingGatewayImpl();
      registration = context.registerService(MessagingGateway.class.getName(), messaging, null);
   }

   private MessageService createMessageService() {
      return new MessageServiceImpl(camelContext, new ConnectionNodeFactoryImpl(camelContext, executor));
   }

   public void stop(BundleContext context) throws Exception {
      me = null;
      this.context = null;

      if (msgServiceRegistration != null) {
         msgServiceRegistration.unregister();
      }
      camelContext.stop();

      //old
      if (registration != null) {
         registration.unregister();
      }

      if (messaging != null) {
         messaging.dispose();
      }
   }

   public static Activator getInstance() {
      return me;
   }

   public BundleContext getContext() {
      return context;
   }
}
