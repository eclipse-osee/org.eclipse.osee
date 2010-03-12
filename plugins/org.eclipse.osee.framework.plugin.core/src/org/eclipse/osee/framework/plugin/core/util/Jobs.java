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
package org.eclipse.osee.framework.plugin.core.util;

import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;

/**
 * @author Ryan D. Brooks
 */
public final class Jobs {

   private Jobs() {
   }

   public static Job startJob(Job job, IJobChangeListener jobChangeListener) {
      return startJob(job, true, jobChangeListener);
   }

   public static Job startJob(Job job) {
      return startJob(job, true, null);
   }

   public static Job startJob(Job job, boolean user) {
      return startJob(job, user, null);
   }

   public static void runInJob(String name, IExceptionableRunnable runnable, Class<?> clazz, String pluginId) {
      runInJob(name, runnable, clazz, pluginId, true);
   }

   public static void runInJob(String name, IExceptionableRunnable runnable, Class<?> clazz, String pluginId, boolean user) {
      startJob(new CatchAndReleaseJob(name, runnable, clazz, pluginId), user);
   }

   public static void runInJob(AbstractOperation operation, boolean user) {
      Operations.executeAsJob(operation, user);
   }

   public static Job startJob(Job job, boolean user, IJobChangeListener jobChangeListener) {
      return Operations.scheduleJob(job, user, Job.LONG, jobChangeListener);
   }
}