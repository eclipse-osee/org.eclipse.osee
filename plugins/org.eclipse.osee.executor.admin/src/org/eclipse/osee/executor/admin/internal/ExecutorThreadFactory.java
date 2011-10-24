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

import java.lang.Thread.State;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * @author Roberto E. Escobar
 */
public class ExecutorThreadFactory implements ThreadFactory {

   private final List<WeakReference<Thread>> threads;
   private final String threadName;
   private final int priority;

   public ExecutorThreadFactory(String threadName, int priority) {
      this.threadName = threadName;
      this.threads = new CopyOnWriteArrayList<WeakReference<Thread>>();
      this.priority = priority;
   }

   public ExecutorThreadFactory(String name) {
      this(name, Thread.NORM_PRIORITY);
   }

   @Override
   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable);

      String name = String.format("%s: %s", threadName, threads.size());
      thread.setName(name);

      int priorityToSet = priority;
      if (priorityToSet > Thread.MAX_PRIORITY) {
         priorityToSet = Thread.MAX_PRIORITY;
      } else if (priorityToSet < Thread.MIN_PRIORITY) {
         priorityToSet = Thread.MIN_PRIORITY;
      }
      thread.setPriority(priority);

      threads.add(new WeakReference<Thread>(thread));
      return thread;
   }

   public List<Thread> getThreads() {
      List<Thread> toReturn = new ArrayList<Thread>();
      for (WeakReference<Thread> weak : threads) {
         Thread thread = weak.get();
         if (thread != null) {
            toReturn.add(thread);
         }
      }
      return toReturn;
   }

   public synchronized void cleanUp() {
      Set<WeakReference<Thread>> toRemove = new HashSet<WeakReference<Thread>>();
      for (WeakReference<Thread> reference : threads) {
         Thread thread = reference.get();
         if (thread == null || State.TERMINATED == thread.getState()) {
            toRemove.add(reference);
         }
      }
      threads.removeAll(toRemove);
   }

}
