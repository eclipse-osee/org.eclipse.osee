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

import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public final class ChangeVersion {
   private GammaId gammaId;
   private ModificationType modType;
   private ApplicabilityToken appId;
   private String value;

   public ChangeVersion() {
      this(null, null, ModificationType.SENTINEL, null);
   }

   public ChangeVersion(GammaId gammaId, ModificationType modType, ApplicabilityToken appId) {
      this(null, gammaId, modType, appId);
   }

   public ChangeVersion(String value, GammaId gammaId, ModificationType modType, ApplicabilityToken appId) {
      this.value = value;
      this.gammaId = gammaId;
      this.modType = modType;
      this.appId = appId;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public ApplicabilityToken getApplicabilityToken() {
      return appId;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public void setApplicabilityToken(ApplicabilityToken appId) {
      this.appId = appId;
   }

   public boolean isValid() {
      return getModType().isValid() && getGammaId() != null;// && getTransactionNumber() != null;
   }

   public void copy(ChangeVersion item) {
      Conditions.checkNotNull(item, "ChangeVersion");
      setGammaId(item.getGammaId());
      setModType(item.getModType());
      setValue(item.getValue());
      setApplicabilityToken(item.getApplicabilityToken());
   }

   @Override
   public String toString() {
      ApplicabilityToken token = getApplicabilityToken();
      String tokenName = token != null ? token.getName() : "null";
      return String.format("[%s,%s,%s]", getGammaId(), getModType(), tokenName);
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
      } else if (gammaId.notEqual(other.gammaId)) {
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
