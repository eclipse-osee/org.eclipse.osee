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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.exception.BranchMergeException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Theron Virgin
 */
public class RelationConflict extends Conflict {

   public RelationConflict(GammaId sourceGamma, GammaId destGamma, ArtifactId artId, TransactionRecord toTransactionId, IOseeBranch mergeBranch, IOseeBranch sourceBranch, IOseeBranch destBranch) {
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
   public Id getObjectId() {
      return RelationId.SENTINEL;
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
   public boolean setToDest(SkynetTransaction transaction) {
      return setToDest();
   }

   @Override
   public boolean setToDest() {
      return false;
   }

   @Override
   public boolean setToSource(SkynetTransaction transaction) {
      return setToSource();
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
   public GammaId getMergeGammaId() throws BranchMergeException {
      throw new BranchMergeException("Relation Conflicts are not implemented yet");
   }

   @Override
   public boolean applyPreviousMerge(BranchId mergeBranchId, BranchId destBranchId) {
      return false;
   }
}
