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

import java.util.Collections;
import java.util.List;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class BranchExportOptions {

   private List<BranchId> branchIds;
   private long minTx = -1L;
   private long maxTx = -1L;
   private String filename;
   private boolean compress;

   public List<BranchId> getBranchUuids() {
      return branchIds != null ? branchIds : Collections.emptyList();
   }

   public long getMinTx() {
      return minTx;
   }

   public long getMaxTx() {
      return maxTx;
   }

   public String getFileName() {
      return filename;
   }

   public boolean isCompress() {
      return compress;
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

   public void setFileName(String filename) {
      this.filename = filename;
   }

   public void setCompress(boolean compress) {
      this.compress = compress;
   }

}
