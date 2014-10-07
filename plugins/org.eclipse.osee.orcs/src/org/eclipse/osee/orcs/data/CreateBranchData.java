/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchData implements Identifiable<Long> {

   private static final int NULL_ARTIFACT_ID = -1;
   private final long MERGE_DESTINATION_BRANCH_ID = -1; // only used on merge branches
   private final int MERGE_ADDRESSING_QUERY_ID = -1; // only used on merge branches

   private String branchName;
   private long branchUuid;
   private BranchType branchType;
   private String creationComment;
   private ITransaction fromTransaction;
   private ITransaction savedTransaction;
   private ArtifactReadable associatedArtifact;
   private ArtifactReadable userArtifact;

   // Merge Branch Legacy Support
   private int mergeAddressingQueryId = MERGE_ADDRESSING_QUERY_ID;
   private long mergeDestinationBranchId = MERGE_DESTINATION_BRANCH_ID;

   private boolean txCopyBranchType = false;
   private Long parentBranchUuid;

   public CreateBranchData() {
      this(Lib.generateUuid());
   }

   public CreateBranchData(long uuid) {
      branchUuid = uuid;
   }

   @Override
   public Long getGuid() {
      return branchUuid;
   }

   public int getAssociatedArtifactId() {
      int result = NULL_ARTIFACT_ID;
      if (associatedArtifact != null) {
         result = associatedArtifact.getLocalId();
      }
      return result;
   }

   public Integer getUserArtifactId() {
      int result = NULL_ARTIFACT_ID;
      if (userArtifact != null) {
         result = userArtifact.getLocalId();
      }
      return result;
   }

   @Override
   public String getName() {
      return branchName;
   }

   public void setName(String branchName) {
      this.branchName = branchName;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public void setBranchType(BranchType branchType) {
      this.branchType = branchType;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public void setCreationComment(String creationComment) {
      this.creationComment = creationComment;
   }

   public ITransaction getFromTransaction() {
      return fromTransaction;
   }

   public void setFromTransaction(ITransaction fromTransaction) {
      this.fromTransaction = fromTransaction;
   }

   public ITransaction getSavedTransaction() {
      return savedTransaction;
   }

   public void setSavedTransaction(ITransaction priorTransaction) {
      this.savedTransaction = priorTransaction;
   }

   public ArtifactReadable getUserArtifact() {
      return userArtifact;
   }

   public void setUserArtifact(ArtifactReadable userArtifact) {
      this.userArtifact = userArtifact;
   }

   public ArtifactReadable getAssociatedArtifact() {
      return associatedArtifact;
   }

   public void setAssociatedArtifact(ArtifactReadable associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public int getMergeAddressingQueryId() {
      return mergeAddressingQueryId;
   }

   public void setMergeAddressingQueryId(int mergeAddressingQueryId) {
      this.mergeAddressingQueryId = mergeAddressingQueryId;
   }

   public long getMergeDestinationBranchId() {
      return mergeDestinationBranchId;
   }

   public void setMergeDestinationBranchId(long destinationBranchId) {
      this.mergeDestinationBranchId = destinationBranchId;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Identity) {
         return getGuid().equals(((Identity<?>) obj).getGuid());
      }
      return false;
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   public boolean isTxCopyBranchType() {
      return txCopyBranchType;
   }

   public void setTxCopyBranchType(boolean value) {
      txCopyBranchType = value;
   }

   @Override
   public String toString() {
      return "CreateBranchData [branchUuid=" + branchUuid + ", branchName=" + branchName + ", branchType=" + branchType + ", creationComment=" + creationComment + ", fromTransaction=" + fromTransaction + ", associatedArtifact=" + associatedArtifact + ", userArtifact=" + userArtifact + ", mergeAddressingQueryId=" + mergeAddressingQueryId + ", destinationBranchId=" + mergeDestinationBranchId + "]";
   }

   public long getUuid() {
      return branchUuid;
   }

   public void setUuid(long uuid) {
      if (uuid <= 0) {
         throw new OseeStateException("uuid [%d] must be > 0", uuid);
      }
      this.branchUuid = uuid;
   }

   public void setParentBranchUuid(Long uuid) {
      this.parentBranchUuid = uuid;
   }

   public Long getParentBranchUuid() {
      return parentBranchUuid;
   }

}
