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
package org.eclipse.osee.framework.core.operation;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class LogProgressMonitor implements IProgressMonitor {
   private boolean isCancelled;
   private String taskName;

   public LogProgressMonitor() {
      this.isCancelled = false;
      this.taskName = "";
   }

   @Override
   public void beginTask(String name, int totalWork) {
      this.taskName = name;
      OseeLog.log(Activator.class, Level.INFO, String.format("Start: %s", taskName));
   }

   @Override
   public void done() {
      OseeLog.log(Activator.class, Level.INFO, String.format("Finish: %s", taskName));
   }

   @Override
   public void internalWorked(double work) {

   }

   @Override
   public boolean isCanceled() {
      return isCancelled;
   }

   @Override
   public void setCanceled(boolean value) {
      this.isCancelled = value;
   }

   @Override
   public void setTaskName(String name) {
      if (Strings.isValid(name)) {
         this.taskName = name;
         OseeLog.log(Activator.class, Level.INFO, name);
      }
   }

   @Override
   public void subTask(String name) {
      if (Strings.isValid(name)) {
         OseeLog.log(Activator.class, Level.FINER, name);
      }
   }

   @Override
   public void worked(int work) {

   }

}
