/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.io.File;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;

public class ManifestData {

   private BranchId branchId;
   private TransactionId localTx;
   private TransactionId uniqueTx;
   private TransferOpType transferOpType;
   private int index;
   private String transferDirectoryName;
   private String fileName;
   private String dirName;

   public BranchId getBranchId() {
      return branchId;
   }

   public void setBranchId(BranchId branchId) {
      this.branchId = branchId;
   }

   public TransactionId getLocalTx() {
      return localTx;
   }

   public void setLocalTx(TransactionId localTx) {
      this.localTx = localTx;
   }

   public TransactionId getUniqueTx() {
      return uniqueTx;
   }

   public void setUniqueTx(TransactionId uniqueTx) {
      this.uniqueTx = uniqueTx;
   }

   public TransferOpType getTransferOpType() {
      return transferOpType;
   }

   public void setTransferOpType(TransferOpType transferOpType) {
      this.transferOpType = transferOpType;
   }

   public String getTransferDirectoryName() {
      return transferDirectoryName;
   }

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public void setTransferDirectoryName(String transferDirectoryName) {
      this.transferDirectoryName = transferDirectoryName;
   }

   public String getFileName() {
      return fileName;
   }

   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   public String getDirName() {
      return fileName;
   }

   public void setDirName(String dirName) {
      this.dirName = dirName;
   }

   // Constructor
   public ManifestData() {
      if (this.branchId == null) {
         this.branchId = BranchId.SENTINEL;
      }
      if (this.localTx == null) {
         this.localTx = TransactionId.SENTINEL;
      }
      if (this.uniqueTx == null) {
         this.uniqueTx = TransactionId.SENTINEL;
      }
      if (this.transferOpType == null) {
         this.transferOpType = TransferOpType.EMPTY;
      }
      this.index = 0;
      if (this.transferDirectoryName == null) {
         this.transferDirectoryName = File.separator + "transfers" + File.separator;
      }
   }
}