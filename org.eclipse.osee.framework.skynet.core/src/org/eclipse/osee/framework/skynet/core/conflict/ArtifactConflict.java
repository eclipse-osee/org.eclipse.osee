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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.BranchMergeException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Theron Virgin
 */
public class ArtifactConflict extends Conflict {
   private static final String CHANGE_ITEM = "Artifact State";
   private static final String ARTIFACT_DELETED = "DELETED";
   private static final String ARTIFACT_MODIFIED = "MODIFIED";
   private final boolean sourceDeleted;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param changeType
    * @param mergeBranch
    * @param sourceBranch
    * @param destBranch
    */
   public ArtifactConflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, Branch mergeBranch, Branch sourceBranch, Branch destBranch, int sourceModType, int destModType, int artTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, null, mergeBranch, sourceBranch, destBranch);
      sourceDeleted = (sourceModType == ModificationType.DELETED.getValue());
   }

   public String getArtifactName() throws OseeCoreException {
      if (sourceDeleted) {
         return getDestArtifact().getDescriptiveName();
      } else {
         return getSourceArtifact().getDescriptiveName();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }

      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#clearValue()
    */
   @Override
   public boolean clearValue() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#computeStatus()
    */
   public ConflictStatus computeStatus() throws OseeCoreException {
      if (!sourceDeleted)
         return super.computeStatus(getObjectId(), ConflictStatus.NOT_RESOLVABLE);
      else
         return super.computeStatus(getObjectId(), ConflictStatus.INFORMATIONAL);

   }

   public int getObjectId() throws OseeCoreException {
      return getArtifact().getArtId();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getChangeItem()
    */
   @Override
   public String getChangeItem() {
      return CHANGE_ITEM;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getConflictType()
    */
   @Override
   public ConflictType getConflictType() {
      return ConflictType.ARTIFACT;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getDestDisplayData()
    */
   @Override
   public String getDestDisplayData() {
      if (sourceDeleted) {
         return ARTIFACT_MODIFIED;
      } else {
         return ARTIFACT_DELETED;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getImage()
    */
   @Override
   public Image getImage() {
      return SkynetActivator.getInstance().getImage("laser_16_16.gif");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getMergeDisplayData()
    */
   @Override
   public String getMergeDisplayData() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getMergeValue()
    */
   protected Object getMergeValue() throws OseeCoreException {
      return getArtifact();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#getSourceDisplayData()
    */
   @Override
   public String getSourceDisplayData() {
      if (sourceDeleted) {
         return ARTIFACT_DELETED;
      } else {
         return ARTIFACT_MODIFIED;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#mergeEqualsDestination()
    */
   @Override
   public boolean mergeEqualsDestination() throws OseeCoreException {
      return getDestArtifact().equals(getMergeValue());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#mergeEqualsSource()
    */
   @Override
   public boolean mergeEqualsSource() throws OseeCoreException {
      return getSourceArtifact().equals(getMergeValue());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setToDest()
    */
   @Override
   public boolean setToDest() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#setToSource()
    */
   @Override
   public boolean setToSource() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#sourceEqualsDestination()
    */
   @Override
   public boolean sourceEqualsDestination() {
      return false;
   }

   public void revertSourceArtifact() throws OseeCoreException {
      getSourceArtifact().revert();
   }

   public int getMergeGammaId() throws BranchMergeException {
      throw new BranchMergeException("Artifact Conflicts can not be handled they must be reverted on the Source Branch");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.Conflict#applyPreviousMerge(int)
    */
   @Override
   public boolean applyPreviousMerge(int mergeBranchId, int destBranchId) throws OseeCoreException {
      return false;
   }

}
