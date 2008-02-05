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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Ryan D. Brooks
 */
public class BlamJob extends Job {
   private final BlamWorkflow workflow;
   private final BlamVariableMap variableMap;
   private final Branch branch;
   private final Collection<IBlamEventListener> listeners;

   public BlamJob(BlamVariableMap variableMap, BlamWorkflow workflow, Branch branch, String name) {
      super(name);

      if (variableMap == null) {
         throw new IllegalArgumentException("VariableMap can not be null");
      }
      if (workflow == null) {
         throw new IllegalArgumentException("Workflow can not be null");
      }
      if (branch == null) {
         throw new IllegalArgumentException("Branch can not be null");
      }

      this.branch = branch;
      this.variableMap = variableMap;
      this.workflow = workflow;
      this.listeners = new LinkedList<IBlamEventListener>();
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      long startTime = System.currentTimeMillis();
      notifyListeners(new BlamStartedEvent());
      try {
         new BlamOperationTx(branch, monitor).execute();
         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, false);
         toReturn = new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getLocalizedMessage(), ex);
      } finally {
         monitor.done();
         notifyListeners(new BlamFinishedEvent(System.currentTimeMillis() - startTime));
      }
      return toReturn;
   }

   public void addListener(IBlamEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }

      listeners.add(listener);
   }

   public boolean removeListener(IBlamEventListener listener) {
      return listeners.remove(listener);
   }

   private void notifyListeners(IBlamEvent event) {
      for (IBlamEventListener listener : listeners) {
         listener.onEvent(event);
      }
   }

   final private class BlamOperationTx extends AbstractSkynetTxTemplate {
      private IProgressMonitor monitor;

      public BlamOperationTx(Branch branch, IProgressMonitor monitor) {
         super(branch);
         this.monitor = monitor;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         List<BlamOperation> operations = workflow.getOperations();
         if (operations.size() == 0) {
            throw new IllegalStateException("No operations were found for this workflow");
         }

         for (BlamOperation operation : operations) {
            operation.runOperation(variableMap, monitor);
         }
      }
   }
}