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
package org.eclipse.osee.framework.plugin.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
public class LogProgressMonitor implements IProgressMonitor {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(LogProgressMonitor.class);
   private boolean isCancelled;
   private String taskName;

   public LogProgressMonitor() {
      this.isCancelled = false;
      this.taskName = "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
    */
   @Override
   public void beginTask(String name, int totalWork) {
      this.taskName = name;
      logger.log(Level.INFO, String.format("Starting: %s", taskName));
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#done()
    */
   @Override
   public void done() {
      logger.log(Level.INFO, String.format("Finished: %s", taskName));
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
    */
   @Override
   public void internalWorked(double work) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
    */
   @Override
   public boolean isCanceled() {
      return isCancelled;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
    */
   @Override
   public void setCanceled(boolean value) {
      this.isCancelled = value;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
    */
   @Override
   public void setTaskName(String name) {
      this.taskName = name;
      logger.log(Level.INFO, name);
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
    */
   @Override
   public void subTask(String name) {
      logger.log(Level.FINER, name);
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
    */
   @Override
   public void worked(int work) {
      // TODO Auto-generated method stub

   }

}
