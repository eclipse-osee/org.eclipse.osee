/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;

/**
 * @author Ryan D. Brooks
 */
public final class Address implements Comparable<Address> {
   private final BranchId branch;
   private final int itemId;
   private final Long transactionId;
   private final GammaId gammaId;
   private ModificationType modType;
   private ApplicabilityId appId;
   private final TxChange txCurrent;
   private final boolean isBaseline;
   private TxChange correctedTxCurrent;
   private boolean purge;

   public Address(boolean isBaseline, BranchId branch, int itemId, Long transactionId, GammaId gammaId, ModificationType modType, ApplicabilityId appId, TxChange txCurrent) {
      this.branch = branch;
      this.itemId = itemId;
      this.transactionId = transactionId;
      this.gammaId = gammaId;
      this.modType = modType;
      this.appId = appId;
      this.txCurrent = txCurrent;
      this.isBaseline = isBaseline;
   }

   public boolean isBaselineTx() {
      return isBaseline;
   }

   public boolean isSimilar(Address other) {
      return other != null && other.itemId == itemId && other.branch.equals(branch);
   }

   public boolean isSameTransaction(Address other) {
      return other != null && transactionId == other.transactionId;
   }

   public boolean hasSameGamma(Address other) {
      return other != null && gammaId == other.gammaId;
   }

   public boolean hasSameModType(Address other) {
      return modType == other.modType;
   }

   public boolean hasSameApplicability(Address other) {
      return appId.equals(other.appId);
   }

   public void ensureCorrectCurrent() {
      TxChange correctCurrent = TxChange.getCurrent(modType);
      if (txCurrent != correctCurrent) {
         correctedTxCurrent = correctCurrent;
      }
   }

   public void ensureNotCurrent() {
      if (txCurrent != TxChange.NOT_CURRENT) {
         correctedTxCurrent = TxChange.NOT_CURRENT;
      }
   }

   public boolean hasIssue() {
      return purge || correctedTxCurrent != null;
   }

   public TxChange getCorrectedTxCurrent() {
      return correctedTxCurrent;
   }

   public void setCorrectedTxCurrent(TxChange correctedTxCurrent) {
      this.correctedTxCurrent = correctedTxCurrent;
   }

   public boolean isPurge() {
      return purge;
   }

   public void setPurge(boolean purge) {
      this.purge = purge;
   }

   public BranchId getBranch() {
      return branch;
   }

   public int getItemId() {
      return itemId;
   }

   public Long getTransactionId() {
      return transactionId;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public ApplicabilityId getApplicabilityId() {
      return appId;
   }

   public void setApplicabilityId(ApplicabilityId appId) {
      this.appId = appId;
   }

   public TxChange getTxCurrent() {
      return txCurrent;
   }

   public boolean isBaseline() {
      return isBaseline;
   }

   @Override
   public String toString() {
      return "Address [branchUuid=" + branch + ", gammaId=" + gammaId + ", itemId=" + itemId + ", modType=" + modType + ", transactionId=" + transactionId + ", txCurrent=" + txCurrent + "]";
   }

   @Override
   public int compareTo(Address otherAddress) {
      if (transactionId != otherAddress.transactionId) {
         return Long.compare(transactionId, otherAddress.transactionId);
      } else {
         return Long.compare(gammaId.getId(), otherAddress.gammaId.getId());
      }
   }
}