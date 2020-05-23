/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.operation;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
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
      if (!OseeProperties.isInTest()) {
         OseeLog.logf(Activator.class, Level.INFO, "Start: %s", taskName);
      }
   }

   @Override
   public void done() {
      if (!OseeProperties.isInTest()) {
         OseeLog.logf(Activator.class, Level.INFO, "Finish: %s", taskName);
      }
   }

   @Override
   public void internalWorked(double work) {
      // provided for subclass implementation
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
         if (!OseeProperties.isInTest()) {
            OseeLog.log(Activator.class, Level.INFO, name);
         }
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
      // provided for subclass implementation
   }

}
