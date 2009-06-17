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
 * Provides a time class for marking time with nano-second resolution. This class
 * is not fully implemented yet to provide proper nano-second data, as it is not
 * being used yet.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class Time {
   protected long seconds;
   protected long nanoSeconds;
   
   /**
    * Creates a <code>Time</code> object with the current time accurate to the second.
    */
   public Time() {
      seconds = System.currentTimeMillis() / 1000;
      nanoSeconds = 0;
   }
   
   /**
    * @param seconds
    * @param nanoSeconds
    */
   public Time(long seconds, long nanoSeconds) {
      super();
      this.seconds = seconds;
      this.nanoSeconds = nanoSeconds;
   }
   
   public void copyFrom(Time sourceTime) {
      this.seconds = sourceTime.seconds;
      this.nanoSeconds = sourceTime.nanoSeconds;
   }
   /**
    * @return Returns the nanoSeconds.
    */
   public long getNanoSeconds() {
      return nanoSeconds;
   }

   /**
    * @return Returns the seconds.
    */
   public long getSeconds() {
      return seconds;
   }
   
   public long getMilliseconds() {
      return seconds*1000 + nanoSeconds/1000;
   }
   
   protected long millisSince(Time time) {
      return this.getMilliseconds() - time.getMilliseconds();
   }
}
