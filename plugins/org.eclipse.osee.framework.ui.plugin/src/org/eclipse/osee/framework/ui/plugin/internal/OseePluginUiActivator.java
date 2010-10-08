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
package org.eclipse.osee.framework.ui.plugin.internal;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.plugin.core.IActionReportingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * a The activator class controls the plug-in life cycle
 */
public class OseePluginUiActivator implements BundleActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.plugin";

   private static OseePluginUiActivator plugin;
   private ServiceTracker tracker;

   @Override
   public void start(BundleContext context) throws Exception {
      tracker = new ServiceTracker(context, IActionReportingService.class.getName(), null);
      tracker.open();
      plugin = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (tracker != null) {
         tracker.close();
      }
      tracker = null;
      plugin = null;
      context = null;
   }

   public static OseePluginUiActivator getInstance() {
      return plugin;
   }

   public IActionReportingService getActionReportingService() throws OseeCoreException {
      IActionReportingService service = null;
      try {
         service = (IActionReportingService) tracker.waitForService(3000);
      } catch (InterruptedException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return service;
   }
}