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

package org.eclipse.osee.activity.api;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author Ryan D. Brooks
 */
public final class ThreadStats {
   public final ThreadInfo threadInfo;
   public long cpuTime;
   public long cpuTimeElapsed;

   public ThreadStats(ThreadInfo threadInfo, long cpuTime) {
      this.threadInfo = threadInfo;
      this.cpuTime = cpuTime;
   }

   public void setCpuTimeElapsed(ThreadMXBean threadMxBean) {
      long currentCpuTime = threadMxBean.getThreadCpuTime(threadInfo.getThreadId());
      cpuTimeElapsed = currentCpuTime - cpuTime;
      cpuTime = currentCpuTime;
   }
}