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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TimerControl;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

/**
 * We use a frequency resolution of 300hz.
 * 
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class SimulatedTime extends TimerControl {

   private static final class Task {
      private final EnvironmentTask task;
      private final TestEnvironment env;

      public Task(EnvironmentTask task, TestEnvironment env) {
         super();
         this.task = task;
         this.env = env;
      }

      public void doTask(int cycleCount) {
         try {
            task.baseRunOneCycle(cycleCount);
         } catch (Throwable ex) {
            OseeLog.log(MessageSystemTestEnvironment.class,
                  Level.SEVERE, "Aborting the test script because an Environment Task is failing", ex);
            env.getRunManager().abort(ex, false);
         }
      }
   }
   private final Collection<CycleCountDown> cycleCounters;
   private IScriptControl scriptControl;
   private int cycleCount;
   private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<Task>();

   /**
    * @param scriptControl - 
    * @throws IOException
    */
   public SimulatedTime(IScriptControl scriptControl) throws IOException {
      super(3);
      this.scriptControl = scriptControl;
      this.cycleCounters = new HashSet<CycleCountDown>(32);
      cycleCount = 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.test.core.environment.TestEnvironment#getEnvTime()
    */
   public long getEnvTime() {
      return (long) (cycleCount * (1000.0 / EnvironmentTask.cycleResolution));
   }

   public ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds) {
      try {
    	 
         scriptControl.unlock();
      } catch (IllegalMonitorStateException ex) {
         if (!Thread.currentThread().getName().contains("(JSK) mux request dispatch") || !Thread.currentThread().getName().contains(
               "(JSK) Mux request dispatch")) OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex.getMessage(), ex);
      }
      CycleCountDown cycleCountDown =
            new CycleCountDown(scriptControl, objToNotify,
                  ((int) Math.rint(milliseconds / (1000.0 / EnvironmentTask.cycleResolution))) - 1);
      synchronized (cycleCounters) {
         cycleCounters.add(cycleCountDown);
      }
      return cycleCountDown;
   }

   public void addTask(EnvironmentTask task, TestEnvironment environment) {
      if (!tasks.contains(task)) {
         tasks.add(new Task(task, environment));
      }
   }

   public void removeTask(EnvironmentTask task) {
      Task itemToRemove = null;
      for (Task t : tasks) {
         if (t.task == task) {
            itemToRemove = t;
            break;
         }
      }
      if (itemToRemove != null) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.FINE,
               "removing environment task " + task.toString());
         tasks.remove(itemToRemove);
      }
   }

   public void step() {

      for (Task t : tasks) {
         t.doTask(cycleCount);
      }
      incrementCycleCount();
   }

   public int getCycleCount() {
      return cycleCount;
   }

   public Collection<CycleCountDown> getCycleCounters() {
      return cycleCounters;
   }

   public void incrementCycleCount() {
      cycleCount++;
   }

   public void setCycleCount(int cycle) {
      cycleCount = cycle;
   }

   public void cancelAllTasks() {
      for (Task t : tasks) {
         t.task.cancel();
      }
      tasks.clear();
   }

   public void removeOccurredCycleCounters() {
      synchronized (cycleCounters) {
         Iterator<CycleCountDown> iter = cycleCounters.iterator();
         while (iter.hasNext()) {
            CycleCountDown counter = iter.next();
            if (counter.cycleOccurred()) {
               iter.remove();
            }
         }
      }
   }

   public void dispose() {
      cycleCounters.clear();
      tasks.clear();
   }
}