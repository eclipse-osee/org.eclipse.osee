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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseServerCommand extends AbstractOperation {

   private CommandInterpreter ci;

   private volatile boolean isRunning;
   private volatile boolean isVerbose;
   private volatile boolean isExecutionAllowed;

   protected BaseServerCommand(String name) {
      super(name, Activator.class.getSimpleName());
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

   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      this.isRunning = true;
      long startTime = System.currentTimeMillis();
      try {
         doCommandWork(monitor);
      } catch (Exception ex) {
         ci.printStackTrace(ex);
      }
      ci.println(String.format("[%s] Completed in [%s]", getName(), Lib.getElapseString(startTime)));
      this.isRunning = false;
   }

   protected abstract void doCommandWork(IProgressMonitor monitor) throws Exception;
}
