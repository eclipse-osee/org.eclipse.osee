/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.internal;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan D. Brooks
 */
public final class ActivityMonitorImpl implements ActivityMonitor {

   private final ConcurrentHashMap<Thread, Object[]> threadToUser = new ConcurrentHashMap<Thread, Object[]>();

   @Override
   public Object[] getThreadRootEntry() {
      return threadToUser.get(Thread.currentThread());
   }

   @Override
   public synchronized Iterable<Thread> getActiveThreads() {
      Set<Thread> threads = threadToUser.keySet();
      Iterator<Thread> threadIter = threads.iterator();
      while (threadIter.hasNext()) {
         if (!threadIter.next().isAlive()) {
            threadIter.remove();
         }
      }
      return threads;
   }

   @Override
   public void addActivityThread(Object[] activityEntry) {
      threadToUser.put(Thread.currentThread(), activityEntry);
   }

   @Override
   public void removeActivityThread() {
      threadToUser.remove(Thread.currentThread());
   }
}