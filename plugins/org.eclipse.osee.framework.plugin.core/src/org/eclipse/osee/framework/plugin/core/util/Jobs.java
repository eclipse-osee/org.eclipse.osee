/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
      // this private empty constructor exists to prevent the default constructor from allowing public construction
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