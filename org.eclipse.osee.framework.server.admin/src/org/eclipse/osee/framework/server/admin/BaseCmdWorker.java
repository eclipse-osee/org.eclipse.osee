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
package org.eclipse.osee.framework.server.admin;

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseCmdWorker implements Runnable {

   private CommandInterpreter ci;

   private volatile boolean isRunning;
   private volatile boolean isVerbose;
   private volatile boolean isExecutionAllowed;

   protected BaseCmdWorker() {
      this.isRunning = false;
      this.isVerbose = true;
      this.isExecutionAllowed = true;
   }

   public boolean isRunning() {
      return isRunning;
   }

   public void setExecutionAllowed(boolean value) {
      this.isExecutionAllowed = value;
   }

   public void setCommandInterpreter(CommandInterpreter ci) {
      this.ci = ci;
   }

   public CommandInterpreter getCommandInterpreter() {
      return this.ci;
   }

   public boolean isVerbose() {
      return isVerbose;
   }

   public void setVerbose(boolean isVerbose) {
      this.isVerbose = isVerbose;
   }

   public boolean isExecutionAllowed() {
      return isExecutionAllowed;
   }

   protected void println(Object value) {
      ci.println(value);
   }

   protected void printStackTrace(Exception ex) {
      ci.printStackTrace(ex);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      this.isRunning = true;
      long startTime = System.currentTimeMillis();
      try {
         doWork(startTime);
      } catch (Exception ex) {
         ci.printStackTrace(ex);
      }
      ci.println(String.format("Done.  Elapsed Time = %s.", getElapsedTime(startTime)));
      this.isRunning = false;
   }

   protected String getElapsedTime(long startTime) {
      return timeToString(System.currentTimeMillis() - startTime);
   }

   protected String timeToString(long value) {
      long leftOverMs = value % 1000;
      long seconds = value / 1000;
      long leftOverSeconds = seconds % 60;
      long minutes = seconds / 60;
      long leftOverMinutes = minutes % 60;
      long hours = minutes / 60;
      return String.format("%d:%02d:%02d.%03d", hours, leftOverMinutes, leftOverSeconds, leftOverMs);
   }

   protected abstract void doWork(long startTime) throws Exception;
}
