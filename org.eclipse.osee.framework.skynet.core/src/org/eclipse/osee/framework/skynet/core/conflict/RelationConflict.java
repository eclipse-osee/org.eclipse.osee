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

package org.eclipse.osee.framework.skynet.core.conflict;

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.exception.BranchMergeException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Theron Virgin
 */
public class RelationConflict extends Conflict {

   public RelationConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, Branch mergeBranch, Branch sourceBranch, Branch destBranch) {
      super(sourceGamma, destGamma, artId, toTransactionId, null, mergeBranch, sourceBranch, destBranch);
   }

   @Override
   public boolean clearValue() {
      return false;
   }

   @Override
   public ConflictStatus computeStatus() {
      return null;
   }

   @Override
   public int getObjectId() throws OseeCoreException {
      return 0;
   }

   @Override
   public String getChangeItem() {
      return null;
   }

   @Override
   public ConflictType getConflictType() {
      return null;
   }

   @Override
   public String getDestDisplayData() {
      return null;
   }

   @Override
   public String getMergeDisplayData() {
      return null;
   }

   public String getMergeValue() {
      return null;
   }

   @Override
   public String getSourceDisplayData() {
      return null;
   }

   @Override
   public boolean mergeEqualsDestination() {
      return false;
   }

   @Override
   public boolean mergeEqualsSource() {
      return false;
   }

   @Override
   public boolean setToDest() {
      return false;
   }

   @Override
   public boolean setToSource() {
      return false;
   }

   @Override
   public boolean sourceEqualsDestination() {
      return false;
   }

   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   @Override
   public int getMergeGammaId() throws BranchMergeException {
      throw new BranchMergeException("Relation Conflicts are not implemented yet");
   }

   @Override
   public boolean applyPreviousMerge(int mergeBranchId, int destBranchId) throws OseeCoreException {
      return false;
   }
}
