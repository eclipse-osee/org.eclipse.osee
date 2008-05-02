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
package org.eclipse.osee.framework.derby;

import org.eclipse.osee.framework.db.connection.IConnection;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class Activator implements BundleActivator {

   private ServiceRegistration derbyClientConnectionService;
   private ServiceRegistration embeddedDerbyConnectionService;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      derbyClientConnectionService =
            context.registerService(IConnection.class.getName(), new DerbyClientConnection(), null);
      embeddedDerbyConnectionService =
            context.registerService(IConnection.class.getName(), new EmbeddedDerbyConnection(), null);
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      derbyClientConnectionService.unregister();
      embeddedDerbyConnectionService.unregister();
   }

}
