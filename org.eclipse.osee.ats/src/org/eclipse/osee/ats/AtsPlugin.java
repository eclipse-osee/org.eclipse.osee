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

package org.eclipse.osee.ats;

import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsPreSaveCacheRemoteEventHandler;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Donald G. Dunne
 */
public class AtsPlugin extends OseeUiActivator {
   private static AtsPlugin pluginInstance;
   public static final String PLUGIN_ID = "org.eclipse.osee.ats";

   public AtsPlugin() {
      super();
      pluginInstance = this;
      AtsPreSaveCacheRemoteEventHandler.getInstance();
      AtsCacheManager.start();
      AtsNotifyUsers.getInstance();
   }

   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   public static AtsPlugin getInstance() {
      return pluginInstance;
   }

}
