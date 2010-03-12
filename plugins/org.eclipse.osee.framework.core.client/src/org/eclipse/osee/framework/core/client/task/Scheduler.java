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
package org.eclipse.osee.framework.core.client.task;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Roberto E. Escobar
 */
public class Scheduler {

   private static final InternalScheduler scheduler = new InternalScheduler();

   private Scheduler() {
   }

   public static void scheduleAtFixedRate(ScheduledTask command, long initialDelay, long period, TimeUnit unit) {
      scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
   }

   public static void scheduleWithFixedDelay(ScheduledTask command, long initialDelay, long delay, TimeUnit unit) {
      scheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
   }

   public static void cancelTask(ScheduledTask command) {
      scheduler.cancelTask(command);
   }

   public static void shutdown() {
      scheduler.shutdown();
   }

   private static final class InternalScheduler {
      private final Map<Runnable, ScheduledFuture<?>> futures;
      private final ScheduledExecutorService executor;

      public InternalScheduler() {
         futures = Collections.synchronizedMap(new HashMap<Runnable, ScheduledFuture<?>>());
         executor = Executors.newSingleThreadScheduledExecutor(new SchedulerThreadFactory());
      }

      public void scheduleAtFixedRate(ScheduledTask command, long initialDelay, long period, TimeUnit unit) {
         ScheduledFuture<?> futureTask = executor.scheduleAtFixedRate(command, initialDelay, period, unit);
         futures.put(command, futureTask);
         command.setScheduledFuture(futureTask);
      }

      public void scheduleWithFixedDelay(ScheduledTask command, long initialDelay, long delay, TimeUnit unit) {
         ScheduledFuture<?> futureTask = executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
         futures.put(command, futureTask);
         command.setScheduledFuture(futureTask);
      }

      public void cancelTask(ScheduledTask command) {
         ScheduledFuture<?> future = futures.get(command);
         if (future != null) {
            if (future.cancel(true)) {
               futures.remove(command);
            }
         }
      }

      public void shutdown() {
         executor.shutdownNow();
      }
   }

   private static final class SchedulerThreadFactory implements ThreadFactory {

      @Override
      public Thread newThread(Runnable runnable) {
         return new Thread(((ScheduledTask) runnable).getName());
      }

   }
}
