/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.commit;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class OseeChange {
   public static enum GammaKind {
      Artiact, Attribute, Relation
   };
   private final TxChange sourceTxChange;
   private final int currentSourceGammaId;
   private int desinationGammaId;
   private final ModificationType currentSourceModType;
   private ModificationType firstSourceModType;
   private int itemId;
   private GammaKind kind;
   private TxChange destinationTxChange;
   private ModificationType desinationModType;
   private ModificationType resultantModType;
   private int resultantGammaId;

   protected OseeChange(TxChange txChange, int currentSourceGammaId, ModificationType currentSourceModType) {
      super();
      this.sourceTxChange = txChange;
      this.currentSourceGammaId = currentSourceGammaId;
      this.currentSourceModType = currentSourceModType;
   }

   public TxChange getTxChange() {
      return sourceTxChange;
   }

   public int getCurrentSourceGammaId() {
      return currentSourceGammaId;
   }

   public ModificationType getCurrentSourceModType() {
      return currentSourceModType;
   }

   public void accept(IChangeResolver resolver) throws OseeCoreException {
   }

   public int getItemId() {
      return itemId;
   }

   public void setItemId(int itemId) {
      this.itemId = itemId;
   }

   public GammaKind getKind() {
      return kind;
   }

   public void setKind(GammaKind kind) {
      this.kind = kind;
   }

   public TxChange getDestinationTxChange() {
      return destinationTxChange;
   }

   public void setDestinationTxChange(TxChange destinationTxChange) {
      this.destinationTxChange = destinationTxChange;
   }

   public ModificationType getDesinationModType() {
      return desinationModType;
   }

   public void setDesinationModType(ModificationType desinationModType) {
      this.desinationModType = desinationModType;
   }

   /**
    * @return the desinationGammaId
    */
   public int getDesinationGammaId() {
      return desinationGammaId;
   }

   /**
    * @param desinationGammaId the desinationGammaId to set
    */
   public void setDesinationGammaId(int desinationGammaId) {
      this.desinationGammaId = desinationGammaId;
   }

   /**
    * @return the firstSourceModType
    */
   public ModificationType getFirstSourceModType() {
      return firstSourceModType;
   }

   /**
    * @param firstSourceModType the firstSourceModType to set
    */
   public void setFirstSourceModType(ModificationType firstSourceModType) {
      this.firstSourceModType = firstSourceModType;
   }

   public boolean wasNewOnSource() {
      return firstSourceModType == ModificationType.NEW || currentSourceModType == ModificationType.NEW || firstSourceModType == null;
   }

   public boolean wasIntroducedOnSource() {
      return firstSourceModType == ModificationType.INTRODUCED || currentSourceModType == ModificationType.INTRODUCED;
   }

   public boolean wasNewOrIndtroducedOnSource() {
      return wasNewOnSource() || wasIntroducedOnSource();
   }

   public ModificationType getResultantModType() {
      return resultantModType;
   }

   public void setResultantModType(ModificationType resultantModType) {
      this.resultantModType = resultantModType;
   }

   public boolean isAlreadyOnDestination() {
      return currentSourceGammaId == desinationGammaId && currentSourceModType.isDeleted() == desinationModType.isDeleted();
   }

   /**
    * @return the resultantGammaId
    */
   public int getResultantGammaId() {
      return resultantGammaId;
   }

   /**
    * @param resultantGammaId the resultantGammaId to set
    */
   public void setResultantGammaId(int resultantGammaId) {
      this.resultantGammaId = resultantGammaId;
   }

   @Override
   public String toString() {
      return "OseeChange [currentSourceGammaId=" + currentSourceGammaId + ", currentSourceModType=" + currentSourceModType + ", desinationGammaId=" + desinationGammaId + ", desinationModType=" + desinationModType + ", destinationTxChange=" + destinationTxChange + ", firstSourceModType=" + firstSourceModType + ", itemId=" + itemId + ", kind=" + kind + ", resultantGammaId=" + resultantGammaId + ", resultantModType=" + resultantModType + ", sourceTxChange=" + sourceTxChange + "]";
   }
}