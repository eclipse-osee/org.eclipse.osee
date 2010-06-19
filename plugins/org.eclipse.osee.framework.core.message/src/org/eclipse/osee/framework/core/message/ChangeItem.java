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
package org.eclipse.osee.framework.core.message;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public abstract class ChangeItem {
   private final int artId;
   private final int itemId;
   private final int itemTypeId;

   private final ChangeVersion baseEntry;
   private final ChangeVersion firstChange;
   private final ChangeVersion currentEntry;
   private final ChangeVersion destinationEntry;
   private final ChangeVersion netEntry;

   protected ChangeItem(int itemId, int itemTypeId, int artId, long currentSourceGammaId, ModificationType currentSourceModType) {
      super();
      this.itemId = itemId;
      this.itemTypeId = itemTypeId;
      this.artId = artId;

      this.currentEntry = new ChangeVersion(currentSourceGammaId, currentSourceModType);

      this.baseEntry = new ChangeVersion();
      this.firstChange = new ChangeVersion();
      this.destinationEntry = new ChangeVersion();
      this.netEntry = new ChangeVersion();
   }

   public int getArtId() {
      return artId;
   }

   public int getItemId() {
      return itemId;
   }

   public int getItemTypeId() {
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
            "ChangeItem - itemId:[%s] artId:%s typeId:%s base:%s first:%s current:%s destination:%s net:%s", itemId,
            getArtId(), getItemTypeId(), getBaselineVersion(), getFirstNonCurrentChange(), getCurrentVersion(),
            getDestinationVersion(), getNetChange());
   }
}