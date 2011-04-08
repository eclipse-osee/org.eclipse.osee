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
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class ServerShutdownOperation extends AbstractOperation {
   private final CommandInterpreter ci;

   public ServerShutdownOperation(OperationLogger logger, CommandInterpreter ci) {
      super("Server Shutdown", Activator.PLUGIN_ID, logger);
      this.ci = ci;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IApplicationServerManager manager = Activator.getApplicationServerManager();

      Bundle equinoxHttpBundle = Platform.getBundle("org.eclipse.equinox.http.jetty");
      equinoxHttpBundle.stop();

      manager.setServletRequestsAllowed(false);

      while (!manager.isSystemIdle()) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ex) {
            log(ex);
         }
      }
      manager.shutdown();

      if ("-oseeOnly".equalsIgnoreCase(ci.nextArgument())) {
         log("Osee Shutdown Complete");
      } else {
         ci.execute("close");
      }
   }
}