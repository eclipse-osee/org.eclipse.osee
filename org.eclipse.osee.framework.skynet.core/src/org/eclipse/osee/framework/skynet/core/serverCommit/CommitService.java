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
package org.eclipse.osee.framework.skynet.core.serverCommit;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.commit.CommitDbOperation;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.LoadChangeDataOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public class CommitService implements ICommitService {

   @Override
   public void commitBranch(IProgressMonitor monitor, ConflictManagerExternal conflictManager, boolean archiveSourceBranch) throws OseeCoreException {
      Branch sourceBranch = conflictManager.getSourceBranch();
      Branch destinationBranch = conflictManager.getDestinationBranch();
      Branch mergeBranch = BranchManager.getMergeBranch(sourceBranch, destinationBranch);

      List<ChangeItem> changes = new ArrayList<ChangeItem>();

      List<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new LoadChangeDataOperation(sourceBranch, destinationBranch, mergeBranch, changes));
      ops.add(new ComputeNetChangeOperation(changes));
      ops.add(new CommitDbOperation(sourceBranch, destinationBranch, mergeBranch, changes));

      String opName =
            String.format("Commit: [%s]->[%s]", sourceBranch.getShortName(), destinationBranch.getShortName());
      IOperation op = new CompositeOperation(opName, Activator.PLUGIN_ID, ops);
      Operations.executeWork(op, monitor, -1);
      try {
         Operations.checkForErrorStatus(op.getStatus());
      } catch (Exception ex) {
         if (ex instanceof OseeCoreException) {
            throw (OseeCoreException) ex;
         } else {
            throw new OseeWrappedException(ex);
         }
      }
      if (archiveSourceBranch) {
         sourceBranch.setArchived(true);
         sourceBranch.persist();
      }
   }
}
