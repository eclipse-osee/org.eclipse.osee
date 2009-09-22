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
   public static enum GammaKind {
      Artifact, Attribute, Relation
   };

   private int artId;
   private int itemId;
   private GammaKind kind;
   private final VersionedChange baseEntry;
   private final VersionedChange firstChange;
   private final VersionedChange currentEntry;
   private final VersionedChange destinationEntry;
   private final VersionedChange netEntry;

   public ChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, long currentSourceTansactionNumber) {
      super();
      this.baseEntry = new VersionedChange();
      this.firstChange = new VersionedChange();
      this.currentEntry = new VersionedChange(currentSourceGammaId, currentSourceModType, currentSourceTansactionNumber);
      this.destinationEntry = new VersionedChange();
      this.netEntry = new VersionedChange();
   }

   public int getArtId() {
      return artId;
   }
   
   public void setArt_id(int artId) {
      this.artId = artId;
   }

   public int getItemId() {
      return itemId;
   }

   protected void setItemId(int itemId) {
      this.itemId = itemId;
   }

   public GammaKind getKind() {
      return kind;
   }

   protected void setKind(GammaKind kind) {
      this.kind = kind;
   }

   public VersionedChange getBase() {
      return baseEntry;
   }

   public VersionedChange getFirst() {
      return firstChange;
   }

   public VersionedChange getCurrent() {
      return currentEntry;
   }

   public VersionedChange getDestination() {
      return destinationEntry;
   }

   public VersionedChange getNet() {
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

   public boolean isIgnoreCase() {
      return wasCreatedAndDeleted() || isAlreadyOnDestination() || isDeletedAndDoestNotExistInDestination() || hasBeenDeletedInDestination() || isDestinationEqualOrNewerThanCurrent();
   }

   private boolean wasCreatedAndDeleted() {
      return wasNewOrIntroducedOnSource() && getCurrent().getModType().isDeleted();
   }

   private boolean isDeletedAndDoestNotExistInDestination() {
      return !getDestination().exists() && getCurrent().getModType().isDeleted();
   }

   private boolean hasBeenDeletedInDestination() {
      return getDestination().exists() && getDestination().getModType().isDeleted();
   }

   private boolean isDestinationEqualOrNewerThanCurrent() {
      return (getCurrent().isNew() || getCurrent().isIntroduced()) && getDestination().exists();
   }

   @Override
   public String toString() {
      return String.format("CommitItem - kind:[%s] itemId:[%s] base:%s first:%s current:%s destination:%s net:%s",
            kind, itemId, getBase(), getFirst(), getCurrent(), getDestination(), getNet());
   }
}