/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import org.eclipse.osee.orcs.data.HasLocalId;

public class OrcsObject implements HasLocalId {

   private int branchId = -1;
   private int gammaId = -1;
   private long typeUuid = -1;
   private ModificationType modType = null;
   private int localId = -1;
   private int transactionId = -1;
   private boolean isHistorical = false;

   public OrcsObject() {
      super();
   }

   @Override
   public int getLocalId() {
      return localId;
   }

   public void setLocalId(int localId) {
      this.localId = localId;
   }

   public int getBranchId() {
      return branchId;
   }

   public void setBranchId(int branchId) {
      this.branchId = branchId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public long getTypeUuid() {
      return typeUuid;
   }

   public void setTypeUuid(long typeUuid) {
      this.typeUuid = typeUuid;
   }

   public ModificationType getModType() {
      return modType;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public int getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(int transactionId) {
      this.transactionId = transactionId;
   }

   public boolean isHistorical() {
      return isHistorical;
   }

   public void setHistorical(boolean isHistorical) {
      this.isHistorical = isHistorical;
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
      OrcsObject other = (OrcsObject) obj;
      if (branchId != other.branchId) {
         return false;
      }
      if (gammaId != other.gammaId) {
         return false;
      }
      if (isHistorical != other.isHistorical) {
         return false;
      }
      if (localId != other.localId) {
         return false;
      }
      if (transactionId != other.transactionId) {
         return false;
      }
      if (typeUuid != other.typeUuid) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

}
