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
package org.eclipse.osee.framework.core.model.change;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public abstract class ChangeItem implements Comparable<ChangeItem> {
   private final int artId;
   private final int itemId;
   private final long itemTypeId;

   private final ChangeVersion baseEntry;
   private final ChangeVersion firstChange;
   private final ChangeVersion currentEntry;
   private final ChangeVersion destinationEntry;
   private final ChangeVersion netEntry;

   private boolean synthetic;

   protected ChangeItem(int itemId, long itemTypeId, int artId, long currentSourceGammaId, ModificationType currentSourceModType) {
      this.itemId = itemId;
      this.itemTypeId = itemTypeId;
      this.artId = artId;

      this.currentEntry = new ChangeVersion(currentSourceGammaId, currentSourceModType);

      this.baseEntry = new ChangeVersion();
      this.firstChange = new ChangeVersion();
      this.destinationEntry = new ChangeVersion();
      this.netEntry = new ChangeVersion();

      this.synthetic = false;
   }

   public void setSynthetic(boolean synthetic) {
      this.synthetic = synthetic;
   }

   public boolean isSynthetic() {
      return synthetic;
   }

   public int getArtId() {
      return artId;
   }

   public int getItemId() {
      return itemId;
   }

   public long getItemTypeId() {
      return itemTypeId;
   }

   public ChangeVersion getBaselineVersion() {
      return baseEntry;
   }

   public ChangeVersion getFirstNonCurrentChange() {
      return firstChange;
   }

   public ChangeVersion getCurrentVersion() {
      return currentEntry;
   }

   public ChangeVersion getDestinationVersion() {
      return destinationEntry;
   }

   public ChangeVersion getNetChange() {
      return netEntry;
   }

   @Override
   public String toString() {
      return String.format(
         "ChangeItem - itemId:[%s] artId:%s typeId:%s base:%s first:%s current:%s destination:%s net:%s synthetic:%s",
         itemId, getArtId(), getItemTypeId(), getBaselineVersion(), getFirstNonCurrentChange(), getCurrentVersion(),
         getDestinationVersion(), getNetChange(), isSynthetic());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof ChangeItem)) {
         return false;
      }

      ChangeItem other = (ChangeItem) obj;

      if (itemId != other.itemId) {
         return false;
      }
      if (artId != other.artId) {
         return false;
      }
      if (currentEntry == null) {
         if (other.currentEntry != null) {
            return false;
         }
      }
      if (!currentEntry.equals(other.currentEntry)) {
         return false;
      }
      if (itemTypeId != other.itemTypeId) {
         return false;
      }
      return true;
   }

   @Override
   public int compareTo(ChangeItem obj) {
      return itemId - obj.itemId;
   }

   @Override
   public int hashCode() {
      return itemId;
   }
}