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

import java.util.Arrays;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;

/**
 * @author Roberto E. Escobar
 */
public class ServerVersionCommand implements ConsoleCommand {

   private IApplicationServerManager appManager;

   public void setApplicationServerManager(IApplicationServerManager appManager) {
      this.appManager = appManager;
   }

   private IApplicationServerManager getApplicationServerManager() {
      return appManager;
   }

   @Override
   public String getName() {
      return "server_version";
   }

   @Override
   public String getDescription() {
      return "Adds/Removes/Lists supported versions from this osee server instance";
   }

   @Override
   public String getUsage() {
      return "[add=<VERSIONS>] [remove=<VERSIONS>]";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new ServerVersionCallable(getApplicationServerManager(), console, params);
   }

   private static final class ServerVersionCallable implements Callable<Boolean> {
      private final Console console;
      private final ConsoleParameters parameters;

      private final IApplicationServerManager appManager;

      public ServerVersionCallable(IApplicationServerManager appManager, Console console, ConsoleParameters parameters) {
         this.appManager = appManager;
         this.console = console;
         this.parameters = parameters;
      }

      @Override
      public Boolean call() throws Exception {
         String[] toAdd = parameters.getArray("add");
         String[] toRemove = parameters.getArray("remove");

         if (toAdd != null && toAdd.length > 0) {
            for (String version : toAdd) {
               appManager.addSupportedVersion(version);
            }
         }

         if (toRemove != null && toRemove.length > 0) {
            for (String version : toRemove) {
               appManager.removeSupportedVersion(version);
            }
         }
         console.writeln("Osee Application Server: %s", Arrays.deepToString(appManager.getSupportedVersions()));
         return Boolean.TRUE;
      }
   }

}