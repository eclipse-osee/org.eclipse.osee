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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ote.core.environment.interfaces.BasicTimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;

public abstract class TimerControl implements ITimerControl{

   private final ScheduledExecutorService executor;
   
   public TimerControl(int maxTimers){
	  executor  = Executors.newScheduledThreadPool(maxTimers);
   }
   
   public void cancelTimers() {
      executor.shutdown();
   }

   public ScheduledFuture<?> schedulePeriodicTask(Runnable task, long initialDelay, long period) {
	   return executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
   }
   
   public ScheduledFuture<?> scheduleOneShotTask(Runnable task, long delay) {
	   return executor.schedule(task, delay, TimeUnit.MILLISECONDS);
   }
   
   public void envWait(int milliseconds) throws InterruptedException {
      envWait(new BasicTimeout(), milliseconds);
   }

   public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
      setTimerFor(obj, milliseconds);
      synchronized (obj) {
         obj.wait();
      }
   }
}
