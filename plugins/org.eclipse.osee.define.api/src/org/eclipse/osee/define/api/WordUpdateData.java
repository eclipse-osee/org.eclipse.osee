/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author David W. Miller
 */
public class WordUpdateData {
   private byte[] wordData;
   private UserId userArtId;
   private List<Long> artifacts;
   private BranchId branch;
   private boolean threeWayMerge;
   private String comment;
   private boolean multiEdit;

   public byte[] getWordData() {
      return wordData;
   }

   public void setWordData(byte[] wordData) {
      this.wordData = wordData;
   }

   public UserId getUserArtId() {
      return userArtId;
   }

   public void setUserArtId(UserId userArtId) {
      this.userArtId = UserId.valueOf(userArtId.getId());
   }

   public List<Long> getArtifacts() {
      return artifacts;
   }

   public void setArtifacts(List<Long> artifacts) {
      this.artifacts = artifacts;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public boolean isThreeWayMerge() {
      return threeWayMerge;
   }

   public void setThreeWayMerge(boolean threeWayMerge) {
      this.threeWayMerge = threeWayMerge;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public boolean isMultiEdit() {
      return multiEdit;
   }

   public void setMultiEdit(boolean multiEdit) {
      this.multiEdit = multiEdit;
   }

}
