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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public abstract class Conflict implements IAdaptable {
   public static enum Status {
      UNTOUCHED(1), EDITED(2), RESOLVED(3), OUT_OF_DATE(4), NOT_RESOLVABLE(5);
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
   private TransactionId fromTransactionId;
   private Artifact artifact;
   private Artifact sourceArtifact;
   private Artifact destArtifact;
   private final ModificationType modType;
   private final ChangeType changeType;
   protected Branch mergeBranch;
   protected Branch sourceBranch;
   protected Branch destBranch;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param transactionType
    * @param changeType
    */
   public Conflict(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, ChangeType changeType, Branch mergeBranch, Branch sourceBranch, Branch destBranch) {
      super();
      this.sourceGamma = sourceGamma;
      this.destGamma = destGamma;
      this.artId = artId;
      this.toTransactionId = toTransactionId;
      this.fromTransactionId = fromTransactionId;
      this.modType = modType;
      this.changeType = changeType;
      this.sourceBranch = sourceBranch;
      this.destBranch = destBranch;
      this.mergeBranch = mergeBranch;
   }

   /**
    * @return the transactionType
    */
   public ModificationType getModificationType() {
      return modType;
   }

   /**
    * @return the changeType
    */
   public ChangeType getChangeType() {
      return changeType;
   }

   /**
    * @return the artifact
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getArtifact() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
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
   public Artifact getSourceArtifact() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      if (sourceArtifact == null) {
         sourceArtifact = ArtifactCache.getActive(artId, sourceBranch.getBranchId());
         if (sourceArtifact == null) {
            sourceArtifact = ArtifactQuery.getArtifactFromId(artId, sourceBranch, true);
         }
      }
      return sourceArtifact;
   }

   /**
    * @return the artifact
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Artifact getDestArtifact() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      if (destArtifact == null) {
         destArtifact = ArtifactCache.getActive(artId, destBranch.getBranchId());
         if (destArtifact == null) {
            destArtifact = ArtifactQuery.getArtifactFromId(artId, destBranch, true);
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
    * @param toTransactionId the toTransactionId to set
    */
   public void setToTransactionId(TransactionId toTransactionId) {
      this.toTransactionId = toTransactionId;
   }

   /**
    * @return the fromTransactionId
    */
   public TransactionId getFromTransactionId() {
      return fromTransactionId;
   }

   /**
    * @param fromTransactionId the fromTransactionId to set
    */
   public void setFromTransactionId(TransactionId fromTransactionId) {
      this.fromTransactionId = fromTransactionId;
   }

   public Image getArtifactImage() throws IllegalArgumentException, SQLException, Exception {
      return getArtifact().getArtifactType().getImage(getChangeType(), ModificationType.CHANGE);
   }

   public boolean okToOverwriteMerge() throws SQLException {
      if (status.equals(Status.RESOLVED)) {
         return false;
      }
      return true;
   }

   public abstract Status computeStatus() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public Status computeStatus(int objectID, Status DefaultStatus) throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception {
      Status passedStatus = DefaultStatus;
      if (sourceEqualsDestination() && mergeEqualsSource()) passedStatus = Status.RESOLVED;
      status =
            ConflictStatusManager.computeStatus(sourceGamma, destGamma, mergeBranch.getBranchId(), objectID,
                  getConflictType().Value(), passedStatus);
      return status;
   }

   public void setStatus(Status status) throws Exception {
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

   public boolean statusEdited() {
      return status.equals(Status.EDITED);
   }

   public boolean statusOutOfDate() {
      return status.equals(Status.OUT_OF_DATE);
   }

   public boolean statusNotResolvable() {
      return status.equals(Status.NOT_RESOLVABLE);
   }

   public int getMergeBranchID() {
      return mergeBranch.getBranchId();
   }

   public String getArtifactName() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
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

   public abstract Image getImage() throws SQLException;

   public abstract String getSourceDisplayData() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException;

   public abstract String getDestDisplayData() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException;

   public abstract boolean mergeEqualsSource() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public abstract boolean mergeEqualsDestination() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public abstract boolean sourceEqualsDestination() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public abstract boolean setToSource() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public abstract boolean setToDest() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public abstract boolean clearValue() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public abstract String getMergeDisplayData() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;

   public abstract String getChangeItem() throws SQLException, Exception;

   public abstract ConflictType getConflictType();

   public abstract int getMergeGammaId() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException, Exception;
}
