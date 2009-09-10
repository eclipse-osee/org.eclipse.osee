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

/**
 * @author Roberto E. Escobar
 */
public class CommitItem {
   public static enum GammaKind {
      Artifact, Attribute, Relation
   };

   private int itemId;
   private GammaKind kind;
   private final ChangePair baseEntry;
   private final ChangePair firstChange;
   private final ChangePair currentEntry;
   private final ChangePair destinationEntry;
   private final ChangePair netEntry;

   public CommitItem(long currentSourceGammaId, ModificationType currentSourceModType) {
      super();
      this.baseEntry = new ChangePair();
      this.firstChange = new ChangePair();
      this.currentEntry = new ChangePair(currentSourceGammaId, currentSourceModType);
      this.destinationEntry = new ChangePair();
      this.netEntry = new ChangePair();
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

   public ChangePair getBase() {
      return baseEntry;
   }

   public ChangePair getFirst() {
      return firstChange;
   }

   public ChangePair getCurrent() {
      return currentEntry;
   }

   public ChangePair getDestination() {
      return destinationEntry;
   }

   public ChangePair getNet() {
      return netEntry;
   }

   public boolean wasNewOnSource() {
      return getFirst().isNew() || getCurrent().isNew();
   }

   public boolean wasIntroducedOnSource() {
      return getFirst().isIntroduced() || getCurrent().isIntroduced();
   }

   public boolean wasNewOrIntroducedOnSource() {
      return wasNewOnSource() || wasIntroducedOnSource();
   }

   public boolean isAlreadyOnDestination() {
      return getCurrent().sameGammaAs(getDestination()) && getCurrent().getModType().isDeleted() == getDestination().getModType().isDeleted();
   }

   @Override
   public String toString() {
      return String.format("CommitItem - kind:[%s] itemId:[%s] base:%s first:%s current:%s destination:%s net:%s",
            kind, itemId, getBase(), getFirst(), getCurrent(), getDestination(), getNet());
   }
}