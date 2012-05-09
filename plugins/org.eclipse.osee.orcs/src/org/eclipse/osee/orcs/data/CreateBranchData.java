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
import org.eclipse.osee.framework.core.data.Identifiable;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public class CreateBranchData implements Identifiable {

   private String branchUuid;
   private String branchName;
   private BranchType branchType;
   private String creationComment;
   private ITransaction fromTransaction;
   private ReadableArtifact associatedArtifact;
   private ReadableArtifact userArtifact;

   // Merge Branch Legacy Support
   private int mergeAddressingQueryId;
   private int mergeDestinationBranchId;

   @Override
   public String getGuid() {
      return branchUuid;
   }

   public void setGuid(String branchUuid) {
      this.branchUuid = branchUuid;
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

   public ReadableArtifact getUserArtifact() {
      return userArtifact;
   }

   public void setUserArtifact(ReadableArtifact userArtifact) {
      this.userArtifact = userArtifact;
   }

   public ReadableArtifact getAssociatedArtifact() {
      return associatedArtifact;
   }

   public void setAssociatedArtifact(ReadableArtifact associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public int getMergeAddressingQueryId() {
      return mergeAddressingQueryId;
   }

   public void setMergeAddressingQueryId(int mergeAddressingQueryId) {
      this.mergeAddressingQueryId = mergeAddressingQueryId;
   }

   public int getMergeDestinationBranchId() {
      return mergeDestinationBranchId;
   }

   public void setMergeDestinationBranchId(int destinationBranchId) {
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

   @Override
   public String toString() {
      return "CreateBranchData [branchUuid=" + branchUuid + ", branchName=" + branchName + ", branchType=" + branchType + ", creationComment=" + creationComment + ", fromTransaction=" + fromTransaction + ", associatedArtifact=" + associatedArtifact + ", userArtifact=" + userArtifact + ", mergeAddressingQueryId=" + mergeAddressingQueryId + ", destinationBranchId=" + mergeDestinationBranchId + "]";
   }

}
