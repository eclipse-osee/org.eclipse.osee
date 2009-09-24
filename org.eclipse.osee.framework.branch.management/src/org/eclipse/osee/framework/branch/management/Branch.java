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
package org.eclipse.osee.framework.branch.management;

import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
public class Branch {
   private final String name;
   private final int parentBranchId;
   private final int associatedArtifactId;

   private final BranchType branchType;
   private final int parentTransactionId;
   private final String staticBranchName;
   private String branchGuid;
   private int branchId;
   private BranchState branchState;
   private int baseTransaction;

   public Branch(BranchType branchType, int parentTransactionId, int parentBranchId, String branchGuid, String name, int associatedArtifactId, String staticBranchName, int baseTransaction) {
      this.parentBranchId = parentBranchId;
      this.name = name;
      this.associatedArtifactId = associatedArtifactId;
      this.branchType = branchType;
      this.parentTransactionId = parentTransactionId;
      this.staticBranchName = staticBranchName;
      this.branchGuid = branchGuid;
      this.baseTransaction = baseTransaction;
   }

   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   public void setGuid(String guid) {
      this.branchGuid = guid;
   }

   public void setBranchState(BranchState branchState) {
      this.branchState = branchState;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public String getName() {
      return name;
   }

   public int getParentBranchId() {
      return parentBranchId;
   }

   public int getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public int getBranchId() {
      return branchId;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public int getParentTransactionId() {
      return parentTransactionId;
   }

   public String getStaticBranchName() {
      return staticBranchName;
   }

   public String getGuid() {
      return branchGuid;
   }

   public int getBaseTransaction() {
      return baseTransaction;
   }

   public void setBaseTransaction(int baseTransaction) {
      this.baseTransaction = baseTransaction;
   }

   @Override
   public String toString() {
      return "Branch [associatedArtifactId=" + associatedArtifactId + ", branchGuid=" + branchGuid + ", branchId=" + branchId + ", branchState=" + branchState + ", branchType=" + branchType + ", name=" + name + ", parentBranchId=" + parentBranchId + ", parentTransactionId=" + parentTransactionId + ", staticBranchName=" + staticBranchName + ", baseTransaction=" + baseTransaction + "]";
   }
}
