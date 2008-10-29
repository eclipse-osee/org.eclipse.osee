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

   public String toString() {
      return name;
   }

   void setScheduledFuture(ScheduledFuture<ScheduledTask> futureTask) {
      if (!this.futureTask.equals(futureTask)) {
         this.futureTask = futureTask;
      }
   }
}
