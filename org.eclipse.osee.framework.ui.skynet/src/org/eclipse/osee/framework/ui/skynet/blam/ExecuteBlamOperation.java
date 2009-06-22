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

import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;

/**
 * @author Ryan D. Brooks
 */
public class ExecuteBlamOperation extends AbstractOperation {
   private final List<BlamOperation> operations;
   private final VariableMap variableMap;
   private final Appendable output;

   public ExecuteBlamOperation(String name, Appendable output, VariableMap variableMap, List<BlamOperation> operations) {
      super(name, SkynetGuiPlugin.PLUGIN_ID);
      this.variableMap = variableMap;
      this.operations = operations;
      this.output = output;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (operations.isEmpty()) {
         throw new OseeStateException("No operations were found for this workflow");
      }
      double workPercentage = 1 / operations.size();
      for (BlamOperation operation : operations) {
         operation.setOutput(output);
         doSubWork(new InternalOperationAdapter(operation), monitor, workPercentage);
      }
   }

   /* (non-Javadoc)
   * @see org.eclipse.osee.framework.core.operation.AbstractOperation#createErrorStatus(java.lang.Throwable)
   */
   @Override
   protected IStatus createErrorStatus(Throwable error) {
      IStatus status = super.createErrorStatus(error);
      try {
         if (error != null) {
            output.append(Lib.exceptionToString(error));
         } else {
            output.append(status.getMessage());
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return status;
   }

   private final class InternalOperationAdapter extends AbstractOperation {
      private final BlamOperation operation;

      public InternalOperationAdapter(BlamOperation operation) {
         super(operation.getName(), SkynetGuiPlugin.PLUGIN_ID);
         this.operation = operation;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         operation.runOperation(variableMap, monitor);
      }
   }
}