/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class BranchImportOptions {

   private String exchangeFile;
   private List<BranchId> branchIds;
   private long minTx = -1L;
   private long maxTx = -1L;
   private boolean cleanBeforeImport;
   private boolean allAsRootBranches;
   private boolean excludeBaselineTxs;
   private boolean useIdsFromImportFile;

   public String getExchangeFile() {
      return exchangeFile;
   }

   public void setExchangeFile(String exchangeFile) {
      this.exchangeFile = exchangeFile;
   }

   public List<BranchId> getBranchUuids() {
      return branchIds != null ? branchIds : Collections.emptyList();
   }

   public long getMinTx() {
      return minTx;
   }

   public long getMaxTx() {
      return maxTx;
   }

   public boolean isCleanBeforeImport() {
      return cleanBeforeImport;
   }

   public boolean isAllAsRootBranches() {
      return allAsRootBranches;
   }

   public boolean isExcludeBaselineTxs() {
      return excludeBaselineTxs;
   }

   public boolean isUseIdsFromImportFile() {
      return useIdsFromImportFile;
   }

   public void setBranchUuids(List<BranchId> branchIds) {
      this.branchIds = branchIds;
   }

   public void setMinTx(long minTx) {
      this.minTx = minTx;
   }

   public void setMaxTx(long maxTx) {
      this.maxTx = maxTx;
   }

   public void setCleanBeforeImport(boolean cleanBeforeImport) {
      this.cleanBeforeImport = cleanBeforeImport;
   }

   public void setAllAsRootBranches(boolean allAsRootBranches) {
      this.allAsRootBranches = allAsRootBranches;
   }

   public void setExcludeBaselineTxs(boolean excludeBaselineTxs) {
      this.excludeBaselineTxs = excludeBaselineTxs;
   }

   public void setUseIdsFromImportFile(boolean useIdsFromImportFile) {
      this.useIdsFromImportFile = useIdsFromImportFile;
   }

}
