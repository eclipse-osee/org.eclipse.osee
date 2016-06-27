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

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public final class ChangeVersion {
   private Long gammaId;
   private ModificationType modType;
   private ApplicabilityId appId;
   private String value;

   public ChangeVersion() {
      this(null, null, null, null);
   }

   public ChangeVersion(Long gammaId, ModificationType modType, ApplicabilityId appId) {
      this(null, gammaId, modType, appId);
   }

   public ChangeVersion(String value, Long gammaId, ModificationType modType, ApplicabilityId appId) {
      this.value = value;
      this.gammaId = gammaId;
      this.modType = modType;
      this.appId = appId;
   }

   public Long getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public ApplicabilityId getApplicabilityId() {
      return appId;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setGammaId(Long gammaId) {
      this.gammaId = gammaId;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public void setApplicabilityId(ApplicabilityId appId) {
      this.appId = appId;
   }

   public boolean isValid() {
      return getModType() != null && getGammaId() != null;// && getTransactionNumber() != null;
   }

   public void copy(ChangeVersion item) {
      setGammaId(item.getGammaId());
      setModType(item.getModType());
      setValue(item.getValue());
      setApplicabilityId(item.getApplicabilityId());
   }

   @Override
   public String toString() {
      return String.format("[%s,%s,%s]", getGammaId(), getModType(), getApplicabilityId());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (gammaId == null ? 0 : gammaId.hashCode());
      result = prime * result + (modType == null ? 0 : modType.hashCode());
      result = prime * result + (value == null ? 0 : value.hashCode());
      result = prime * result + (appId == null ? 0 : appId.hashCode());
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
      ChangeVersion other = (ChangeVersion) obj;
      if (gammaId == null) {
         if (other.gammaId != null) {
            return false;
         }
      } else if (!gammaId.equals(other.gammaId)) {
         return false;
      }
      if (modType == null) {
         if (other.modType != null) {
            return false;
         }
      } else if (!modType.equals(other.modType)) {
         return false;
      }
      if (value == null) {
         if (other.value != null) {
            return false;
         }
      } else if (!value.equals(other.value)) {
         return false;
      }
      if (appId == null) {
         if (other.appId != null) {
            return false;
         }
      } else if (!appId.equals(other.appId)) {
         return false;
      }
      return true;
   }

}
