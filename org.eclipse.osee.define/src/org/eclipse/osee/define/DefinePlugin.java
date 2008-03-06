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
package org.eclipse.osee.define;

import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

public class DefinePlugin extends OseeUiActivator {
   private static DefinePlugin pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "org.eclipse.osee.define";
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(DefinePlugin.class);

   public DefinePlugin() {
      super();
      pluginInstance = this;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.plugin.OseePlugin#getPluginName()
    */
   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   /**
    * Returns the shared instance.
    */
   public static DefinePlugin getInstance() {
      return pluginInstance;
   }

   /**
    * @return the logger
    */
   public static Logger getLogger() {
      return logger;
   }
}