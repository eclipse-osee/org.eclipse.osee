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
import java.util.LinkedList;
import java.util.List;
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

   public List<String> getThreadActivityDelta(ThreadStats[] threadStats) {
      List<String> threads = new LinkedList<>();

      for (ThreadStats stat : threadStats) {
         stat.setCpuTimeElapsed(threadMxBean);
      }

      Arrays.sort(threadStats, (ThreadStats t1, ThreadStats t2) -> Long.compare(t1.cpuTimeElapsed, t2.cpuTimeElapsed));

      int n = Math.max(threadStats.length - 15, 0);

      for (int i = threadStats.length - 1; i >= n; i--) {
         if (threadStats[i].cpuTimeElapsed == 0) {
            break;
         }

         threads.add(String.format("[%s] - id [%s] elapsed [%s] total [%s]", threadStats[i].threadInfo.getThreadName(),
            threadStats[i].threadInfo.getThreadId(), threadStats[i].cpuTimeElapsed / ConvertToMillSec,
            threadStats[i].cpuTime / ConvertToMillSec));
         // sb.append(threadStats[i].threadInfo.getBlockedTime());
         // sb.append(", ");

         StackTraceElement[] stackTrace = threadStats[i].threadInfo.getStackTrace();
         if (stackTrace.length > 0) {
            int stackCount = Math.min(4, stackTrace.length);
            for (int j = 0; j < stackCount; j++) {
               threads.add("trace: " + String.valueOf(stackTrace[j]));
            }
         }
      }
      return threads;
   }
}