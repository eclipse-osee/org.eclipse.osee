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
package org.eclipse.osee.ats.config.demo.internal;

import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OseeAtsConfigDemoActivator extends OseeActivator {
   // The shared instance.
   private static OseeAtsConfigDemoActivator plugin;
   public static final String PLUGIN_ID = "org.eclipse.osee.ats.config.demo";

   /**
    * The constructor.
    */
   public OseeAtsConfigDemoActivator() {
      super();
      plugin = this;
   }

   public static OseeAtsConfigDemoActivator getInstance() {
      return plugin;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.plugin.core.OseeActivator#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
   }

}
