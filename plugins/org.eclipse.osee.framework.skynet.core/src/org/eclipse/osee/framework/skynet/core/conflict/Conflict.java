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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public abstract class Conflict implements Adaptable {
   protected ConflictStatus status;
   protected int sourceGamma;
   protected int destGamma;
   private int artId;
   private final TransactionToken toTransactionId;
   private final TransactionToken commitTransactionId;
   private Artifact artifact;
   private Artifact sourceArtifact;
   private Artifact destArtifact;
   protected BranchId mergeBranch;
   protected IOseeBranch sourceBranch;
   protected IOseeBranch destBranch;

   private String sourceDiffFile = null;
   private String destDiffFile = null;

   protected Conflict(int sourceGamma, int destGamma, int artId, TransactionToken toTransactionId, TransactionToken commitTransactionId, BranchId mergeBranch, IOseeBranch sourceBranch, IOseeBranch destBranch) {
      this.sourceGamma = sourceGamma;
      this.destGamma = destGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.mergeBranch = mergeBranch;
      this.commitTransactionId = commitTransactionId;
   }

   public Artifact getArtifact() throws OseeCoreException {
      if (artifact == null) {
         artifact = ArtifactQuery.getArtifactFromId(artId, mergeBranch, INCLUDE_DELETED);
      }
      return artifact;
   }

   public Artifact getSourceArtifact() throws OseeCoreException {
      if (sourceArtifact == null) {
         if (commitTransactionId == null) {
            sourceArtifact = ArtifactQuery.getArtifactFromId(artId, sourceBranch, INCLUDE_DELETED);
         } else {
            TransactionRecord baseTx = BranchManager.getBaseTransaction(mergeBranch);
            sourceArtifact = ArtifactQuery.getHistoricalArtifactFromId(artId, baseTx, INCLUDE_DELETED);
         }
      }
      return sourceArtifact;
   }

   public Artifact getDestArtifact() throws OseeCoreException {
      if (destArtifact == null) {
         if (commitTransactionId == null) {
            destArtifact = ArtifactQuery.getArtifactFromId(artId, destBranch, INCLUDE_DELETED);
         } else {
            destArtifact = ArtifactQuery.getHistoricalArtifactFromId(artId,
               TransactionManager.getPriorTransaction(commitTransactionId), INCLUDE_DELETED);

         }
      }
      return destArtifact;
   }

   public BranchId getMergeBranch() {
      return mergeBranch;
   }

   public IOseeBranch getSourceBranch() {
      return sourceBranch;
   }

   public IOseeBranch getDestBranch() {
      return destBranch;
   }

   public int getSourceGamma() {
      return sourceGamma;
   }

   public void setSourceGamma(int sourceGamma) {
      this.sourceGamma = sourceGamma;
   }

   public int getDestGamma() {
      return destGamma;
   }

   public void setDestGamma(int destGamma) {
      this.destGamma = destGamma;
   }

   public int getArtId() {
      return artId;
   }

   public void setArtId(int artId) {
      this.artId = artId;
   }

   public TransactionToken getToTransactionId() {
      return toTransactionId;
   }

   public TransactionToken getCommitTransactionId() {
      return commitTransactionId;
   }

   public abstract ConflictStatus computeStatus() throws OseeCoreException;

   protected ConflictStatus computeStatus(int objectID, ConflictStatus defaultStatus) throws OseeCoreException {
      ConflictStatus passedStatus = defaultStatus;
      try {
         if (sourceEqualsDestination() && mergeEqualsSource()) {
            passedStatus = ConflictStatus.RESOLVED;
         }
      } catch (AttributeDoesNotExist ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      TransactionId baseTx = BranchManager.getBaseTransaction(mergeBranch);
      status = ConflictStatusManager.computeStatus(sourceGamma, destGamma, mergeBranch, objectID,
         getConflictType().getValue(), passedStatus, baseTx);
      return status;
   }

   public void setStatus(ConflictStatus status) throws OseeCoreException {
      if (this.status.equals(status)) {
         return;
      }
      ConflictStatusManager.setStatus(status, sourceGamma, destGamma, mergeBranch);
      this.status = status;
   }

   public String getArtifactName() throws OseeCoreException {
      return getArtifact().getName();
   }

   public void handleResolvedSelection() throws Exception {
      if (status.equals(ConflictStatus.EDITED)) {
         setStatus(ConflictStatus.RESOLVED);
      } else if (status.equals(ConflictStatus.RESOLVED)) {
         setStatus(ConflictStatus.EDITED);
      } else if (status.equals(ConflictStatus.OUT_OF_DATE_RESOLVED)) {
         setStatus(ConflictStatus.RESOLVED);
      } else if (status.equals(ConflictStatus.OUT_OF_DATE)) {
         setStatus(ConflictStatus.EDITED);
      } else if (status.equals(ConflictStatus.PREVIOUS_MERGE_APPLIED_SUCCESS)) {
         setStatus(ConflictStatus.RESOLVED);
      } else if (status.equals(ConflictStatus.PREVIOUS_MERGE_APPLIED_CAUTION)) {
         setStatus(ConflictStatus.EDITED);
      }
   }

   public String getSourceDiffFile() {
      return sourceDiffFile;
   }

   public void setSourceDiffFile(String sourceDiffFile) {
      this.sourceDiffFile = sourceDiffFile;
   }

   public String getDestDiffFile() {
      return destDiffFile;
   }

   public void setDestDiffFile(String destDiffFile) {
      this.destDiffFile = destDiffFile;
   }

   public ConflictStatus getStatus() {
      return status;
   }

   public void computeEqualsValues() throws OseeCoreException {
      // provided for subclass implementation
   }

   public abstract String getSourceDisplayData() throws OseeCoreException;

   public abstract String getDestDisplayData() throws OseeCoreException;

   public abstract boolean mergeEqualsSource() throws OseeCoreException;

   public abstract boolean mergeEqualsDestination() throws OseeCoreException;

   public abstract boolean sourceEqualsDestination() throws OseeCoreException;

   public abstract boolean setToSource() throws OseeCoreException;

   public abstract boolean setToDest() throws OseeCoreException;

   public abstract boolean clearValue() throws OseeCoreException;

   public abstract String getMergeDisplayData() throws OseeCoreException;

   public abstract String getChangeItem() throws OseeCoreException;

   public abstract ConflictType getConflictType();

   public abstract int getMergeGammaId() throws OseeCoreException;

   public abstract int getObjectId() throws OseeCoreException;

   public abstract boolean applyPreviousMerge(BranchId mergeBranchId, BranchId destBranchId);
}
