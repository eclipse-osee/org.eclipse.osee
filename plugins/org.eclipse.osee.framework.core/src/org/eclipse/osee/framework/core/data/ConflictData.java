/*********************************************************************
 * Copyright (c) 2024 Boeing
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

public class ConflictData {
   ArtifactId artId;
   ArtifactTypeToken artType;
   AttributeTypeId attrTypeId;
   AttributeId attrId;
   //working values
   TransactionId workingTxId;
   TxCurrent workingTxCurrent;
   ModificationType workingModType;
   GammaId workingGammaId;
   //destination current values
   TransactionId currentDestTxId;
   TxCurrent currentDestTxCurrent;
   ModificationType currentDestModType;
   GammaId currentDestGammaId;
   //baselineTx values
   TransactionId baselineTxTxId;
   TxCurrent baselineTxTxCurrent;
   ModificationType baselineTxModType;
   GammaId baselineTxGammaId;

   public ConflictData(ArtifactId artId, ArtifactTypeToken artType, AttributeTypeId attrTypeId, AttributeId attrId, TransactionId workingTxId, TxCurrent workingTxCurrent, ModificationType workingModType, GammaId workingGammaId, TransactionId currentDestTxId, TxCurrent currentDestTxCurrent, ModificationType currentDestModType, GammaId currentDestGammaId, TransactionId baselineTxTxId, TxCurrent baselineTxTxCurrent, ModificationType baselineTxModType, GammaId baselineTxGammaId) {
      this.artId = artId;
      this.artType = artType;
      this.attrTypeId = attrTypeId;
      this.attrId = attrId;
      this.workingTxId = workingTxId;
      this.workingTxCurrent = workingTxCurrent;
      this.workingModType = workingModType;
      this.workingGammaId = workingGammaId;
      this.currentDestTxId = currentDestTxId;
      this.currentDestTxCurrent = currentDestTxCurrent;
      this.currentDestModType = currentDestModType;
      this.currentDestGammaId = currentDestGammaId;
      this.baselineTxTxId = baselineTxTxId;
      this.baselineTxTxCurrent = baselineTxTxCurrent;
      this.baselineTxModType = baselineTxModType;
      this.baselineTxGammaId = baselineTxGammaId;
   }

   public ConflictData() {
      //for jax-rs
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public TransactionId getWorkingTxId() {
      return workingTxId;
   }

   public TxCurrent getWorkingTxCurrent() {
      return workingTxCurrent;
   }

   public ModificationType getWorkingModType() {
      return workingModType;
   }

   public GammaId getWorkingGammaId() {
      return workingGammaId;
   }

   public TransactionId getCurrentDestTxId() {
      return currentDestTxId;
   }

   public TxCurrent getCurrentDestTxCurrent() {
      return currentDestTxCurrent;
   }

   public ModificationType getCurrentDestModType() {
      return currentDestModType;
   }

   public GammaId getCurrentDestGammaId() {
      return currentDestGammaId;
   }

   public TransactionId getBaselineTxTxId() {
      return baselineTxTxId;
   }

   public TxCurrent getBaselineTxTxCurrent() {
      return baselineTxTxCurrent;
   }

   public ModificationType getBaselineTxModType() {
      return baselineTxModType;
   }

   public GammaId getBaselineTxGammaId() {
      return baselineTxGammaId;
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public AttributeTypeId getAttrTypeId() {
      return attrTypeId;
   }

   public void setAttrTypeId(AttributeTypeId attrTypeId) {
      this.attrTypeId = attrTypeId;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public void setAttrId(AttributeId attrId) {
      this.attrId = attrId;
   }

}
