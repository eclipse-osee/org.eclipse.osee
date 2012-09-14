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

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.server.IServerTaskScheduler;
import org.eclipse.osee.framework.core.server.ServerTaskInfo;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class ServerTaskStatsCommand implements ConsoleCommand {

   private IServerTaskScheduler scheduler;

   public void setTaskScheduler(IServerTaskScheduler scheduler) {
      this.scheduler = scheduler;
   }

   @Override
   public String getName() {
      return "server_task_status";
   }

   @Override
   public String getDescription() {
      return "Displays server scheduled task information";
   }

   @Override
   public String getUsage() {
      return "";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new SchedulingCallable(console, scheduler);
   }

   private final class SchedulingCallable implements Callable<Boolean> {
      private final Console ci;
      private final IServerTaskScheduler scheduler;

      public SchedulingCallable(Console ci, IServerTaskScheduler scheduler) {
         this.ci = ci;
         this.scheduler = scheduler;
      }

      @Override
      public Boolean call() throws Exception {
         List<ServerTaskInfo> infos = scheduler.getServerTaskInfo();
         int count = 0;
         for (ServerTaskInfo task : infos) {
            IStatus status = task.getLastStatus();

            ci.writeln();
            ci.writeln("Task:[%s][%s] Status:[%s] NextRunIn:[%s]", ++count, task.getName(), task.getTaskState(),
               Lib.asTimeString(task.getTimeUntilNextRun()));
            ci.writeln("Scheme:[%s]", task.getSchedulingScheme());
            ci.writeln("Period:[%s %s]", task.getPeriod(), task.getTimeUnit().toString().toLowerCase());
            ci.write("\tLast Run Status:[");
            int severity = status.getSeverity();
            switch (severity) {
               case IStatus.OK:
                  ci.write("OK");
                  break;
               case IStatus.ERROR:
                  ci.write("ERROR");
                  break;
               case IStatus.WARNING:
                  ci.write("WARNING");
                  break;
               case IStatus.INFO:
                  ci.write("INFO");
                  break;
               case IStatus.CANCEL:
                  ci.write("CANCEL");
                  break;
               default:
                  ci.write(severity);
                  break;
            }
            ci.writeln("] Code:[%s]", status.getCode());
            String message = status.getMessage();
            if (Strings.isValid(message)) {
               ci.write("\tMessage:%s", message.replaceAll("\n", "\n\t"));
            }
            Throwable th = status.getException();
            if (th != null) {
               ci.write("\tException: [%s]", Lib.exceptionToString(th).replaceAll("\n", "\n\t"));
            }
            ci.writeln();

         }
         return Boolean.TRUE;
      }
   }

}