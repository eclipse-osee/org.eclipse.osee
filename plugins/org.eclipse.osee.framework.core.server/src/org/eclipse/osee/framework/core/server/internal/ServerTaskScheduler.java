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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IServerTask;
import org.eclipse.osee.framework.core.server.IServerTaskScheduler;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ServerTaskScheduler implements IServerTaskScheduler {

   private Log logger;

   private final Map<Runnable, ScheduledFuture<?>> futures = new ConcurrentHashMap<Runnable, ScheduledFuture<?>>();

   private ScheduledExecutorService executor;
   private IApplicationServerManager serverManager;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setServerManager(IApplicationServerManager serverManager) {
      this.serverManager = serverManager;
   }

   public void start() {
      ThreadFactory factory = serverManager.createNewThreadFactory("Osee Task Scheduler", Thread.NORM_PRIORITY);
      executor = Executors.newSingleThreadScheduledExecutor(factory);
   }

   public void stop() {
      if (executor != null) {
         executor.shutdown();
      }
      futures.clear();
   }

   @Override
   public void addServerTask(IServerTask taskProvider) {
      if (taskProvider != null) {
         logger.info("Adding task: [%s]", taskProvider.getName());
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

   @Override
   public void removeServerTask(IServerTask taskProvider) {
      if (taskProvider != null) {
         logger.info("Removing task: [%s]", taskProvider.getName());
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
