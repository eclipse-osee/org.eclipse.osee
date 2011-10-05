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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRow {

   private int artifactId = -1;
   private int branchId = -1;

   private int transactionId = -1;
   private int stripeId = -1;

   private long artTypeUuid = -1;

   private String guid = null;
   private String humanReadableId = null;

   private int gammaId = -1;
   private ModificationType modType = null;
   private boolean isHistorical = false;

   public ArtifactRow() {
      // do nothing
   }

   public int getArtifactId() {
      return artifactId;
   }

   public int getBranchId() {
      return branchId;
   }

   public int getTransactionId() {
      return transactionId;
   }

   public int getStripeId() {
      return stripeId;
   }

   public long getArtTypeUuid() {
      return artTypeUuid;
   }

   public String getGuid() {
      return guid;
   }

   public String getHumanReadableId() {
      return humanReadableId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public boolean isHistorical() {
      return isHistorical;
   }

   public void setArtifactId(int artifactId) {
      this.artifactId = artifactId;
   }

   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   public void setTransactionId(int transactionId) {
      this.transactionId = transactionId;
   }

   public void setStripeId(int stripeId) {
      this.stripeId = stripeId;
   }

   public void setArtTypeUuid(long artTypeUuid) {
      this.artTypeUuid = artTypeUuid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setHumanReadableId(String humanReadableId) {
      this.humanReadableId = humanReadableId;
   }

   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public void setHistorical(boolean isHistorical) {
      this.isHistorical = isHistorical;
   }

   @Override
   public String toString() {
      return "ArtifactRow [artifactId=" + artifactId + ", branchId=" + branchId + ", transactionId=" + transactionId + ", artTypeUuid=" + artTypeUuid + ", guid=" + guid + ", humanReadableId=" + humanReadableId + ", gammaId=" + gammaId + ", modType=" + modType + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (artTypeUuid ^ (artTypeUuid >>> 32));
      result = prime * result + artifactId;
      result = prime * result + branchId;
      result = prime * result + gammaId;
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      result = prime * result + ((humanReadableId == null) ? 0 : humanReadableId.hashCode());
      result = prime * result + (isHistorical ? 1231 : 1237);
      result = prime * result + ((modType == null) ? 0 : modType.hashCode());
      result = prime * result + stripeId;
      result = prime * result + transactionId;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      ArtifactRow other = (ArtifactRow) obj;
      if (artTypeUuid != other.artTypeUuid) {
         return false;
      }
      if (artifactId != other.artifactId) {
         return false;
      }
      if (branchId != other.branchId) {
         return false;
      }
      if (gammaId != other.gammaId) {
         return false;
      }
      if (guid == null) {
         if (other.guid != null) {
            return false;
         }
      } else if (!guid.equals(other.guid)) {
         return false;
      }
      if (humanReadableId == null) {
         if (other.humanReadableId != null) {
            return false;
         }
      } else if (!humanReadableId.equals(other.humanReadableId)) {
         return false;
      }
      if (isHistorical != other.isHistorical) {
         return false;
      }
      if (modType != other.modType) {
         return false;
      }
      if (stripeId != other.stripeId) {
         return false;
      }
      if (transactionId != other.transactionId) {
         return false;
      }
      return true;
   }

}
