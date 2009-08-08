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
package org.eclipse.osee.framework.core.client;

import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class CoreClientActivator extends Plugin {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core.client";
   private static CoreClientActivator instance;
   private BundleContext context;

   public CoreClientActivator() {
      instance = this;
      context = null;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      this.context = context;
      HttpServer.startServer(1);
      context.addBundleListener(new BundleListener() {

         @Override
         public void bundleChanged(BundleEvent arg0) {
            Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.core.server");
            if (bundle != null && bundle.getState() == Bundle.ACTIVE) {
               try {
                  bundle.stop();
               } catch (BundleException ex) {
                  OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
               }
            }
         }

      });
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      HttpServer.stopServer();

      ClientSessionManager.releaseSession();
      context = null;
   }

   public static CoreClientActivator getInstance() {
      return instance;
   }

   public static BundleContext getBundleContext() {
      return getInstance().context;
   }
}