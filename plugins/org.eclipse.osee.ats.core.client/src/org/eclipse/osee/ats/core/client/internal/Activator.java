/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager.ConfigurationProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.INotificationManager;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.ats.core.client";

   @Override
   public void start(BundleContext bundleContext) throws Exception {
      AtsNotificationManager.setConfigurationProvider(new ConfigurationProvider() {

         @Override
         public INotificationManager getNotificationManager() {
            return OseeNotificationManager.getInstance();
         }

         @Override
         public boolean isProduction() throws OseeCoreException {
            return ClientSessionManager.isProductionDataStore();
         }
      });
   }

   @Override
   public void stop(BundleContext bundleContext) throws Exception {
      //
   }

}
