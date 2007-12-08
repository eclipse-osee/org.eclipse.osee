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
package org.eclipse.osee.framework.messaging.event.skynet;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * The main plugin class to be used in the desktop.
 */
public class SkynetEventPlugin extends OseeUiActivator {

   private static SkynetEventPlugin pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.messaging.event.skynet";

   public SkynetEventPlugin() {
      super();
      pluginInstance = this;
   }

   /**
    * Returns the shared instance.
    */
   public static SkynetEventPlugin getInstance() {
      return pluginInstance;
   }
}