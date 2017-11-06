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
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public final class ActivityMonitor {

   private final ConcurrentHashMap<Thread, Object[]> threadToRootEntry = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<Long, Object[]> parentToRootEntry = new ConcurrentHashMap<>();
   private final Object[] defaultRootEntry;

   public ActivityMonitor(Object[] defaultRootEntry) {
      this.defaultRootEntry = defaultRootEntry;
      parentToRootEntry.put(Id.SENTINEL, defaultRootEntry);
   }

   public Object[] getDefaultRootEntry() {
      return defaultRootEntry;
   }

   public Object[] getThreadRootEntry() {
      Object[] threadRootEntry = threadToRootEntry.get(Thread.currentThread());
      if (threadRootEntry == null) {
         threadRootEntry = defaultRootEntry;
      }
      return threadRootEntry;
   }

   public Object[] getThreadRootEntry(Long parentId) {
      Object[] threadRootEntry = parentToRootEntry.get(parentId);
      if (threadRootEntry == null) {
         threadRootEntry = getThreadRootEntry();
      }
      return threadRootEntry;
   }

   public synchronized Iterable<Thread> getActiveThreads() {
      Set<Thread> threads = threadToRootEntry.keySet();
      Iterator<Thread> threadIter = threads.iterator();
      while (threadIter.hasNext()) {
         if (!threadIter.next().isAlive()) {
            threadIter.remove();
         }
      }
      return threads;
   }

   public void addActivityThread(Object[] activityEntry) {
      threadToRootEntry.put(Thread.currentThread(), activityEntry);
   }

   public void removeActivityThread() {
      threadToRootEntry.remove(Thread.currentThread());
   }
}