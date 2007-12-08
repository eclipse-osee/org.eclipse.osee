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
package org.eclipse.osee.framework.plugin.core;

/**
 * The activator class controls the plug-in life cycle
 */
public class PluginCoreActivator extends OseeActivator {
   private static PluginCoreActivator pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "osee.plugin.core";

   public PluginCoreActivator() {
      super();
      pluginInstance = this;
   }

   /**
    * Returns the shared instance.
    */
   public static PluginCoreActivator getInstance() {
      return pluginInstance;
   }
}