/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Donald G. Dunne
 */
public abstract class TransactionRow {

   protected GammaId gammaId = GammaId.SENTINEL;
   protected BranchId branch = BranchId.SENTINEL;
   protected ModificationType modType = ModificationType.SENTINEL;
   protected TxCurrent txCurrent = TxCurrent.SENTINEL;
   protected TransactionId tx = TransactionId.SENTINEL;
   protected TransactionDetails txd;
   protected boolean dereferenced = false;

   public TransactionRow() {
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   abstract public Id getItemId();

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public ModificationType getModType() {
      return modType;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public TxCurrent getTxCurrent() {
      return txCurrent;
   }

   public void setTxCurrent(TxCurrent txCurrent) {
      this.txCurrent = txCurrent;
   }

   public TransactionId getTx() {
      return tx;
   }

   public void setTx(TransactionId tx) {
      this.tx = tx;
   }

   public TransactionDetails getTxd() {
      return txd;
   }

   public void setTxd(TransactionDetails txd) {
      this.txd = txd;
   }

   public boolean isDereferenced() {
      return dereferenced;
   }

   public void setDereferenced(boolean dereferenced) {
      this.dereferenced = dereferenced;
   }

}
