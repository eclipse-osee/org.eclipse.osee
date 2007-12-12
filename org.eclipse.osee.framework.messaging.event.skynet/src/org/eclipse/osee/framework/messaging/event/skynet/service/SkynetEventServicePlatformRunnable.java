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
package org.eclipse.osee.framework.messaging.event.skynet.service;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.jdk.core.util.CmdLineArgs;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.HeadlessEclipseConfigurationFactory;

/**
 * @author Robert A. Fisher
 */
public class SkynetEventServicePlatformRunnable implements IApplication {

   private static final Logger logger =
         ConfigUtil.getConfigFactory().getLogger(SkynetEventServicePlatformRunnable.class);
   private SkynetEventService skynetEventService;

   public SkynetEventServicePlatformRunnable() {
      super();
      skynetEventService = null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
    */
   public Object start(IApplicationContext context) throws Exception {
      System.setProperty(OseeProperties.OSEE_CONFIG_FACTORY, HeadlessEclipseConfigurationFactory.class.getName());

      CmdLineArgs commandArgs = new CmdLineArgs(Platform.getApplicationArgs());

      String database = commandArgs.get("-database");
      skynetEventService = new SkynetEventService(database);
      return EXIT_OK;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#stop()
    */
   public void stop() {
      try {
         skynetEventService.kill();
      } catch (RemoteException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }
}
