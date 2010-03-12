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
package org.eclipse.osee.ote.messaging.dds;

/**
 * <code>Duration</code> is used to specify a difference in time with
 * nano-second resolution.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class Duration extends Time {
   public final static Duration ZERO_DURATION = new Duration(0, 0);
   private Time startTime;
   
   /**
    * Construct a new <code>Duration</code> with specific values.
    * 
    * @param seconds - The number of seconds for the duration.
    * @param nanoSeconds - The number of nano-seconds for the duration.
    */
   public Duration(long seconds, long nanoSeconds) {
      super(seconds, nanoSeconds);
   }
   
   /**
    * Mark an instant in time as a "start" point. This is used in 
    * conjunction with the <code>expired()</code> method.
    */
   public void markStart() {
      startTime = new Time();
   }

   /**
    * Check if the time specified by this <code>Duration</code> is less
    * than the amount of time since <code>markStart()</code> was called.
    * 
    * @return <b>true</b> iff the time since <code>markStart()</code> is greater than this duration.
    */
   public boolean expired() {
      return (new Time().millisSince(startTime) > this.getMilliseconds());
   }
}
