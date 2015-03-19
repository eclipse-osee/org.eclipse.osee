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
package org.eclipse.osee.orcs.rest.model;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class BranchImportOptions {

   private String exchangeFile;
   private List<Long> branchUuids;
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

   public List<Long> getBranchUuids() {
      return branchUuids != null ? branchUuids : Collections.<Long> emptyList();
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

   public void setBranchUuids(List<Long> branchUuids) {
      this.branchUuids = branchUuids;
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
