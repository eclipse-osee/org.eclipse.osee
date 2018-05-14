/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.executor.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan D. Brooks
 */
public class OseeThreadFactory implements ThreadFactory {
   private final ThreadGroup threadGroup = new ThreadGroup("OSEE Threads");
   private final AtomicInteger threadNumber = new AtomicInteger(1);

   @Override
   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(threadGroup, runnable, "OSEE thread " + threadNumber.getAndIncrement() + " - idle");
      return thread;
   }
}