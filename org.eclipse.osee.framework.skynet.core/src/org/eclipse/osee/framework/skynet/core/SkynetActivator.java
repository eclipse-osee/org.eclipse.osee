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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.skynet.core.attribute.HttpAttributeTagger;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class
 * 
 * @author Robert A. Fisher
 */
public class SkynetActivator extends OseeUiActivator {
   private static SkynetActivator pluginInstance;
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";
   public static final String AUTO_TAG_KEY = "osee.auto.tag";
   private Job job;

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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.OseeUiActivator#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      ClientSessionManager.class.getCanonicalName();
      HttpAttributeTagger.getInstance();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      HttpAttributeTagger.getInstance().deregisterFromEventManager();
      if (job != null && job.getThread().isAlive()) job.getThread().interrupt();
      super.stop(context);
   }
}
