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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public final class ChangeVersion {
   private Long gammaId;
   private ModificationType modType;
   private String value;
   private Integer transactionNumber;

   public ChangeVersion() {
      this(null, null, null);
   }

   public ChangeVersion(Long gammaId, ModificationType modType, Integer transactionNumber) {
      this(null, gammaId, modType, transactionNumber);
   }

   public ChangeVersion(String value, Long gammaId, ModificationType modType, Integer transactionNumber) {
      super();
      this.value = value;
      this.gammaId = gammaId;
      this.modType = modType;
      this.transactionNumber = transactionNumber;
   }

   public Long getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public String getValue() {
      return value;
   }

   public Integer getTransactionNumber() {
      return transactionNumber;
   }

   public void setTransactionNumber(Integer transactionNumber) {
      this.transactionNumber = transactionNumber;
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

   public boolean isValid() {
      return getModType() != null && getGammaId() != null;// && getTransactionNumber() != null;
   }

   public void copy(ChangeVersion item) {
      setGammaId(item.getGammaId());
      setModType(item.getModType());
      setTransactionNumber(item.getTransactionNumber());
      setValue(item.getValue());
   }

   @Override
   public String toString() {
      return String.format("[%s,%s,%s]", getTransactionNumber(), getGammaId(), getModType());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (gammaId == null ? 0 : gammaId.hashCode());
      result = prime * result + (modType == null ? 0 : modType.hashCode());
      result = prime * result + (transactionNumber == null ? 0 : transactionNumber.hashCode());
      result = prime * result + (value == null ? 0 : value.hashCode());
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
      if (transactionNumber == null) {
         if (other.transactionNumber != null) {
            return false;
         }
      } else if (!transactionNumber.equals(other.transactionNumber)) {
         return false;
      }
      if (value == null) {
         if (other.value != null) {
            return false;
         }
      } else if (!value.equals(other.value)) {
         return false;
      }
      return true;
   }

}
