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
package org.eclipse.osee.framework.skynet.core.transaction;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * This abstract class provides a uniform way of executing transactions within an Eclipse Job. It handles exceptions
 * ensuring that transactions are processed in the correct order and roll-backs are performed whenever errors are
 * detected.
 * 
 * @author Donald G. Dunne
 */
public abstract class AbstractSkynetTxJobTemplate extends Job {

   private final Branch branch;
   static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchPersistenceManager.class);
   protected IProgressMonitor monitor;

   /**
    * @param name
    */
   public AbstractSkynetTxJobTemplate(String name, Branch branch) {
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
         return new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, result.getText(), null);
      }
      try {
         SkynetTx createTasksTx = new SkynetTx(branch);
         createTasksTx.execute();
      } catch (Exception ex) {
         ConfigUtil.getConfigFactory().getLogger(SkynetActivator.class).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         return new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
      return Status.OK_STATUS;
   }

   private class SkynetTx extends AbstractSkynetTxTemplate {
      /**
       * @param branch
       */
      public SkynetTx(Branch branch) {
         super(branch);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws OseeCoreException, SQLException {
         // This calls the containing class's version of the method
         AbstractSkynetTxJobTemplate.this.handleTxWork();
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate#handleTxFinally()
       */
      @Override
      protected void handleTxFinally() throws OseeCoreException, SQLException {
         super.handleTxFinally();
         // This calls the containing class's version of the method
         AbstractSkynetTxJobTemplate.this.handleTxFinally();
      }
   }

   /**
    * Provides the transaction's work implementation.
    * 
    * @throws Exception
    */
   protected abstract void handleTxWork() throws OseeCoreException, SQLException;

   /**
    * This convenience method is provided in case child classes have a portion of code that needs to execute always at
    * the end of the transaction, regardless of exceptions. <br/><b>Override to add additional code to finally block</b>
    * 
    * @throws Exception
    */
   protected void handleTxFinally() throws OseeCoreException, SQLException {
      // override to add additional code to finally
   }

}
