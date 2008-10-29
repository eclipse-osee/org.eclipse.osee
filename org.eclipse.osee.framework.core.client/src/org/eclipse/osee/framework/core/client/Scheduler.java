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
package org.eclipse.osee.framework.core.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Roberto E. Escobar
 */
public class Scheduler {

   private final static Scheduler instance = new Scheduler();

   private final Map<Runnable, ScheduledFuture<?>> futures;
   private final ScheduledExecutorService executor;

   private Scheduler() {
      futures = Collections.synchronizedMap(new HashMap<Runnable, ScheduledFuture<?>>());
      executor = Executors.newSingleThreadScheduledExecutor();
   }

   public static Scheduler getInstance() {
      return instance;
   }

   public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.scheduleAtFixedRate(command, initialDelay, period, unit);
      futures.put(command, futureTask);
   }

   public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      ScheduledFuture<?> futureTask = executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
      futures.put(command, futureTask);
   }

   public void cancelTask(Runnable command) {
      ScheduledFuture<?> future = futures.get(command);
      if (future != null) {
         future.cancel(true);
      }
   }

   public void shutdown() {
      executor.shutdownNow();
   }
}
