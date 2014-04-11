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
package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;

/**
 * @author Roberto E. Escobar
 */
public class VersionDataImpl implements VersionData {

   private Long branchUuid = RelationalConstants.BRANCH_SENTINEL;
   private int txId = RelationalConstants.TRANSACTION_SENTINEL;
   private long gamma = RelationalConstants.GAMMA_SENTINEL;
   private boolean historical = RelationalConstants.IS_HISTORICAL_DEFAULT;
   private int stripeId = RelationalConstants.TRANSACTION_SENTINEL;

   public VersionDataImpl() {
      super();
   }

   @Override
   public long getGammaId() {
      return gamma;
   }

   @Override
   public void setGammaId(long gamma) {
      this.gamma = gamma;
   }

   @Override
   public int getTransactionId() {
      return txId;
   }

   @Override
   public void setTransactionId(int txId) {
      this.txId = txId;
   }

   @Override
   public int getStripeId() {
      return stripeId;
   }

   @Override
   public void setStripeId(int stripeId) {
      this.stripeId = stripeId;
   }

   @Override
   public long getBranchId() {
      return branchUuid;
   }

   @Override
   public void setBranchId(long branchUuid) {
      this.branchUuid = branchUuid;
   }

   @Override
   public boolean isInStorage() {
      return RelationalConstants.TRANSACTION_SENTINEL != txId;
   }

   @Override
   public boolean isHistorical() {
      return historical;
   }

   @Override
   public void setHistorical(boolean historical) {
      this.historical = historical;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + branchUuid.hashCode();
      result = prime * result + (int) (gamma ^ (gamma >>> 32));
      result = prime * result + (historical ? 1231 : 1237);
      result = prime * result + stripeId;
      result = prime * result + txId;
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
      VersionDataImpl other = (VersionDataImpl) obj;
      if (branchUuid != other.branchUuid) {
         return false;
      }
      if (gamma != other.gamma) {
         return false;
      }
      if (historical != other.historical) {
         return false;
      }
      if (stripeId != other.stripeId) {
         return false;
      }
      if (txId != other.txId) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Version [branchUuid=" + branchUuid + ", txId=" + txId + ", gamma=" + gamma + ", historical=" + historical + ", stripeId=" + stripeId + "]";
   }

   @Override
   public VersionData clone() {
      VersionData copy = new VersionDataImpl();
      copy.setBranchId(getBranchId());
      copy.setGammaId(getGammaId());
      copy.setHistorical(isHistorical());
      copy.setStripeId(getStripeId());
      copy.setTransactionId(getTransactionId());
      return copy;
   }

}
