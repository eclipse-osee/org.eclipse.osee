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
package org.eclipse.osee.executor.admin.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorThreadFactory implements ThreadFactory {

   private final String threadName;
   private final int priority;
   private final AtomicInteger threadCount = new AtomicInteger(0);

   public ExecutorThreadFactory(String threadName, int priority) {
      this.threadName = threadName;
      this.priority = priority;
   }

   public ExecutorThreadFactory(String name) {
      this(name, Thread.NORM_PRIORITY);
   }

   @Override
   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable);

      String name = String.format("%s: %s", threadName, threadCount.getAndAdd(1));
      thread.setName(name);

      int priorityToSet = priority;
      if (priorityToSet > Thread.MAX_PRIORITY) {
         priorityToSet = Thread.MAX_PRIORITY;
      } else if (priorityToSet < Thread.MIN_PRIORITY) {
         priorityToSet = Thread.MIN_PRIORITY;
      }
      thread.setPriority(priority);

      return thread;
   }

}
