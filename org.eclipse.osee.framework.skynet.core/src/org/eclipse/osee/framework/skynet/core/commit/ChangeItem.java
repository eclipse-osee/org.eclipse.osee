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
public abstract class ChangeItem {
   private int artId;
   private int itemId;

   private final ChangeVersion baseEntry;
   private final ChangeVersion firstChange;
   private final ChangeVersion currentEntry;
   private final ChangeVersion destinationEntry;
   private final ChangeVersion netEntry;

   protected ChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, int currentSourceTransactionNumber) {
      super();
      this.currentEntry = new ChangeVersion(currentSourceGammaId, currentSourceModType, currentSourceTransactionNumber);

      this.baseEntry = new ChangeVersion();
      this.firstChange = new ChangeVersion();
      this.destinationEntry = new ChangeVersion();
      this.netEntry = new ChangeVersion();
   }

   public int getArtId() {
      return artId;
   }

   public void setArtId(int artId) {
      this.artId = artId;
   }

   public int getItemId() {
      return itemId;
   }

   protected void setItemId(int itemId) {
      this.itemId = itemId;
   }

   public ChangeVersion getBase() {
      return baseEntry;
   }

   public ChangeVersion getFirst() {
      return firstChange;
   }

   public ChangeVersion getCurrent() {
      return currentEntry;
   }

   public ChangeVersion getDestination() {
      return destinationEntry;
   }

   public ChangeVersion getNet() {
      return netEntry;
   }

   @Override
   public String toString() {
      return String.format("CommitItem - itemId:[%s] base:%s first:%s current:%s destination:%s net:%s", itemId,
            getBase(), getFirst(), getCurrent(), getDestination(), getNet());
   }
}