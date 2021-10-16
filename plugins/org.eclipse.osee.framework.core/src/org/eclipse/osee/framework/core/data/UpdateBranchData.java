/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Dominic Guss
 */
public class UpdateBranchData {
   private XResultData results = new XResultData();
   private BranchId fromBranch = BranchId.SENTINEL;
   private BranchId toBranch = BranchId.SENTINEL;
   private BranchId newBranchId = BranchId.SENTINEL;
   private String fromName;
   private String toName;
   private boolean needsMerge;

   public BranchId getFromBranch() {
      return fromBranch;
   }

   public void setFromBranch(BranchId fromBranch) {
      this.fromBranch = fromBranch;
   }

   public BranchId getToBranch() {
      return toBranch;
   }

   public void setToBranch(BranchId toBranch) {
      this.toBranch = toBranch;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public boolean isNeedsMerge() {
      return needsMerge;
   }

   public void setNeedsMerge(boolean needsMerge) {
      this.needsMerge = needsMerge;
   }

   public String getFromName() {
      return fromName;
   }

   public void setFromName(String fromName) {
      this.fromName = fromName;
   }

   public String getToName() {
      return toName;
   }

   public void setToName(String toName) {
      this.toName = toName;
   }

   public BranchId getNewBranchId() {
      return newBranchId;
   }

   public void setNewBranchId(BranchId newBranchId) {
      this.newBranchId = newBranchId;
   }
}
