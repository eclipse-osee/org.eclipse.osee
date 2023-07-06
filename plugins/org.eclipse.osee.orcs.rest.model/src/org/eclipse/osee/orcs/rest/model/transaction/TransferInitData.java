/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.orcs.rest.model.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"branchIds", "baseTxId", "exportId", "results"})
public class TransferInitData {

   @JsonProperty("branchIds")
   private List<BranchId> branchIds;
   @JsonProperty("exportId")
   private TransactionId exportId;
   @JsonProperty("baseTxId")
   private TransactionId baseTxId;
   @JsonProperty("transferDBType")
   private TransferDBType transferDBType;
   @JsonProperty("results")
   private XResultData results;

   public List<BranchId> getBranchIds() {
      return branchIds;
   }

   public void setBranchIds(List<BranchId> branchIds) {
      this.branchIds = branchIds;
   }

   public TransactionId getExportId() {
      return exportId;
   }

   public void setExportId(TransactionId exportId) {
      this.exportId = exportId;
   }

   public TransactionId getBaseTxId() {
      return baseTxId;
   }

   public void setBaseTxId(TransactionId baseTxId) {
      this.baseTxId = baseTxId;
   }

   public TransferDBType getTransferDBType() {
      return transferDBType;
   }

   public void setTransferDBType(TransferDBType transferDBType) {
      this.transferDBType = transferDBType;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

}
