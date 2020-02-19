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

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Sole purpose is to read input from a specified stream and save it into a buffer providing some access methods to the
 * buffer.
 *
 * @author Michael P. Masterson
 */
public class InputBufferThread extends Thread {
   private final byte[] charBuffer = new byte[1024];
   private final InputStream input;
   private final StringBuilder buffer;
   private volatile boolean shouldStopRunning;
   private long lastRead;

   /**
    * @param input The stream to read from
    */
   public InputBufferThread(InputStream input) {
      super("Stream input buffer thread");
      shouldStopRunning = false;
      this.input = input;
      buffer = new StringBuilder(8196);
   }

   /**
    * Overridden Thread.run method. Reads from the input stream on character at a time until the end of available input
    * or until the bold shouldStopRunning is set by an outside source.
    */
   @Override
   public void run() {
      XConsoleLogger.out("thread started");
      int count = 0;
      try {
         int size = input.read(charBuffer);
         while (size >= 0) {
            count++;
            synchronized (this) {
               lastRead = System.currentTimeMillis();
               if (shouldStopRunning) {
                  break;
               }
               append(charBuffer, size);
            }
            size = input.read(charBuffer);
         }
      } catch (InterruptedIOException e) {
         if (shouldStopRunning != true) {
            XConsoleLogger.err(Lib.exceptionToString(e));
         }
      } catch (IOException e) {
         if (!shouldStopRunning) {
            XConsoleLogger.err(Lib.exceptionToString(e), "error at count " + count);
            e.printStackTrace(System.err);
         }
      } finally {
         try {
            input.close();
         } catch (IOException e) {
            XConsoleLogger.err(Lib.exceptionToString(e));
            System.out.flush();
         } finally {
            // wake up anyone waiting for data or else they will be stuck forever
            synchronized (this) {
               notifyAll();
            }
         }
      }
   }

   /**
    * Appends one character to the buffer.
    *
    * @param line The character to append
    */
   private void append(byte[] line, int size) {
      buffer.append(new String(line, 0, size));
      notify();
   }

   /**
    * Checks if the string passed is contained in the buffer so far
    *
    * @param matcher The string to look for
    * @return a positive value representing the index at which it was found or negative 1 if it was not found
    */
   public synchronized int contains(String matcher, boolean remove) {
      int index = buffer.lastIndexOf(matcher);
      if (remove && index >= 0) {
         buffer.delete(0, index + matcher.length());
      }
      return index;
   }

   /**
    * Checks if the string passed is contained in the buffer so far
    *
    * @param matcher The string to look for
    * @return a positive value representing the index at which it was found or negative 1 if it was not found
    */
   public synchronized int contains(String matcher) {
      return contains(matcher, false);
   }

   public synchronized int waitFor(String matcher, int millis) throws InterruptedException {
      return waitFor(matcher, false, millis);
   }

   public synchronized int waitFor(String matcher, boolean remove, int millis) throws InterruptedException {
      if (shouldStopRunning) {
         throw new IllegalStateException("stream processing terminated");
      }
      long time = System.currentTimeMillis();
      int result = contains(matcher, false);
      long timeRemaining = millis;
      while (result < 0 && timeRemaining > 0) {
         wait(timeRemaining);
         if (shouldStopRunning) {
            // we were told to stop or the stream was closed on the other end
            throw new InterruptedException("stream processing terminated");
         }
         result = contains(matcher, false);
         timeRemaining = millis - (System.currentTimeMillis() - time);
      }
      if (remove && result >= 0) {
         buffer.delete(0, result + matcher.length());
      }
      return result;
   }

   public synchronized MatchResult waitFor(Pattern pattern, boolean remove, int millis) throws InterruptedException {
      if (shouldStopRunning) {
         throw new IllegalStateException("stream processing terminated");
      }
      Matcher matcher = pattern.matcher(buffer.toString());
      long time = System.currentTimeMillis();
      long timeRemaining = millis;
      boolean result = matcher.matches();
      while (!result && timeRemaining > 0) {
         wait(timeRemaining);
         if (shouldStopRunning) {
            // we were told to stop or the stream was closed on the other end
            throw new InterruptedException("stream processing terminated");
         }
         matcher = matcher.reset(buffer.toString());
         result = matcher.matches();
         timeRemaining = millis - (System.currentTimeMillis() - time);
      }
      if (remove && result) {
         buffer.delete(0, matcher.end());
      }
      return result ? matcher.toMatchResult() : null;
   }

   /**
    * @return true if a transmission occurred false otherwise
    */
   public synchronized boolean waitFor(int millis) throws InterruptedException {
      if (shouldStopRunning) {
         throw new IllegalStateException("stream processing terminated");
      }

      long currentTime = System.currentTimeMillis();
      long savedLastRead = lastRead;
      while (savedLastRead != lastRead) {
         wait(millis);
         // make sure we did not reach ourr timeout limit, also we need to handle potential 'spurious' wakeups
         long next = System.currentTimeMillis();
         millis -= next - currentTime;
         currentTime = next;
         if (savedLastRead == lastRead && millis <= 0) {
            return false;
         }
      }
      return true;
   }

   /**
    * @return The entire buffered input.
    */
   public synchronized String getBuffer() {
      return buffer.toString();
   }

   public synchronized String subString(int beginIndex) {
      return buffer.substring(beginIndex);
   }

   public synchronized String subString(int beginIndex, int endIndex) {
      return buffer.substring(beginIndex, endIndex);
   }

   /**
    * @return The entire buffered input.
    */
   public synchronized int getLength() {
      return buffer.length();
   }

   /**
    * Tells this thread whether to stop on the next cycle or not
    *
    * @param b True if the thread should stop on the next run cycle.
    */
   public void stopOnNextRun(boolean b) {
      this.shouldStopRunning = b;
   }

   public void stopNow() throws InterruptedException {
      this.shouldStopRunning = true;
      interrupt();
      join(5000);
   }

   public synchronized long getLastRead() {
      if (lastRead == 0) {
         return System.currentTimeMillis();
      } else {
         return lastRead;
      }
   }

   public synchronized void clear() {
      buffer.delete(0, buffer.length());
   }

}
