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
   private ModificationType baseSourceModType;
   private final long currentSourceGammaId;
   private final ModificationType currentSourceModType;
   private long destinationGammaId;
   private ModificationType destinationModType;
   private long netGammaId;
   private ModificationType netModType;

   public CommitItem(long currentSourceGammaId, ModificationType currentSourceModType) {
      super();
      this.currentSourceGammaId = currentSourceGammaId;
      this.currentSourceModType = currentSourceModType;
   }

   public long getCurrentSourceGammaId() {
      return currentSourceGammaId;
   }

   public ModificationType getCurrentSourceModType() {
      return currentSourceModType;
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

   public ModificationType getDestinationModType() {
      return destinationModType;
   }

   public void setDestinationModType(ModificationType destinationModType) {
      this.destinationModType = destinationModType;
   }

   public long getDestinationGammaId() {
      return destinationGammaId;
   }

   public void setDestinationGammaId(long desinationGammaId) {
      this.destinationGammaId = desinationGammaId;
   }

   public ModificationType getBaseSourceModType() {
      return baseSourceModType;
   }

   public void setBaseSourceModType(ModificationType baseSourceModType) {
      this.baseSourceModType = baseSourceModType;
   }

   public boolean wasNewOnSource() {
      return baseSourceModType == ModificationType.NEW || currentSourceModType == ModificationType.NEW || baseSourceModType == null;
   }

   public boolean wasIntroducedOnSource() {
      return baseSourceModType == ModificationType.INTRODUCED || currentSourceModType == ModificationType.INTRODUCED;
   }

   public boolean wasNewOrIntroducedOnSource() {
      return wasNewOnSource() || wasIntroducedOnSource();
   }

   public boolean isAlreadyOnDestination() {
      return currentSourceGammaId == destinationGammaId && currentSourceModType.isDeleted() == destinationModType.isDeleted();
   }

   public ModificationType getNetModType() {
      return netModType;
   }

   public void setNetModType(ModificationType netModType) {
      this.netModType = netModType;
   }

   public long getNetGammaId() {
      return netGammaId;
   }

   public void setNetGammaId(long netGammaId) {
      this.netGammaId = netGammaId;
   }

   @Override
   public String toString() {
      return String.format(
            "CommitItem - kind:[%s] itemId:[%s] baseMod[%s] source[%s,%s] destination[%s,%s] net[%s,%s]", kind, itemId,
            baseSourceModType, currentSourceGammaId, currentSourceModType, destinationGammaId, destinationModType,
            netGammaId, netModType);
   }
}