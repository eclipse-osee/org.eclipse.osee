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
package org.eclipse.osee.executor.admin;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Roberto E. Escobar
 */
public interface ExecutorAdmin {

   void createFixedPoolExecutor(String id, int poolSize);

   <T> Future<T> schedule(String id, Callable<T> callable, ExecutionCallback<T> callback);

   <T> Future<T> schedule(String name, Callable<T> task);

   void shutdown(String id);

   Future<?> scheduleOnce(String name, Runnable task);

   ScheduledFuture<?> scheduleAtFixedRate(String name, Runnable task, long initialDelay, long executionRate, TimeUnit timeUnit);

   ScheduledFuture<?> scheduleWithFixedDelay(String name, Runnable task, long initialDelay, long delay, TimeUnit unit);

}