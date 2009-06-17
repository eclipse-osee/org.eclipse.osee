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
package org.eclipse.osee.ote.core;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.internal.Activator;

/**
 * @author Ken J. Aguilar
 */
public abstract class OseeTestThread {

   private static final Logger logger = Logger.getLogger("osee.test.core.OseeTestThread");
   private final Thread thread;
   private final WeakReference<TestEnvironment> env;
   private static final HashSet<OseeTestThread> threadList = new HashSet<OseeTestThread>(32);
   private volatile Throwable causeOfDeath = null;
   private volatile Date timeOfDeath = null;

   /**
    * Creates the thread with the given name and as a non daemon thread
    * 
    * @param name
    */
   public OseeTestThread(String name, TestEnvironment env) {
      this(name, false, null, env);
   }

   public OseeTestThread(String name, ThreadGroup group, TestEnvironment env) {
      this(name, false, group, env);
   }

   public Thread.State getState() {
      return thread.getState();
   }

   /**
    * Creates the thread with the given name and daemon flag
    * 
    * @param name the name of this thread
    * @param isDaemon marks the thread as a daemon thread
    */
   public OseeTestThread(String name, boolean isDaemon, ThreadGroup group, TestEnvironment env) {
      GCHelper.getGCHelper().addRefWatch(this);
      this.env = new WeakReference<TestEnvironment>(env);
      thread = new Thread(group, name) {

         public void run() {
            try {
               OseeTestThread.this.run();
               synchronized (threadList) {
                  threadList.remove(OseeTestThread.this);
               }
            } catch (TestException e) {
               logger.log(e.getLevel(), "TestException in " + e.getThreadName() + ": " + e.getMessage(), e);
               cleanupAfterException(e);
            } catch (Throwable t) {
               OseeLog.log(Activator.class, Level.SEVERE, "Unhandled exception in " + thread.getName(), t);
               cleanupAfterException(t);
            }
         }
      };
      thread.setDaemon(isDaemon);
      threadList.add(this);
   }

   /**
    * Starts the thread
    */
   public void start() {
      thread.start();
   }

   public void setName(String name) {
      thread.setName(name);
   }

   public String getName() {
      return thread.getName();
   }

   public boolean isAlive() {
      return thread.isAlive();
   }

   public void interrupt() {
      OseeLog.log(TestEnvironment.class, Level.SEVERE,
            "Calling interrupt() on " + thread.getName(), new RuntimeException("call trace"));
      thread.interrupt();
   }

   /**
    * This method will be called upon thread execution
    * 
    * @throws TestException
    */
   protected abstract void run() throws Exception;

   public void join() throws InterruptedException {
      thread.join();
   }

   public void join(int milliseconds) throws InterruptedException {
      thread.join(milliseconds);
   }

   public TestEnvironment getEnvironment() {
      return ((TestEnvironment) OseeTestThread.this.env.get());
   }

   public static Collection<OseeTestThread> getThreads() {
      return threadList;
   }

   private synchronized void cleanupAfterException(Throwable t) {
      causeOfDeath = t;
      timeOfDeath = Calendar.getInstance().getTime();
      ((TestEnvironment) this.env.get()).handleException(t, Level.OFF);
   }

   public Throwable getCauseOfDeath() {
      return causeOfDeath;
   }

   public Date getTimeOfDeath() {
      return timeOfDeath;
   }

   public static void clearThreadReferences() {
      synchronized (threadList) {
         threadList.clear();
      }
   }

   public boolean isInterrupted() {
      return thread.isInterrupted();
   }

   public void setDaemon(boolean isDaemon) {
      thread.setDaemon(isDaemon);
   }

   public Thread getThread() {
      return thread;
   }
}
