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

import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Roberto E. Escobar
 */
public class ProgressMonitorTask extends ScheduledTask {
   private final IProgressMonitor monitor;
   private final Statement statement;

   private ProgressMonitorTask(IProgressMonitor monitor, Statement statement, String name) {
      super(name);
      this.monitor = monitor;
      this.statement = statement;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.client.task.ScheduledTask#innerRun()
    */
   @Override
   protected void innerRun() throws Exception {
      if (monitor != null) {
         if (monitor.isCanceled()) {
            if (statement != null) {
               statement.cancel();
            }
            unscheduleTask();
         }
      } else {
         unscheduleTask();
      }
   }

   private void unscheduleTask() {
      Thread stopThread = new Thread() {
         public void run() {
            Scheduler.cancelTask(ProgressMonitorTask.this);
         }
      };
      stopThread.start();
   }

   public static ScheduledTask monitor(String name, IProgressMonitor monitor, Statement statement, long millis) {
      ProgressMonitorTask task = new ProgressMonitorTask(monitor, statement, name);
      Scheduler.scheduleAtFixedRate(task, 0, millis, TimeUnit.MILLISECONDS);
      return task;
   }
}