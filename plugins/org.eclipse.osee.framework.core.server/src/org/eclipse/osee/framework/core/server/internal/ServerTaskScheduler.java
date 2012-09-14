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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IServerTask;
import org.eclipse.osee.framework.core.server.IServerTaskScheduler;
import org.eclipse.osee.framework.core.server.SchedulingScheme;
import org.eclipse.osee.framework.core.server.ServerTaskInfo;
import org.eclipse.osee.framework.core.server.ServerTaskInfo.TaskState;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ServerTaskScheduler implements IServerTaskScheduler {

   private Log logger;

   private final Map<IServerTask, ScheduledFuture<?>> futures =
      new ConcurrentHashMap<IServerTask, ScheduledFuture<?>>();

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

   private void scheduleAtFixedRate(IServerTask command, long initialDelay, long period, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.scheduleAtFixedRate(command, initialDelay, period, unit);
      futures.put(command, futureTask);
   }

   private void scheduleWithFixedDelay(IServerTask command, long initialDelay, long delay, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
      futures.put(command, futureTask);
   }

   private void scheduleOneShot(IServerTask command, long initialDelay, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.schedule(command, initialDelay, unit);
      futures.put(command, futureTask);
   }

   @Override
   public List<ServerTaskInfo> getServerTaskInfo() {
      List<ServerTaskInfo> infos = new ArrayList<ServerTaskInfo>();

      for (Entry<IServerTask, ScheduledFuture<?>> entry : futures.entrySet()) {
         IServerTask task = entry.getKey();

         ScheduledFuture<?> future = entry.getValue();
         long waitTimeForNextRun = future.getDelay(TimeUnit.MILLISECONDS);

         TaskState state = TaskState.SCHEDULED;
         if (waitTimeForNextRun == 0 && (!future.isDone() || !future.isCancelled())) {
            state = TaskState.RUNNING;
         } else if (future.isCancelled()) {
            state = TaskState.CANCELLED;
         } else if (waitTimeForNextRun > 0) {
            state = TaskState.WAITING;
         } else if (future.isDone()) {
            state = TaskState.DONE;
         }

         ServerTaskInfo info = new ServerTaskInfoImpl(task, state, waitTimeForNextRun);
         infos.add(info);
      }
      return infos;
   }
   private final static class ServerTaskInfoImpl implements ServerTaskInfo {

      private final IServerTask task;
      private final TaskState state;
      private final long timeTilNextRun;

      public ServerTaskInfoImpl(IServerTask task, TaskState state, long timeTilNextRun) {
         super();
         this.task = task;
         this.state = state;
         this.timeTilNextRun = timeTilNextRun;
      }

      @Override
      public String getName() {
         return task.getName();
      }

      @Override
      public SchedulingScheme getSchedulingScheme() {
         return task.getSchedulingScheme();
      }

      @Override
      public long getInitialDelay() {
         return task.getInitialDelay();
      }

      @Override
      public long getPeriod() {
         return task.getPeriod();
      }

      @Override
      public long getTimeUntilNextRun() {
         return timeTilNextRun;
      }

      @Override
      public TimeUnit getTimeUnit() {
         return task.getTimeUnit();
      }

      @Override
      public IStatus getLastStatus() {
         return task.getLastStatus();
      }

      @Override
      public TaskState getTaskState() {
         return state;
      }

   }
}
