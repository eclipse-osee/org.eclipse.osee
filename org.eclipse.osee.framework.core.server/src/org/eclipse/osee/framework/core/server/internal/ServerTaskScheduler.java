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
package org.eclipse.osee.framework.core.server.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IServerTask;
import org.eclipse.osee.framework.core.server.IServerTaskScheduler;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ServerTaskScheduler implements IServerTaskScheduler {
   private final Map<Runnable, ScheduledFuture<?>> futures;
   private final ScheduledExecutorService executor;

   public ServerTaskScheduler() {
      futures = Collections.synchronizedMap(new HashMap<Runnable, ScheduledFuture<?>>());
      executor =
            Executors.newSingleThreadScheduledExecutor(CoreServerActivator.createNewThreadFactory("Osee Task Scheduler"));
   }

   public void addServerTask(IServerTask taskProvider) {
      if (taskProvider != null) {
         OseeLog.log(CoreServerActivator.class, Level.INFO, "Adding task: " + taskProvider.getName());
         switch (taskProvider.getSchedulingScheme()) {
            case ONE_SHOT:
               scheduleOneShot(taskProvider, taskProvider.getInitialDelay(), taskProvider.getTimeUnit());
               break;
            case FIXED_DELAY_BETWEEN_RUNS:
               scheduleWithFixedDelay(taskProvider, taskProvider.getInitialDelay(), taskProvider.getPeriod(),
                     taskProvider.getTimeUnit());
               break;
            case FIXED_RATE:
               scheduleAtFixedRate(taskProvider, taskProvider.getInitialDelay(), taskProvider.getPeriod(),
                     taskProvider.getTimeUnit());
               break;
            default:
               break;
         }
      }
   }

   public void removeServerTask(IServerTask taskProvider) {
      if (taskProvider != null) {
         OseeLog.log(CoreServerActivator.class, Level.INFO, "Removing task: " + taskProvider.getName());
         ScheduledFuture<?> future = futures.get(taskProvider);
         if (future != null) {
            future.cancel(true);
            futures.remove(future);
         }
      }
   }

   private void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.scheduleAtFixedRate(command, initialDelay, period, unit);
      futures.put(command, futureTask);
   }

   private void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
      futures.put(command, futureTask);
   }

   private void scheduleOneShot(Runnable command, long initialDelay, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.schedule(command, initialDelay, unit);
      futures.put(command, futureTask);
   }
}
