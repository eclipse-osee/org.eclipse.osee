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
package org.eclipse.osee.framework.skynet.core;

import java.util.logging.Logger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.OseeInfo;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class to be used in the desktop.
 * 
 * @author Robert A. Fisher
 */
public class SkynetActivator extends OseeUiActivator {
   private static SkynetActivator pluginInstance;
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";
   public static final String AUTO_TAG_KEY = "osee.auto.tag";
   private Job job;
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetActivator.class);

   public SkynetActivator() {
      super();
      if (pluginInstance == null) pluginInstance = this;
   }

   /**
    * Returns the shared instance.
    */
   public static SkynetActivator getInstance() {
      return pluginInstance;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      if (job != null && job.getThread().isAlive()) job.getThread().interrupt();
   }

   public static Logger getLogger() {
      return logger;
   }

   public boolean isAutoTaggingEnabled() {
      String propertyValue = System.getProperty(AUTO_TAG_KEY, null);
      if (propertyValue != null) {
         return Boolean.parseBoolean(propertyValue);
      }
      return Boolean.parseBoolean(OseeInfo.getValue(SkynetActivator.AUTO_TAG_KEY));
   }

   public static void setAutoTaggingEnabled(boolean enabled) {
      OseeInfo.putValue(SkynetActivator.AUTO_TAG_KEY, String.valueOf(enabled));
   }
}
