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
package org.eclipse.osee.ote.ui.host.cmd;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class UiPlugin implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.ui.host.cmd";
   private static UiPlugin plugin;

   public static UiPlugin getInstance() {
      return plugin;
   }

   @Override
   public void start(BundleContext context) {
      plugin = this;
   }

   @Override
   public void stop(BundleContext context) {
      // method overridden only to satisfy its defining interface
   }
}