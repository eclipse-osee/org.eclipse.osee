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
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.swt.widgets.Display;

/**
 * Schedules a task to be executed in the display thread.
 * 
 * @author Ken J. Aguilar
 * @see org.eclipse.swt.widgets.Display#timerExec(int, java.lang.Runnable)
 */
public abstract class PeriodicDisplayTask {
   protected final int period;
   private final Display display;
   private DisplayTask task = null;

   /**
    * Inner class used mainly to hide the run method from being exposed as a public method.
    * 
    * @author Ken J. Aguilar
    */
   private final class DisplayTask implements Runnable {
      /**
       * once the specified period has elapsed this method will be called in the {@link Display}'s thread.
       */
      public void run() {
         update();
         // the call to the update method may have set done to true
         schedule();
      }
   }

   /**
    * Creates a new PeriodicDisplayTask
    * 
    * @param display The display containing the target of the update
    * @param period the period in milliseconds <B>between</B> updates
    */
   protected PeriodicDisplayTask(final Display display, final int period) {
      this.period = period;
      this.display = display;
   }

   /**
    * starts the periodic task
    */
   final public synchronized void start() {
      if (task != null) {
         stop();
      }
      task = new DisplayTask();
      schedule();
   }

   /**
    * Stops the Periodic updater from running. Any outstanding updates will be not be executed unless the update is
    * already in execution. This PeriodicDisplayTask can be started again.
    */
   final public synchronized void stop() {
	   if (task != null) {
		   display.timerExec(-1, task);
		   task = null;
	   }
   }

   /**
    * This method schedules the execution of the run method after the period has elapsed. This method must be called
    * from a subclass's implementation of the run method if the run method must occur again. If the timer has been
    * stopped then calling this method has no effect
    */
   final protected void schedule() {
      if (task != null && !display.isDisposed()) {
         display.timerExec(period, task);
      }
   }

   protected abstract void update();

   public Display getDisplay() {
      return display;
   }
}
