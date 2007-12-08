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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Sole purpose is to read input from a specified stream and save it into a buffer providing some access methods to the
 * buffer.
 * 
 * @author Michael P. Masterson
 */
public class InputBufferThread extends Thread {

   private BufferedReader input;
   private StringBuffer buffer;
   private boolean shouldStopRunning;
   private long lastRead;

   /**
    * @param input The stream to read from
    */
   public InputBufferThread(BufferedReader input) {
      super();
      shouldStopRunning = false;
      this.input = input;
      buffer = new StringBuffer();
   }

   /**
    * Overridden Thread.run method. Reads from the input stream on character at a time until the end of available input
    * or until the boold shouldStopRunning is set by an outside source.
    */
   public void run() {
      char character;
      try {
         while ((character = (char) input.read()) != -1) {
            lastRead = System.currentTimeMillis();
            if (this.shouldStopRunning) {
               break;
            }
            this.append(character);
         }

         input.close();
      } catch (IOException ex) {
         return;
      }
   }

   /**
    * Appends one character to the buffer.
    * 
    * @param line The character to append
    */
   private synchronized void append(char line) {
      buffer.append(line);
   }

   /**
    * Checks if the string passed is contained in the buffer so far
    * 
    * @param matcher The string to look for
    * @return True if the String passed is somewhere in the buffer at this point
    */
   public synchronized boolean contains(String matcher, boolean remove) {
      int index = buffer.lastIndexOf(matcher);
      if (remove && index > 0) {
         buffer.delete(0, index + matcher.length());
      }
      return index >= 0;
   }

   public boolean contains(String matcher) {
      return contains(matcher, false);
   }

   /**
    * @return The entire buffered input.
    */
   public synchronized String getBuffer() {
      return buffer.toString();
   }

   /**
    * Tells this thread whether to stop on the next cycle or not
    * 
    * @param b True if the thread should stop on the next run cycle.
    */
   public void stopOnNextRun(boolean b) {
      this.shouldStopRunning = b;
   }

   public long getLastRead() {
      if (lastRead == 0)
         return System.currentTimeMillis();
      else
         return lastRead;
   }
}
