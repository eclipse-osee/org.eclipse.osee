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
package org.eclipse.osee.ote.core.environment;

import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.TestException;



/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public abstract class EnvironmentTask {
   public static final double cycleResolution = 300.0;
   private double hzRate;
   private int phase;
   private int runEveryNcycles;
   private boolean running;
   private TimerTask timerTask;
   
   private double averateRate;

   private final Benchmark bm;
   
   public EnvironmentTask(double hzRate) {
      this(hzRate, 0);
   }
   
   public EnvironmentTask(double hzRate, int phase) {
      this.hzRate = hzRate;
      this.running = true;
      this.phase = phase;
      runEveryNcycles = (int)Math.round(cycleResolution / hzRate);
      bm = new Benchmark(getClass().getName(), (long) (1000000.0 / hzRate));
      GCHelper.getGCHelper().addRefWatch(this);
   }
   
   public EnvironmentTask( double hzRate, TestEnvironment environment)
   {
      this( hzRate);
      environment.addTask(this);
   }
   
   public void baseRunOneCycle(int cycleCount) throws TestException{
      if (cycleCount == -1 || cycleCount % runEveryNcycles == phase && running) {	// if my turn

         bm.samplePoint();
         try {
            runOneCycle();
         }
         catch (InterruptedException e) {
        	 OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
         }

      }
   }
   
   public void cancel(){
      if(timerTask != null)
         timerTask.cancel();
   }
   
   public void disable() {
      this.running = false;
   }
   
   public void enable() {
      this.running = true;
   }
   /** Gets the hzRate. */
   public double getHzRate() {
      return hzRate;
   }
   
   public boolean isRunning(){
      return this.running;
   }
   
   public abstract void runOneCycle() throws InterruptedException, TestException;
   
   public void setTimerTask(TimerTask timerTask){
      this.timerTask = timerTask;
   }
   
   public double getAverateRate() {
      return averateRate;
   }
   
   public String toString() {
      return this.getClass().getName() + "{ Task: " + hzRate + "Hz, Phase " + phase + ", running=" + (running ? "true" : "false") + " }";
      
   }
}