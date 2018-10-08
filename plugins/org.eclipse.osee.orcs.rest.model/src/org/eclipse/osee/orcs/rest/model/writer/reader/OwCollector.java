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
package org.eclipse.osee.orcs.rest.model.writer.reader;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
public class OwCollector {

   private String instructions;
   private OwBranch branch;
   private BranchId branchId;
   private String persistComment;
   private String asUserId;
   private List<OwArtifact> create;
   private List<OwArtifact> update;
   private List<ArtifactToken> delete;
   private List<OwArtifactType> artTypes;
   private List<OwAttributeType> attrTypes;
   private List<OwRelationType> relTypes;
   private List<OwBranch> branches;
   private List<OwArtifactToken> artTokens;

   public OwCollector() {
      create = new LinkedList<>();
      branch = new OwBranch(BranchId.SENTINEL.getId(), "");
      branchId = BranchId.SENTINEL;
   }

   public List<OwArtifact> getCreate() {
      if (create == null) {
         create = new LinkedList<>();
      }
      return create;
   }

   public void setCreate(List<OwArtifact> artifacts) {
      this.create = artifacts;
   }

   public List<OwArtifactType> getArtTypes() {
      if (artTypes == null) {
         artTypes = new LinkedList<>();
      }
      return artTypes;
   }

   public void setArtTypes(List<OwArtifactType> artTypes) {
      this.artTypes = artTypes;
   }

   public List<OwAttributeType> getAttrTypes() {
      if (attrTypes == null) {
         attrTypes = new LinkedList<>();
      }
      return attrTypes;
   }

   public void setAttrTypes(List<OwAttributeType> attrTypes) {
      this.attrTypes = attrTypes;
   }

   public List<OwRelationType> getRelTypes() {
      if (relTypes == null) {
         relTypes = new LinkedList<>();
      }
      return relTypes;
   }

   public void setRelTypes(List<OwRelationType> relTypes) {
      this.relTypes = relTypes;
   }

   public List<OwArtifactToken> getArtTokens() {
      if (artTokens == null) {
         artTokens = new LinkedList<>();
      }
      return artTokens;
   }

   public void setArtTokens(List<OwArtifactToken> artTokens) {
      this.artTokens = artTokens;
   }

   public List<OwArtifact> getUpdate() {
      if (update == null) {
         update = new LinkedList<>();
      }
      return update;
   }

   public void setUpdate(List<OwArtifact> update) {
      this.update = update;
   }

   public List<ArtifactToken> getDelete() {
      if (delete == null) {
         delete = new LinkedList<>();
      }
      return delete;
   }

   public void setDelete(List<ArtifactToken> delete) {
      this.delete = delete;
   }

   public OwBranch getBranch() {
      return branch;
   }

   public BranchId getBranchId() {
      return branchId;
   }

   public void setBranch(OwBranch branch) {
      this.branch = branch;
   }

   public void setBranchId(BranchId branchId) {
      this.branchId = branchId;
   }

   public List<OwBranch> getBranches() {
      if (branches == null) {
         branches = new LinkedList<>();
      }
      return branches;
   }

   public void setBranches(List<OwBranch> branches) {
      this.branches = branches;
   }

   public String getInstructions() {
      return instructions;
   }

   public void setInstructions(String instructions) {
      this.instructions = instructions;
   }

   @Override
   public String toString() {
      return "OwCollector [branch=" + branch + ", create=" + create + ", update=" + update + ", delete=" + delete + "]";
   }

   public String getPersistComment() {
      return persistComment;
   }

   public void setPersistComment(String persistComment) {
      this.persistComment = persistComment;
   }

   public String getAsUserId() {
      return asUserId;
   }

   public void setAsUserId(String asUserId) {
      this.asUserId = asUserId;
   }

}
