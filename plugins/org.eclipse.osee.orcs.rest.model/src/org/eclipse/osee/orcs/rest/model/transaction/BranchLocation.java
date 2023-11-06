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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;

public class BranchLocation {
   private BranchId branchId;
   private TransactionId baseTxId;
   private TransactionId uniqueTxId;

   public BranchId getBranchId() {
      return branchId;
   }

   public void setBranchId(BranchId branchId) {
      this.branchId = branchId;
   }

   public TransactionId getBaseTxId() {
      return baseTxId;
   }

   public void setBaseTxId(TransactionId baseTx) {
      this.baseTxId = baseTx;
   }

   public TransactionId getUniqueTxId() {
      return uniqueTxId;
   }

   public void setUniqueTxId(TransactionId baseTx) {
      this.uniqueTxId = baseTx;
   }
}
