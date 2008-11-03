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
package org.eclipse.osee.framework.server.lookup.servlet;

import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.server.OseeHttpServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ServerLookupActivator implements BundleActivator {

   private OseeHttpServiceTracker httpTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      httpTracker =
            new OseeHttpServiceTracker(context, OseeServerContext.LOOKUP_CONTEXT, ServerLookupServlet.class);
      httpTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      httpTracker.close();
      httpTracker = null;
   }

}
