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
package org.eclipse.osee.framework.server.admin.management;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
class ServerShutdownWorker extends BaseServerCommand {

   protected ServerShutdownWorker(CommandInterpreter ci) {
      super("Server Shutdown", ci);
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      IApplicationServerManager manager = CoreServerActivator.getApplicationServerManager();

      Bundle equinoxHttpBundle = Platform.getBundle("org.eclipse.equinox.http.jetty");
      equinoxHttpBundle.stop();

      manager.setServletRequestsAllowed(false);

      while (!manager.isSystemIdle()) {
         try {
            Thread.sleep(5000);
         } catch (InterruptedException ex) {
            printStackTrace(ex);
         }
      }
      manager.shutdown();
      getCommandInterpreter().execute("close");
   }
}
