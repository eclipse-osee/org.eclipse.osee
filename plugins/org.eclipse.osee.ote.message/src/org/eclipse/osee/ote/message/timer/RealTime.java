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
package org.eclipse.osee.ote.message.timer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TimerControl;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RealTime extends TimerControl {
   private final HashMap<EnvironmentTask, ScheduledFuture<?>> handleMap =
      new HashMap<EnvironmentTask, ScheduledFuture<?>>(32);

   /**
    * Constructor
    */
   public RealTime() {
      super((Runtime.getRuntime().availableProcessors() + 1) / 2 + 1);
   }

   @Override
   public void addTask(final EnvironmentTask task, final TestEnvironment environment) {
      if (!handleMap.containsKey(task)) {
         final WeakReference<TestEnvironment> te = new WeakReference<TestEnvironment>(environment);
         final ScheduledFuture<?> handle = schedulePeriodicTask(new Runnable() {
            final Benchmark bm = new Benchmark(task.getClass().getName() + ":" + task.getHzRate() + "Hz",
               (long) (1000000.0 / task.getHzRate()));

            @Override
            public void run() {

               try {
                  bm.samplePoint();
                  if (task.isRunning()) {
                     task.runOneCycle();
                  }
               } catch (Throwable ex) {
                  ScheduledFuture<?> h = handleMap.get(task);
                  if (h != null) {
                     h.cancel(false);
                  }
                  te.get().handleException(ex, "exception while running one cycle for task " + task.toString(),
                     Level.SEVERE, false);
               }
            }

         }, 0, (long) Math.rint(1000.0 / task.getHzRate()));
         handleMap.put(task, handle);
      }
   }

   @Override
   public void removeTask(EnvironmentTask task) {
      ScheduledFuture<?> handle = handleMap.remove(task);
      if (handle != null) {
         handle.cancel(false);
      }
   }

   @Override
   public long getEnvTime() {
      return System.currentTimeMillis();
   }

   @Override
   public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      synchronized (obj) {
         obj.wait(milliseconds);
      }
   }

   @Override
   public ICancelTimer setTimerFor(final ITimeout objToNotify, int milliseconds) {
      objToNotify.setTimeout(false);
      final ScheduledFuture<?> handle = scheduleOneShotTask(new Runnable() {

         @Override
         public void run() {
            synchronized (objToNotify) {
               objToNotify.setTimeout(true);
               objToNotify.notifyAll();
            }
         }
      }, milliseconds);

      return new ICancelTimer() {

         @Override
         public void cancelTimer() {
            handle.cancel(false);
         }
      };
   }

   @Override
   public int getCycleCount() {
      return (int) System.currentTimeMillis() / 20;
   }

   @Override
   public void incrementCycleCount() {
   }

   public List<CycleCountDown> getCycleCounters() {
      return null;
   }

   public void setCycleCounters(List<CycleCountDown> cycleCounters) {
   }

   @Override
   public void setCycleCount(int cycle) {
   }

   @Override
   public void dispose() {
   }

   @Override
   public void cancelAllTasks() {
      for (ScheduledFuture<?> handle : handleMap.values()) {
         handle.cancel(false);
      }
      handleMap.clear();
   }

   @Override
   public void step() {
   }

   @Override
   public long getTimeOfDay() {
      return getEnvTime();
   }
}