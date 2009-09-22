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
public final class VersionedChange {
   private Long gammaId;
   private ModificationType modType;
   private String value;
   private Long transactionNumber;
   

   public VersionedChange() {
      this(null, null, null);
   }

   public VersionedChange(Long gammaId, ModificationType modType, Long transactionNumber) {
      super();
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

   public Long getTransactionNumber(){
      return transactionNumber;
   }
   
   public void setTransactionNumber(long transactionNumber){
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

   public boolean isNew() {
      return getModType() == ModificationType.NEW;
   }

   public boolean isIntroduced() {
      return getModType() == ModificationType.INTRODUCED;
   }

   public boolean isDeleted() {
      return getModType().isDeleted();
   }

   public boolean exists() {
      return getModType() != null && getGammaId() != null;
   }

   public boolean sameGammaAs(VersionedChange other) {
      boolean result = false;
      if (this.getGammaId() == other.getGammaId()) {
         result = true;
      } else if (this.getGammaId() != null) {
         result = this.getGammaId().equals(other.getGammaId());
      }
      return result;
   }

   @Override
   public String toString() {
      return String.format("[%s,%s]", getGammaId(), getModType());
   }

}
