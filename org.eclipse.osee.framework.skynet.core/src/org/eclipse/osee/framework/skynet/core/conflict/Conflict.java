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

import java.sql.SQLException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public abstract class Conflict implements IAdaptable {
   public static enum Status {

      UNTOUCHED(1), EDITED(2), RESOLVED(3), OUT_OF_DATE(4), NOT_RESOLVABLE(5), COMMITTED(6), INFORMATIONAL(7);
      private final int value;

      Status(int value) {
         this.value = value;
      }

      public final int getValue() {
         return value;
      }

      public static Status getStatus(int value) {
         for (Status status : values()) {
            if (status.value == value) return status;
         }
         return null;
      }
   };

   public static enum ConflictType {
      ATTRIBUTE(1), RELATION(2), ARTIFACT(3);
      private final int value;

      ConflictType(int value) {
         this.value = value;
      }

      public final int Value() {
         return value;
      }
   };

   protected Status status;
   protected int sourceGamma;
   protected int destGamma;
   private int artId;
   private TransactionId toTransactionId;
   private TransactionId commitTransactionId;
   private Artifact artifact;
   private Artifact sourceArtifact;
   private Artifact destArtifact;
   protected Branch mergeBranch;
   protected Branch sourceBranch;
   protected Branch destBranch;

   private String sourceDiffFile = null;
   private String destDiffFile = null;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param changeType
    */
   public Conflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId commitTransactionId, Branch mergeBranch, Branch sourceBranch, Branch destBranch) {
      super();
      this.sourceGamma = sourceGamma;
      this.destGamma = destGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.mergeBranch = mergeBranch;
      this.commitTransactionId = commitTransactionId;
   }

   public Conflict(int sourceGamma, int destGamma, int artId, TransactionId commitTransactionId, Branch mergeBranch, Branch destBranch) {
      this(sourceGamma, destGamma, artId, null, commitTransactionId, mergeBranch, null, destBranch);
   }

   /**
    * @return the artifact
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getArtifact() throws OseeCoreException, SQLException {
      if (artifact == null) {
         artifact = ArtifactCache.getActive(artId, mergeBranch.getBranchId());
         if (artifact == null) {
            artifact = ArtifactQuery.getArtifactFromId(artId, mergeBranch, true);
         }
      }
      return artifact;
   }

   /**
    * @return the artifact
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getSourceArtifact() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, OseeCoreException {
      if (sourceArtifact == null) {
         if (commitTransactionId == null) {
            sourceArtifact = ArtifactCache.getActive(artId, sourceBranch.getBranchId());
            if (sourceArtifact == null) {
               sourceArtifact = ArtifactQuery.getArtifactFromId(artId, sourceBranch, true);
            }
         } else {
            sourceArtifact =
                  ArtifactPersistenceManager.getInstance().getArtifactFromId(artId,
                        TransactionIdManager.getStartEndPoint(mergeBranch).getKey());
         }
      }
      return sourceArtifact;
   }

   /**
    * @return the artifact
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getDestArtifact() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, OseeCoreException {
      if (destArtifact == null) {
         if (commitTransactionId == null) {
            destArtifact = ArtifactCache.getActive(artId, destBranch.getBranchId());
            if (destArtifact == null) {
               destArtifact = ArtifactQuery.getArtifactFromId(artId, destBranch, true);
            }
         } else {
            destArtifact =
                  ArtifactPersistenceManager.getInstance().getArtifactFromId(artId,
                        TransactionIdManager.getPriorTransaction(commitTransactionId));

         }
      }
      return destArtifact;
   }

   public Branch getMergeBranch() {
      return mergeBranch;
   }

   public Branch getSourceBranch() {
      return sourceBranch;
   }

   public Branch getDestBranch() {
      return destBranch;
   }

   /**
    * @return the sourceGamma
    */
   public int getSourceGamma() {
      return sourceGamma;
   }

   /**
    * @param sourceGamma the sourceGamma to set
    */
   public void setSourceGamma(int sourceGamma) {
      this.sourceGamma = sourceGamma;
   }

   /**
    * @return the destGamma
    */
   public int getDestGamma() {
      return destGamma;
   }

   /**
    * @param destGamma the destGamma to set
    */
   public void setDestGamma(int destGamma) {
      this.destGamma = destGamma;
   }

   /**
    * @return the artId
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @param artId the artId to set
    */
   public void setArtId(int artId) {
      this.artId = artId;
   }

   /**
    * @return the toTransactionId
    */
   public TransactionId getToTransactionId() {
      return toTransactionId;
   }

   /**
    * @return the toTransactionId
    */
   public TransactionId getCommitTransactionId() {
      return commitTransactionId;
   }

   public Image getArtifactImage() throws OseeCoreException, SQLException {
      return getArtifact().getArtifactType().getImage();
   }

   public boolean okToOverwriteMerge() throws OseeCoreException {
      if (status.equals(Status.RESOLVED) || status.equals(Status.COMMITTED)) {
         return false;
      }
      return true;
   }

   public abstract Status computeStatus() throws OseeCoreException, SQLException;

   public Status computeStatus(int objectID, Status DefaultStatus) throws OseeCoreException, SQLException {
      Status passedStatus = DefaultStatus;
      try {
         if (sourceEqualsDestination() && mergeEqualsSource()) passedStatus = Status.RESOLVED;
      } catch (AttributeDoesNotExist ex) {
      }
      status =
            ConflictStatusManager.computeStatus(sourceGamma, destGamma, mergeBranch.getBranchId(), objectID,
                  getConflictType().Value(), passedStatus,
                  TransactionIdManager.getStartEndPoint(mergeBranch).getKey().getTransactionNumber(),
                  this instanceof AttributeConflict ? ((AttributeConflict) this).getAttrId() : 0);
      return status;
   }

   public void setStatus(Status status) throws OseeCoreException, SQLException {
      if (this.status.equals(status)) return;
      ConflictStatusManager.setStatus(status, sourceGamma, destGamma);
      this.status = status;
   }

   public boolean statusUntouched() {
      return status.equals(Status.UNTOUCHED);
   }

   public boolean statusResolved() {
      return status.equals(Status.RESOLVED);
   }

   public boolean statusCommitted() {
      return status.equals(Status.COMMITTED);
   }

   public boolean statusEdited() {
      return status.equals(Status.EDITED);
   }

   public boolean statusOutOfDate() {
      return status.equals(Status.OUT_OF_DATE);
   }

   public boolean statusNotResolvable() {
      return status.equals(Status.NOT_RESOLVABLE);
   }

   public boolean statusInformational() {
      return status.equals(Status.INFORMATIONAL);
   }

   public boolean statusEditable() {
      return !(status.equals(Status.RESOLVED) || status.equals(Status.COMMITTED) || status.equals(Status.INFORMATIONAL) || status.equals(Status.NOT_RESOLVABLE));
   }

   public int getMergeBranchID() {
      return mergeBranch.getBranchId();
   }

   public String getArtifactName() throws OseeCoreException, SQLException {
      return getArtifact().getDescriptiveName();
   }

   public void handleResolvedSelection() throws Exception {
      if (status.equals(Conflict.Status.EDITED)) {
         setStatus(Conflict.Status.RESOLVED);
      } else if (status.equals(Conflict.Status.RESOLVED)) {
         setStatus(Conflict.Status.EDITED);
      } else if (status.equals(Conflict.Status.OUT_OF_DATE)) {
         setStatus(Conflict.Status.RESOLVED);
      }
   }

   /**
    * @return the sourceDiffFile
    */
   public String getSourceDiffFile() {
      return sourceDiffFile;
   }

   /**
    * @param sourceDiffFile the sourceDiffFile to set
    */
   public void setSourceDiffFile(String sourceDiffFile) {
      this.sourceDiffFile = sourceDiffFile;
   }

   /**
    * @return the destDiffFile
    */
   public String getDestDiffFile() {
      return destDiffFile;
   }

   /**
    * @param destDiffFile the destDiffFile to set
    */
   public void setDestDiffFile(String destDiffFile) {
      this.destDiffFile = destDiffFile;
   }

   public Status getStatus() {
      return status;
   }

   public abstract Image getImage() throws SQLException;

   public abstract String getSourceDisplayData() throws OseeCoreException, SQLException;

   public abstract String getDestDisplayData() throws OseeCoreException, SQLException;

   public abstract boolean mergeEqualsSource() throws OseeCoreException, SQLException;

   public abstract boolean mergeEqualsDestination() throws OseeCoreException, SQLException;

   public abstract boolean sourceEqualsDestination() throws OseeCoreException, SQLException;

   public abstract boolean setToSource() throws OseeCoreException, SQLException;

   public abstract boolean setToDest() throws OseeCoreException, SQLException;

   public abstract boolean clearValue() throws OseeCoreException, SQLException;

   public abstract String getMergeDisplayData() throws OseeCoreException, SQLException;

   public abstract String getChangeItem() throws OseeCoreException, SQLException;

   public abstract ConflictType getConflictType();

   public abstract int getMergeGammaId() throws OseeCoreException, SQLException;
}
