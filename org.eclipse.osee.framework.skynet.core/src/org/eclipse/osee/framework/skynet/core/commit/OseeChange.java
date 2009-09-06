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
   private final int sourceGammaId;
   private int desinationGammaId;
   private final ModificationType sourceModificationType;
   private int itemId;
   private GammaKind kind;
   private TxChange destinationTxChange;
   private ModificationType desinationModificationType;

   protected OseeChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId) {
      super();
      this.sourceTxChange = txChange;
      this.sourceGammaId = gammaId;
      this.sourceModificationType = modificationType;
      this.itemId = itemId;
   }

   public TxChange getTxChange() {
      return sourceTxChange;
   }

   public int getSourceGammaId() {
      return sourceGammaId;
   }

   public ModificationType getModificationType() {
      return sourceModificationType;
   }

   public int getTypeId() {
      return itemId;
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

   public ModificationType getDesinationModificationType() {
      return desinationModificationType;
   }

   public void setDesinationModificationType(ModificationType desinationModificationType) {
      this.desinationModificationType = desinationModificationType;
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
}