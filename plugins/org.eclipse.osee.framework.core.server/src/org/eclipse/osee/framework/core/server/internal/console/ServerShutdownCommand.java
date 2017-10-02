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
package org.eclipse.osee.framework.core.server.internal.console;

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Roberto E. Escobar
 */
public class ServerShutdownCommand implements ConsoleCommand {

   private IApplicationServerManager appManager;

   public void setApplicationServerManager(IApplicationServerManager appManager) {
      this.appManager = appManager;
   }

   private IApplicationServerManager getApplicationServerManager() {
      return appManager;
   }

   @Override
   public String getName() {
      return "server_shutdown";
   }

   @Override
   public String getDescription() {
      return "Shutdown server instance";
   }

   @Override
   public String getUsage() {
      return "[oseeOnly=<TRUE|FALSE>] - To shutdown only the osee server instance without shutting down OSGI framework\n" + //
         "                        - Shutsdown both osee server instance and OSGI framework\n";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new ServerShutdownCallable(getApplicationServerManager(), console, params);
   }

   private static final class ServerShutdownCallable implements Callable<Boolean> {
      private final Console console;
      private final ConsoleParameters parameters;

      private final IApplicationServerManager manager;

      public ServerShutdownCallable(IApplicationServerManager manager, Console console, ConsoleParameters parameters) {
         this.manager = manager;
         this.console = console;
         this.parameters = parameters;
      }

      @Override
      public Boolean call() throws Exception {
         while (!manager.isSystemIdle()) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex) {
               console.write(ex);
            }
         }
         manager.shutdown();

         if (parameters.getBoolean("oseeOnly")) {
            console.writeln("Osee Shutdown Complete");
         } else {
            // Stop OSGI
            Bundle thisBundle = FrameworkUtil.getBundle(getClass());
            Bundle frameworkBundle = thisBundle.getBundleContext().getBundle(0);
            frameworkBundle.stop();
         }
         return Boolean.TRUE;
      }
   }

}