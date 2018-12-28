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
package org.eclipse.osee.framework.jdk.core.util.benchmark;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Takes time measurements and provides some simple statistics. Useful for determining elapsed time between two points
 * in frequently executed code.<BR>
 * <P>
 * <B>NOTE: </B>To enable benchmarking the JVM argument -Dosee.benchmark must be specified otherwise this code does
 * nothing
 * 
 * @author Ken J. Aguilar
 */
public class Benchmark {

   private static boolean IS_BENCHMARKING_ENABLED = false;
   private final long threshold;
   private long totalSamples;
   private long startTime;
   private long totalTime;
   private long exceedCount;
   private long longestSample;
   private long shortestSample;
   private long totalExceedAmount;
   private static final Vector<Benchmark> list = new Vector<>(16);
   private final HashMap<String, Integer> exceeders = new HashMap<>(100);
   private final String name;

   public Benchmark(String name) {
      this(name, Long.MAX_VALUE);
   }

   /**
    * @param threshold if samples exceed this time (in microseconds) then the exceed count will be incremented
    */
   public Benchmark(String name, long threshold) {
      this.name = name;
      synchronized (list) {
         list.add(this);
      }
      this.threshold = threshold;
      totalSamples = 0;
      totalTime = 0;
      exceedCount = 0;
      longestSample = 0;
      shortestSample = Long.MAX_VALUE;
      startTime = Long.MIN_VALUE;
      totalExceedAmount = 0;
   }

   public static void resetAll() {
      synchronized (list) {
         for (Benchmark bm : list) {
            bm.totalSamples = 0;
            bm.totalTime = 0;
            bm.exceedCount = 0;
            bm.longestSample = 0;
            bm.shortestSample = Long.MAX_VALUE;
            bm.startTime = Long.MIN_VALUE;
            bm.totalExceedAmount = 0;
            bm.exceeders.clear();
         }
      }

   }

   /**
    * Begins the sample
    */
   public void startSample() {
      if (!isBenchmarkingEnabled()) {
         return;
      }
      startSample(System.nanoTime());
   }

   public void startSample(long time) {
      if (!isBenchmarkingEnabled()) {
         return;
      }
      startTime = time;
   }

   public void samplePoint() {
      if (!isBenchmarkingEnabled()) {
         return;
      }
      samplePoint(System.nanoTime());
   }

   /**
    * Measures time between sample points
    */
   public void samplePoint(long time) {
      if (!isBenchmarkingEnabled()) {
         return;
      }
      if (startTime == Long.MIN_VALUE) {
         // this is the first time samplePoint was called
         startTime = time;
      } else {
         final long duration = (time - startTime) / 1000;
         totalTime += duration;
         if (duration > threshold) {
            exceedCount++;
            totalExceedAmount += threshold - duration;
         }
         if (duration > longestSample) {
            longestSample = duration;
         }
         if (duration < shortestSample) {
            shortestSample = duration;
         }

         totalSamples++;
         startTime = time;
      }
   }

   public boolean endSample() {
      if (!isBenchmarkingEnabled()) {
         return false;
      }
      return endSample(System.nanoTime());
   }

   /**
    * Ends the sample. Measures the elapsed time between the start of the sample and the time this method is called.
    */
   public boolean endSample(long time) {
      boolean exceeded = false;
      if (!isBenchmarkingEnabled()) {
         return exceeded;
      }
      final long duration = (time - startTime) / 1000;
      totalTime += duration;
      if (duration > threshold) {
         exceedCount++;
         totalExceedAmount += threshold - duration;
         exceeded = true;
      }
      if (duration > longestSample) {
         longestSample = duration;
      }
      if (duration < shortestSample) {
         shortestSample = duration;
      }

      totalSamples++;
      return exceeded;
   }

   /**
    * @return the number of samples that exceeded the threshold
    */
   public long getExceedCount() {
      return exceedCount;
   }

   /**
    * @return the longest sample in microseconds
    */
   public long getLongestSample() {
      return longestSample;
   }

   /**
    * @return the threshold value in microseconds. If no threshold was set this will return <CODE>Long.MAX_VALUE</CODE>
    */
   public long getThreshold() {
      return threshold;
   }

   /**
    * @return the total samples taken
    */
   public long getTotalSamples() {
      return totalSamples;
   }

   /**
    * @return the average of the samples in microseconds
    */
   public long getAverage() {
      if (totalSamples > 0) {
         return totalTime / totalSamples;
      } else {
         return Long.MAX_VALUE;
      }
   }

   /**
    * @return the shortest sample taken in microseconds
    */
   public long getShortestSample() {
      return shortestSample;
   }

   public String getName() {
      return name;
   }

   public long getAverageExceedAmount() {
      if (exceedCount == 0) {
         return 0;
      }
      return Math.abs(totalExceedAmount / exceedCount);
   }

   public static void main(String[] args) {
      Benchmark bm = new Benchmark("unit test", 10000);

      if (isBenchmarkingEnabled()) {
         System.out.println("benchmarking is enabled");
      } else {
         System.out.println("benchmarking is disabled");
      }

      for (int i = 0; i < 1000; i++) {
         bm.startSample();
         try {
            Thread.sleep(i % 2 == 0 ? 5 : 10);
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
         bm.endSample();
      }

      // Sorry Need to keep this java 1.4 compatible
      Object[] formatArgs = {
         new Long(bm.getTotalSamples()),
         new Float((float) bm.getLongestSample() / 1000),
         new Float((float) bm.getShortestSample() / 1000),
         new Float((float) bm.getAverage() / 1000),
         new Long(bm.getExceedCount())};
      MessageFormat outmessage = new MessageFormat(
         "total samples: {0,number,integer}, max time: {1,number,integer}ms, min: {2,number,integer}ms, average: {3,number,integer}ms, exceed count: {4,number,integer}");

      System.out.println(outmessage.format(formatArgs));

   }

   @Override
   public String toString() {
      return String.format("%s\t total samples: %d,\t average: %fms,\t max time: %f, min: %fms, exceed count: %d", name,
         new Long(getTotalSamples()), new Float(getLongestSample() / 1000), new Float(getShortestSample() / 1000),
         new Float(getAverage() / 1000), new Long(getExceedCount()));
   }

   public void addExceeder(String exceeder) {
      Integer c = exceeders.get(exceeder);
      if (c == null) {
         exceeders.put(exceeder, 1);
      } else {
         exceeders.put(exceeder, c + 1);
      }
   }

   public Collection<Map.Entry<String, Integer>> getExceeders() {
      return exceeders.entrySet();
   }

   public static List<Benchmark> getAllBenchamrks() {
      synchronized (list) {
         return new ArrayList<>(list);
      }
   }

   public static void setBenchmarkingEnabled(boolean isEnabled) {
      Benchmark.IS_BENCHMARKING_ENABLED = isEnabled;
   }

   public static boolean isBenchmarkingEnabled() {
      return Benchmark.IS_BENCHMARKING_ENABLED;
   }

}
