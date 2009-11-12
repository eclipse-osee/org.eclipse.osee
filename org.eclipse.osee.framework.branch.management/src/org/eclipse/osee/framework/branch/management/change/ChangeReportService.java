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
package org.eclipse.osee.framework.branch.management.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.branch.management.IChangeReportService;
import org.eclipse.osee.framework.branch.management.internal.InternalBranchActivator;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportService implements IChangeReportService {
   
   public IStatus getChanges(TransactionRecord sourceTransaction, TransactionRecord destinationTransaction, IProgressMonitor monitor, boolean isHistorical, Collection<ChangeItem> changes) throws OseeCoreException{
      List<IOperation> ops = new ArrayList<IOperation>();

      if (isHistorical) {
         ops.add(new LoadChangeDataOperation(sourceTransaction.getId(), destinationTransaction, changes));
      } else {
         ops.add(new LoadChangeDataOperation(sourceTransaction, destinationTransaction, null, changes));
      }
      ops.add(new ComputeNetChangeOperation(changes));

      String opName =
            String.format("Gathering changes");
      IOperation op = new CompositeOperation(opName, InternalBranchActivator.PLUGIN_ID, ops);
      Operations.executeWork(op, monitor, -1);
      try {
         Operations.checkForErrorStatus(op.getStatus());
         return Status.OK_STATUS;
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         } else {
            throw new OseeWrappedException(ex);
         }
      }
   }

}
