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
package org.eclipse.osee.activity.internal;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import org.eclipse.osee.activity.api.ThreadStats;

/**
 * @author Ryan D. Brooks
 */
public class ThreadActivity {
   private final ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
   private static final int ConvertToMillSec = 1000000;

   public ThreadStats[] getThreadActivity() {
      ThreadInfo[] threadInfos = threadMxBean.dumpAllThreads(false, false);
      ThreadStats[] threadStats = new ThreadStats[threadInfos.length];

      for (int i = 0; i < threadStats.length; i++) {
         threadStats[i] = new ThreadStats(threadInfos[i], threadMxBean.getThreadCpuTime(threadInfos[i].getThreadId()));
      }
      return threadStats;
   }

   public String getThreadActivityDelta(ThreadStats[] threadStats) {
      StringBuilder sb = new StringBuilder(400);

      for (ThreadStats stat : threadStats) {
         stat.setCpuTimeElapsed(threadMxBean);
      }

      Arrays.sort(threadStats, (ThreadStats t1, ThreadStats t2) -> Long.compare(t1.cpuTimeElapsed, t2.cpuTimeElapsed));

      int n = Math.max(threadStats.length - 15, 0);

      for (int i = threadStats.length - 1; i >= n; i--) {
         if (threadStats[i].cpuTimeElapsed == 0) {
            break;
         }

         sb.append(threadStats[i].threadInfo.getThreadName());
         sb.append("(");
         sb.append(threadStats[i].threadInfo.getThreadId());
         sb.append("), ");
         sb.append(threadStats[i].cpuTimeElapsed / ConvertToMillSec);
         sb.append(", ");
         sb.append(threadStats[i].cpuTime / ConvertToMillSec);
         sb.append(", ");
         // sb.append(threadStats[i].threadInfo.getBlockedTime());
         // sb.append(", ");

         StackTraceElement[] stackTrace = threadStats[i].threadInfo.getStackTrace();
         if (stackTrace.length > 0) {
            int stackCount = Math.min(4, stackTrace.length);
            for (int j = 0; j < stackCount; j++) {
               sb.append(stackTrace[j]);
               sb.append(' ');
            }
         }
         sb.append("\n");
      }
      return sb.toString();
   }
}