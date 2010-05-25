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
package org.eclipse.osee.ote.ui.message.internal;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.ote.service.IOteClientService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends OseeUiActivator {
   private static Activator pluginInstance;

   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui.message";

   private ServiceTracker oteClientServiceTracker;

   /**
    * Returns the shared instance.
    */
   public static Activator getDefault() {
      return pluginInstance;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      oteClientServiceTracker = new ServiceTracker(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();
      pluginInstance = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      oteClientServiceTracker.close();
      pluginInstance = null;
   }

   public IOteClientService getOteClientService() {
      return (IOteClientService) oteClientServiceTracker.getService();
   }
}