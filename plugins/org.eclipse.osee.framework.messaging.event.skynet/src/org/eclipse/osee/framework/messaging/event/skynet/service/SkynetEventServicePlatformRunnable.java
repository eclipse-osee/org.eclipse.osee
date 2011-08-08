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

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.jdk.core.util.CmdLineArgs;

/**
 * @author Robert A. Fisher
 */
public class SkynetEventServicePlatformRunnable implements IApplication {
   private SkynetEventService skynetEventService;

   public SkynetEventServicePlatformRunnable() {
      super();
      skynetEventService = null;
   }

   @Override
   public Object start(IApplicationContext context) throws Exception {
      CmdLineArgs commandArgs = new CmdLineArgs(Platform.getApplicationArgs());

      String database = commandArgs.get("-database");
      skynetEventService = new SkynetEventService(database);
      return EXIT_OK;
   }

   @Override
   public void stop() {
      skynetEventService.kill();
   }
}
