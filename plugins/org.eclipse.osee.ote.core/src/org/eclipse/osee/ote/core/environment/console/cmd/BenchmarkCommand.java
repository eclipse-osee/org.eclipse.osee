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
package org.eclipse.osee.ote.core.environment.console.cmd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;

/**
 * @author Ken J. Aguilar
 */
public class BenchmarkCommand extends ConsoleCommand {
   private static final String NAME = "b";
   private static final String DESCRIPTION = "outputs results from the benchmarking utility";

   public BenchmarkCommand() {
      super(NAME, DESCRIPTION);
   }

   protected void doCmd(ConsoleShell shell, String[] switches, String[] args) {
      if (Benchmark.isBenchmarkingEnabled()) {
         if (switches.length > 0) {
            processSwitches(switches, args);
         }
         print("Current Benchmarks\n");
         final StringBuilder buffer = new StringBuilder(4096);
         final Iterator<Benchmark> iter = Benchmark.getAllBenchamrks().iterator();
         while (iter.hasNext()) {
            Benchmark bm = (Benchmark) iter.next();
            float val = ((float) bm.getLongestSample()) / 1000.0f;
            buffer.append(bm.getName()).append(": total samples: ").append(bm.getTotalSamples());
            buffer.append(". Max Time: ").append(val).append("ms. Min: ");
            val = ((float) bm.getShortestSample()) / 1000.0f;
            buffer.append(val).append("ms. Avg: ");
            val = ((float) bm.getAverage()) / 1000.0f;
            buffer.append(val).append("ms. Exceed Count: ").append(bm.getExceedCount()).append(" (threshold=").append(
                  bm.getThreshold() / 1000.0f);
            val = ((float) bm.getAverageExceedAmount()) / 1000.0f;
            buffer.append("ms) avg. exceed time:  ").append(val).append("ms\n");
            for (Map.Entry<String, Integer> entry : bm.getExceeders()) {
               buffer.append("\tExceeder ").append(entry.getKey()).append(": counted ").append(entry.getValue()).append(
                     '\n');
            }
         }
         print(buffer.toString());
      } else {
         print("Benchmarks disabled\n");
      }
   }

   private void processSwitches(String[] switches, String[] args) {
      for (String sw : switches) {
         if (sw.equals("-f")) {
            try {
               writeToCSV(args[0]);
               println("wrote results to file " + args[0]);
            } catch (Exception e) {
               printStackTrace(e);
            }
         } else if (sw.equals("-r")) {
            Benchmark.resetAll();
            println("benchmarks reset");
         } else {
            println("unknown switch '" + sw + "'");
         }
      }
   }

   private void writeToCSV(String fileName) throws FileNotFoundException {
      File file = new File(fileName);
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      PrintWriter out = new PrintWriter(bos);
      final Iterator<Benchmark> iter = Benchmark.getAllBenchamrks().iterator();
      out.println("NAME,TOTAL SAMPLES,MAX TIME,MIN TIME,AVG TIME,EXCEED CNT,THRESHOLD,AVG EXCEED TIME,EXCEEDERS");
      while (iter.hasNext()) {
         final Benchmark bm = (Benchmark) iter.next();
         out.format("%s,%d,%f,%f,%f,%d,%f,%f", bm.getName(), bm.getTotalSamples(), bm.getLongestSample() / 1000.0f,
               bm.getShortestSample() / 1000.0f, bm.getAverage() / 1000.0f, bm.getExceedCount(),
               bm.getThreshold() / 1000.0f, bm.getAverageExceedAmount() / 1000.0f);
         for (Map.Entry<String, Integer> entry : bm.getExceeders()) {
            out.format(",Exceeder %s:%d", entry.getKey(), entry.getValue());
         }
         out.println();
      }
      out.close();
   }

}
