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
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.jdk.core.util.CmdLineArgs;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.SkynetEventPlugin;

/**
 * @author Robert A. Fisher
 */
public class SkynetEventServicePlatformRunnable implements IApplication {
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
         OseeLog.log(SkynetEventPlugin.class, Level.SEVERE, ex);
      }
   }
}
