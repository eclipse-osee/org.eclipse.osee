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
package org.eclipse.osee.framework.skynet.core.internal.event;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * @author Roberto E. Escobar
 */
public final class OseeEventThreadFactory implements ThreadFactory {
   private final List<WeakReference<Thread>> threads;
   private final String threadName;
   private final int priority;

   public OseeEventThreadFactory(String threadName, int priority) {
      this.threadName = threadName;
      this.threads = new CopyOnWriteArrayList<>();
      this.priority = priority;
   }

   public OseeEventThreadFactory(String name) {
      this(name, Thread.NORM_PRIORITY);
   }

   @Override
   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, threadName + ":" + threads.size());
      thread.setPriority(priority);
      this.threads.add(new WeakReference<>(thread));
      return thread;
   }
}