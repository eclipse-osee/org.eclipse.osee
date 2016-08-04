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

import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class ChangeItem implements Comparable<ChangeItem> {

   private ChangeIgnoreType ignoreType = ChangeIgnoreType.NONE;
   private ChangeType changeType = ChangeType.UNKNOWN_CHANGE;
   private int artId = -1;
   private int itemId = -1;
   private long itemTypeId = -1;

   private ChangeVersion baselineVersion = new ChangeVersion();
   private ChangeVersion firstNonCurrentChange = new ChangeVersion();
   private ChangeVersion currentVersion = new ChangeVersion();
   private ChangeVersion destinationVersion = new ChangeVersion();
   private ChangeVersion netChange = new ChangeVersion();

   private boolean synthetic = false;
   private boolean isApplicabilityCopy = false;

   private int artIdB = -1;

   public ChangeItem() {
      super();
   }

   public void copy(ChangeItem source) {
      Conditions.checkNotNull(source, "ChangeItem");
      setIgnoreType(source.getIgnoreType());
      setChangeType(source.getChangeType());
      setArtId(source.getArtId());
      setItemId(source.getItemId());
      setItemTypeId(source.getItemTypeId());
      setBaselineVersion(source.getBaselineVersion());
      setFirstNonCurrentChange(source.getFirstNonCurrentChange());
      setCurrentVersion(source.getCurrentVersion());
      setDestinationVersion(source.getDestinationVersion());
      setNetChange(source.getNetChange());
      setSynthetic(source.isSynthetic());
      setApplicabilityCopy(source.isApplicabilityCopy());
      setArtIdB(source.getArtIdB());
   }

   public ChangeIgnoreType getIgnoreType() {
      return ignoreType;
   }

   public void setIgnoreType(ChangeIgnoreType type) {
      this.ignoreType = type;
   }

   public void setSynthetic(boolean synthetic) {
      this.synthetic = synthetic;
   }

   public boolean isApplicabilityCopy() {
      return isApplicabilityCopy;
   }

   public void setApplicabilityCopy(boolean isApplicabilityCopy) {
      this.isApplicabilityCopy = isApplicabilityCopy;
   }

   public ChangeType getChangeType() {
      return changeType;
   }

   public void setChangeType(ChangeType changeType) {
      this.changeType = changeType;
   }

   public void setArtId(int artId) {
      this.artId = artId;
   }

   public void setItemId(int itemId) {
      this.itemId = itemId;
   }

   public void setItemTypeId(long itemTypeId) {
      this.itemTypeId = itemTypeId;
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
      return baselineVersion;
   }

   public ChangeVersion getFirstNonCurrentChange() {
      return firstNonCurrentChange;
   }

   public ChangeVersion getCurrentVersion() {
      return currentVersion;
   }

   public ChangeVersion getDestinationVersion() {
      return destinationVersion;
   }

   public ChangeVersion getNetChange() {
      return netChange;
   }

   public void setBaselineVersion(ChangeVersion baselineVersion) {
      this.baselineVersion = baselineVersion;
   }

   public void setFirstNonCurrentChange(ChangeVersion firstNonCurrentChange) {
      this.firstNonCurrentChange = firstNonCurrentChange;
   }

   public void setCurrentVersion(ChangeVersion currentVersion) {
      this.currentVersion = currentVersion;
   }

   public void setDestinationVersion(ChangeVersion destinationVersion) {
      this.destinationVersion = destinationVersion;
   }

   public void setNetChange(ChangeVersion netChange) {
      this.netChange = netChange;
   }

   public int getArtIdB() {
      return artIdB;
   }

   public void setArtIdB(int artIdB) {
      this.artIdB = artIdB;
   }

   @Override
   public String toString() {
      return String.format(
         "ChangeItem - itemId:[%s] artId:%s typeId:%s base:%s first:%s current:%s destination:%s net:%s synthetic:%s ignoreType:%s",
         itemId, getArtId(), getItemTypeId(), getBaselineVersion(), getFirstNonCurrentChange(), getCurrentVersion(),
         getDestinationVersion(), getNetChange(), isSynthetic(), getItemTypeId());
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
      if (currentVersion == null) {
         if (other.currentVersion != null) {
            return false;
         }
      }
      if (!currentVersion.equals(other.currentVersion)) {
         return false;
      }
      if (itemTypeId != other.itemTypeId) {
         return false;
      }
      return true;
   }

   public boolean totalEquals(ChangeItem other) {
      if (!this.equals(other)) {
         return false;
      }
      if (!this.ignoreType.equals(other.ignoreType)) {
         return false;
      }
      if (!this.changeType.equals(other.changeType)) {
         return false;
      }
      if (!(this.synthetic == other.synthetic)) {
         return false;
      }
      if (!(this.isApplicabilityCopy == other.isApplicabilityCopy)) {
         return false;
      }
      if (this.getArtIdB() != other.getArtIdB()) {
         return false;
      }
      if (baselineVersion == null) {
         if (other.baselineVersion != null) {
            return false;
         }
      }
      if (!baselineVersion.equals(other.baselineVersion)) {
         return false;
      }
      if (destinationVersion == null) {
         if (other.destinationVersion != null) {
            return false;
         }
      }
      if (!destinationVersion.equals(other.destinationVersion)) {
         return false;
      }
      if (netChange == null) {
         if (other.netChange != null) {
            return false;
         }
      }
      if (!netChange.equals(other.netChange)) {
         return false;
      }
      if (firstNonCurrentChange == null) {
         if (other.firstNonCurrentChange != null) {
            return false;
         }
      }
      if (!firstNonCurrentChange.equals(other.firstNonCurrentChange)) {
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