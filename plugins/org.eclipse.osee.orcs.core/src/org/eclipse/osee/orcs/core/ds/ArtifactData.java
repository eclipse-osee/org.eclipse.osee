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

/**
 * @author Roberto E. Escobar
 */
public class ArtifactData extends OrcsObject {

   private int stripeId = -1;

   private String guid = null;
   private String humanReadableId = null;

   public ArtifactData() {
      // do nothing
   }

   public int getStripeId() {
      return stripeId;
   }

   public int getArtifactId() {
      return getLocalId();
   }

   public void setArtifactId(int artId) {
      setLocalId(artId);
   }

   public long getArtTypeUuid() {
      return getTypeUuid();
   }

   public void setArtTypeUuid(long artType) {
      setTypeUuid(artType);
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String getHumanReadableId() {
      return humanReadableId;
   }

   public void setStripeId(int stripeId) {
      this.stripeId = stripeId;
   }

   public void setHumanReadableId(String humanReadableId) {
      this.humanReadableId = humanReadableId;
   }

   @Override
   public String toString() {
      return "ArtifactRow [artifactId=" + getLocalId() + ", branchId=" + getBranchId() + ", transactionId=" + getTransactionId() + ", artTypeUuid=" + getTypeUuid() + ", guid=" + guid + ", humanReadableId=" + humanReadableId + ", gammaId=" + getGammaId() + ", modType=" + getModType() + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (getTypeUuid() ^ (getTypeUuid() >>> 32));
      result = prime * result + getLocalId();
      result = prime * result + getBranchId();
      result = prime * result + getGammaId();
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      result = prime * result + ((humanReadableId == null) ? 0 : humanReadableId.hashCode());
      result = prime * result + (isHistorical() ? 1231 : 1237);
      result = prime * result + ((getModType() == null) ? 0 : getModType().hashCode());
      result = prime * result + stripeId;
      result = prime * result + getTransactionId();
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
      ArtifactData other = (ArtifactData) obj;
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

      if (stripeId != other.stripeId) {
         return false;
      }

      return super.equals(obj);
   }

}
