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
package org.eclipse.osee.ats.config.demo;

import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class OseeAtsConfigDemoPlugin extends OseeActivator {
   // The shared instance.
   private static OseeAtsConfigDemoPlugin plugin;
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeAtsConfigDemoPlugin.class);
   public static final String PLUGIN_ID = "org.eclipse.osee.ats.config.demo";

   /**
    * The constructor.
    */
   public OseeAtsConfigDemoPlugin() {
      super();
      plugin = this;
   }

   public static OseeAtsConfigDemoPlugin getInstance() {
      return plugin;
   }

   public static Logger getLogger() {
      return logger;
   }

}
