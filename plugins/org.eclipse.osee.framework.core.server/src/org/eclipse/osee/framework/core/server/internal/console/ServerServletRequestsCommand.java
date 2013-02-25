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

/**
 * @author Roberto E. Escobar
 */
public class ServerServletRequestsCommand implements ConsoleCommand {

   private IApplicationServerManager appManager;

   public void setApplicationServerManager(IApplicationServerManager appManager) {
      this.appManager = appManager;
   }

   private IApplicationServerManager getApplicationServerManager() {
      return appManager;
   }

   @Override
   public String getName() {
      return "server_servlet_requests";
   }

   @Override
   public String getDescription() {
      return "Set servlets to accept/reject requests";
   }

   @Override
   public String getUsage() {
      return "[accept=<TRUE|FALSE>] - Accept/Reject servlet requests\n" + //
      "                      - To display whether server is accepting servlet requests\n";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new ServerServletRequestsCallable(getApplicationServerManager(), console, params);
   }

   private static final class ServerServletRequestsCallable implements Callable<Boolean> {
      private final Console console;
      private final ConsoleParameters parameters;

      private final IApplicationServerManager appManager;

      public ServerServletRequestsCallable(IApplicationServerManager appManager, Console console, ConsoleParameters parameters) {
         this.appManager = appManager;
         this.console = console;
         this.parameters = parameters;
      }

      @Override
      public Boolean call() throws Exception {
         if (parameters.exists("accept")) {
            boolean value = parameters.getBoolean("accept");
            appManager.setServletRequestsAllowed(value);
         }
         console.writeln("Osee Application Server: [%s] - servlet requests",
            appManager.isAcceptingRequests() ? "ACCEPTING" : "REJECTING");
         return Boolean.TRUE;
      }
   }

}