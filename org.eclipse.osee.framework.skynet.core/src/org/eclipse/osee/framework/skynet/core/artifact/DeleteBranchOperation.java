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

package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class DeleteBranchOperation extends AbstractOperation {

   private final Branch branch;

   public DeleteBranchOperation(Branch branch) {
      super("Delete Branch: " + branch, Activator.PLUGIN_ID);
      this.branch = branch;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      BranchState originalState = branch.getBranchState();
      BranchArchivedState originalArchivedState = branch.getArchiveState();
      try {
         branch.setBranchState(BranchState.DELETED);
         branch.setArchived(true);
         branch.persist();
      } catch (Exception ex) {
         branch.setBranchState(originalState);
         branch.setArchived(originalArchivedState.isArchived());
         throw ex;
      }
   }
}