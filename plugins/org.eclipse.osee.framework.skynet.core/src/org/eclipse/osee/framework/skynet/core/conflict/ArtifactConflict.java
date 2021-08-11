/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.conflict;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchMergeException;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Theron Virgin
 */
public class ArtifactConflict extends Conflict {
   private static final String CHANGE_ITEM = "Artifact State";
   private static final String ARTIFACT_DELETED = "DELETED";
   private static final String ARTIFACT_MODIFIED = "MODIFIED";
   private final ModificationType sourceModType;
   private final ModificationType destModType;

   public ArtifactConflict(GammaId sourceGamma, GammaId destGamma, ArtifactId artId, TransactionToken toTransactionId, BranchId mergeBranch, BranchToken sourceBranch, BranchToken destBranch, ModificationType sourceModType, ModificationType destModType, long artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, null, mergeBranch, sourceBranch, destBranch);
      this.sourceModType = sourceModType;
      this.destModType = destModType;
   }

   @Override
   public String getArtifactName() {
      if (sourceModType.isDeleted()) {
         return getDestArtifact().getName();
      } else {
         return getSourceArtifact().getName();
      }
   }

   @Override
   public boolean clearValue() {
      return false;
   }

   @Override
   public ConflictStatus computeStatus() {
      return super.computeStatus(getObjectId(), ConflictStatus.INFORMATIONAL);
   }

   @Override
   public Id getObjectId() {
      return getArtId();
   }

   @Override
   public String getChangeItem() {
      return CHANGE_ITEM;
   }

   @Override
   public ConflictType getConflictType() {
      return ConflictType.ARTIFACT;
   }

   @Override
   public String getDestDisplayData() {
      return destModType.isDeleted() ? ARTIFACT_DELETED : ARTIFACT_MODIFIED;
   }

   @Override
   public String getMergeDisplayData() {
      return "";
   }

   protected Object getMergeValue() {
      return getArtifact();
   }

   @Override
   public String getSourceDisplayData() {
      return sourceModType.isDeleted() ? ARTIFACT_DELETED : ARTIFACT_MODIFIED;
   }

   @Override
   public boolean mergeEqualsDestination() {
      return getDestArtifact().equals(getMergeValue());
   }

   @Override
   public boolean mergeEqualsSource() {
      return getSourceArtifact().equals(getMergeValue());
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
      throw new BranchMergeException(
         "Artifact Conflicts can not be handled they must be reverted on the Source Branch");
   }

   @Override
   public boolean applyPreviousMerge(BranchId mergeBranchId, BranchId destBranchId) {
      return false;
   }

}
