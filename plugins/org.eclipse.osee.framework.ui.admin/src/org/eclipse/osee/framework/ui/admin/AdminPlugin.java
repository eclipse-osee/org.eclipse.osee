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
package org.eclipse.osee.framework.ui.admin;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Jeff C. Phillips
 */
public class AdminPlugin extends OseeUiActivator {

   // The shared instance.
   private static AdminPlugin pluginInstance;

   /**
    * The constructor.
    */
   public AdminPlugin() {
      pluginInstance = this;
   }

   /**
    * This method is called upon plug-in activation
    */
   public void start(BundleContext context) throws Exception {
      super.start(context);
   }

   /**
    * Returns the shared instance.
    */
   public static AdminPlugin getDefault() {
      return pluginInstance;
   }

   /**
    * Returns the shared instance.
    */
   public static AdminPlugin getInstance() {
      return pluginInstance;
   }
}
