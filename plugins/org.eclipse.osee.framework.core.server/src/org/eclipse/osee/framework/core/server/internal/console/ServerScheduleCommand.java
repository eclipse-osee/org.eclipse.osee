/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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

/**
 * @author Ryan D. Brooks
 */
public class ServerScheduleCommand implements ConsoleCommand {

   @Override
   public String getName() {
      return "server_schedule_cmd";
   }

   @Override
   public String getDescription() {
      return "Schedules a console command";
   }

   @Override
   public String getUsage() {
      return "iterate=<NUMBER_OF_TIMES_TO_SCHEDULE> delay=<DELAY_IN_SECONDS> cmd=\"<CMD + PARAMETERS>\"";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new SchedulingCallable(console, params);
   }

   private final class SchedulingCallable implements Callable<Boolean> {
      private final Console ci;
      private final ConsoleParameters params;

      public SchedulingCallable(Console ci, ConsoleParameters params) {
         this.ci = ci;
         this.params = params;
      }

      @Override
      public Boolean call() throws Exception {
         long delayMiliseconds = params.getInt("delay") * 1000;
         int iterations = params.getInt("iterate");

         String command = params.get("cmd");

         for (int i = 0; i < iterations; i++) {
            Thread.sleep(delayMiliseconds);
            ci.execute(command);
         }
         return Boolean.TRUE;
      }
   }

}