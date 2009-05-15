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

import java.util.Hashtable;
import org.eclipse.osee.framework.messaging.MessagingGateway;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Andrew M. Finkbeiner
 */
public class Activator implements BundleActivator {

   private ServiceRegistration registration;
   private MessagingGatewayImpl messaging;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      messaging = new MessagingGatewayImpl();
      registration = context.registerService(MessagingGateway.class.getName(), messaging, new Hashtable());
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      if (registration != null) {
         registration.unregister();
      }

      if (messaging != null) {
         messaging.dispose();
      }
   }
}
