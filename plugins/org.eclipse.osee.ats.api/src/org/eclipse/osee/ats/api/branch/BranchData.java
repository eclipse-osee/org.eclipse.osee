/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.branch;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class BranchData {

   String branchName;
   BranchId parent = BranchId.SENTINEL;
   boolean applyAccess = false;
   XResultData results = new XResultData();
   boolean validate = false;
   BranchType branchType;
   ArtifactToken associatedArt = ArtifactToken.SENTINEL;
   ArtifactToken author = ArtifactToken.SENTINEL;
   String creationComment;

   public String getBranchName() {
      return branchName;
   }

   public void setBranchName(String branchName) {
      this.branchName = branchName;
   }

   public BranchId getParent() {
      return parent;
   }

   public void setParent(BranchId parent) {
      this.parent = parent;
   }

   public boolean isApplyAccess() {
      return applyAccess;
   }

   public void setApplyAccess(boolean applyAccess) {
      this.applyAccess = applyAccess;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public boolean isValidate() {
      return validate;
   }

   public void setValidate(boolean validate) {
      this.validate = validate;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public void setBranchType(BranchType branchType) {
      this.branchType = branchType;
   }

   public ArtifactToken getAssociatedArt() {
      return associatedArt;
   }

   public void setAssociatedArt(ArtifactToken associatedArt) {
      this.associatedArt = associatedArt;
   }

   public ArtifactToken getAuthor() {
      return author;
   }

   public void setAuthor(ArtifactToken author) {
      this.author = author;
   }

   public String getCreationComment() {
      return creationComment;
   }

   public void setCreationComment(String creationComment) {
      this.creationComment = creationComment;
   }

}
