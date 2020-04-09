/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mbse.cameo.browser;

/**
 * @author David W. Miller
 */
public class BranchData {
   private long id;
   private String name;
   private int viewId;
   private long associatedArtifact;
   private long baselineTx;
   private long parentTx;
   private ParentBranch parentBranch;
   private int branchState;
   private int branchType;
   private boolean archived;
   private String shortName;
   private int idIntValue;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getViewId() {
      return viewId;
   }

   public void setViewId(int viewId) {
      this.viewId = viewId;
   }

   public long getAssociatedArtifact() {
      return associatedArtifact;
   }

   public void setAssociatedArtifact(long associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public long getBaselineTx() {
      return baselineTx;
   }

   public void setBaselineTx(long baselineTx) {
      this.baselineTx = baselineTx;
   }

   public long getParentTx() {
      return parentTx;
   }

   public void setParentTx(long parentTx) {
      this.parentTx = parentTx;
   }

   public ParentBranch getParentBranch() {
      return parentBranch;
   }

   public void setParentBranch(ParentBranch parentBranch) {
      this.parentBranch = parentBranch;
   }

   public int getBranchState() {
      return branchState;
   }

   public void setBranchState(int branchState) {
      this.branchState = branchState;
   }

   public int getBranchType() {
      return branchType;
   }

   public void setBranchType(int branchType) {
      this.branchType = branchType;
   }

   public boolean isArchived() {
      return archived;
   }

   public void setArchived(boolean archived) {
      this.archived = archived;
   }

   public String getShortName() {
      return shortName;
   }

   public void setShortName(String shorName) {
      this.shortName = shorName;
   }

   public int getIdIntValue() {
      return idIntValue;
   }

   public void setIdIntValue(int idIntValue) {
      this.idIntValue = idIntValue;
   }

   @Override
   public String toString() {
      return String.format("%s-%d", getName(), getId());
   }
}
