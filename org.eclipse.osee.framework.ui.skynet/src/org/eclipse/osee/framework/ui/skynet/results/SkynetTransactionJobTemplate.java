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
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * This abstract class provides a uniform way of executing transactions within an Eclipse Job. It handles exceptions
 * ensuring that transactions are processed in the correct order and roll-backs are performed whenever errors are
 * detected.
 * 
 * @author Donald G. Dunne
 */
public abstract class SkynetTransactionJobTemplate extends Job {
   private final Branch branch;
   protected IProgressMonitor monitor;

   /**
    * @param name
    */
   public SkynetTransactionJobTemplate(String name, Branch branch) {
      super(name);
      this.branch = branch;
   }

   /**
    * @param user true if Job initiated by user
    * @param priority Job.LONG, Job.SHORT
    */
   public void run(boolean user, int priority) {
      setUser(user);
      setPriority(priority);
      schedule();
   }

   /**
    * Perform whatever preprocessing or UI required. NOTE: It is the applications job to run in Display thread if
    * necessary
    * 
    * @return TrueResult if ok to continue; FalseResult will terminate Job with getText() error
    */
   public Result performPreprocess() {
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      this.monitor = monitor;
      Result result = performPreprocess();
      if (result.isFalse()) {
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, result.getText(), null);
      }
      try {
         SkynetTransaction transaction = new SkynetTransaction(branch);
         handleTxWork(transaction);
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         try {
            handleTxFinally();
         } catch (OseeCoreException ex) {
            return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
      }
      return Status.OK_STATUS;
   }

   /**
    * Provides the transaction's work implementation.
    * 
    * @param transaction
    * @throws Exception
    */
   protected abstract void handleTxWork(SkynetTransaction transaction) throws OseeCoreException;

   /**
    * This convenience method is provided in case child classes have a portion of code that needs to execute always at
    * the end of the transaction, regardless of exceptions. <br/>
    * <b>Override to add additional code to finally block</b>
    * 
    * @throws Exception
    */
   protected void handleTxFinally() throws OseeCoreException {
   }

}
