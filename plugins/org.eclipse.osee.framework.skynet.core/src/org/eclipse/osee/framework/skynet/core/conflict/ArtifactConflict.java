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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchMergeException;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Theron Virgin
 */
public class ArtifactConflict extends Conflict {
   private static final String CHANGE_ITEM = "Artifact State";
   private static final String ARTIFACT_DELETED = "DELETED";
   private static final String ARTIFACT_MODIFIED = "MODIFIED";
   private final ModificationType sourceModType;
   private final ModificationType destModType;

   public ArtifactConflict(int sourceGamma, int destGamma, ArtifactId artId, TransactionToken toTransactionId, BranchId mergeBranch, IOseeBranch sourceBranch, IOseeBranch destBranch, ModificationType sourceModType, ModificationType destModType, long artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, null, mergeBranch, sourceBranch, destBranch);
      this.sourceModType = sourceModType;
      this.destModType = destModType;
   }

   @Override
   public String getArtifactName()  {
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
   public ConflictStatus computeStatus()  {
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

   protected Object getMergeValue()  {
      return getArtifact();
   }

   @Override
   public String getSourceDisplayData() {
      return sourceModType.isDeleted() ? ARTIFACT_DELETED : ARTIFACT_MODIFIED;
   }

   @Override
   public boolean mergeEqualsDestination()  {
      return getDestArtifact().equals(getMergeValue());
   }

   @Override
   public boolean mergeEqualsSource()  {
      return getSourceArtifact().equals(getMergeValue());
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
   public int getMergeGammaId() throws BranchMergeException {
      throw new BranchMergeException(
         "Artifact Conflicts can not be handled they must be reverted on the Source Branch");
   }

   @Override
   public boolean applyPreviousMerge(BranchId mergeBranchId, BranchId destBranchId) {
      return false;
   }

}
