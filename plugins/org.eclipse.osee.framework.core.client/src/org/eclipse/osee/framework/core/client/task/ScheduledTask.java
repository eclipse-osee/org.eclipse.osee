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

import java.util.concurrent.ScheduledFuture;

/**
 * @author Roberto E. Escobar
 */
public abstract class ScheduledTask implements Runnable {
   private final String name;
   private ScheduledFuture<ScheduledTask> futureTask;

   protected ScheduledTask(String name) {
      this.name = name;
      this.futureTask = null;
   }

   public String getName() {
      return name;
   }

   public final void run() {
      try {
         innerRun();
      } catch (Throwable th) {
         th.printStackTrace();
      }
   }

   @SuppressWarnings("unchecked")
   void setScheduledFuture(ScheduledFuture<?> futureTask) {
      this.futureTask = (ScheduledFuture<ScheduledTask>) futureTask;
   }

   protected ScheduledFuture<ScheduledTask> getFutureTask() {
      return futureTask;
   }

   protected abstract void innerRun() throws Exception;

}
