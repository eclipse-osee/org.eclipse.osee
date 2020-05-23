/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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