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
package org.eclipse.osee.framework.ui.service.control;

import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Roberto E. Escobar
 */
public class ControlPlugin extends OseeUiActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.service.control";
   private static ControlPlugin pluginInstance; // The shared instance.
   private ServiceTracker tracker;

   /**
    * The constructor.
    */
   public ControlPlugin() {
      pluginInstance = this;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      tracker = new ServiceTracker(context, IWorkbenchUserService.class.getName(), null);
      tracker.open();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (tracker != null) {
         tracker.close();
      }
      super.stop(context);
   }

   /**
    * Returns the shared instance.
    */
   public static ControlPlugin getInstance() {
      return pluginInstance;
   }

   public IWorkbenchUserService getDirectoryService() {
      return (IWorkbenchUserService) tracker.getService();
   }
}
