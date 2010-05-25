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
package org.eclipse.osee.framework.database.operation;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;

/**
 * @author Ryan D. Brooks
 */
public final class Address implements Comparable<Address> {
   final int branchId;
   final int itemId;
   final int transactionId;
   final long gammaId;
   final ModificationType modType;
   final TxChange txCurrent;
   final boolean isBaseline;
   TxChange correctedTxCurrent;
   boolean purge;

   public Address(boolean isBaseline, int branchId, int itemId, int transactionId, long gammaId, ModificationType modType, TxChange txCurrent) {
      super();
      this.branchId = branchId;
      this.itemId = itemId;
      this.transactionId = transactionId;
      this.gammaId = gammaId;
      this.modType = modType;
      this.txCurrent = txCurrent;
      this.isBaseline = isBaseline;
   }

   public boolean isBaselineTx() {
      return isBaseline;
   }

   public boolean isSimilar(Address other) {
      return other != null && other.itemId == itemId && other.branchId == branchId;
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

   @Override
   public String toString() {
      return "Address [branchId=" + branchId + ", gammaId=" + gammaId + ", itemId=" + itemId + ", modType=" + modType + ", transactionId=" + transactionId + ", txCurrent=" + txCurrent + "]";
   }

   @Override
   public int compareTo(Address otherAddress) {
      if (transactionId != otherAddress.transactionId) {
         return transactionId - otherAddress.transactionId;
      } else {
         return (int) (gammaId - otherAddress.gammaId);
      }
   }
}