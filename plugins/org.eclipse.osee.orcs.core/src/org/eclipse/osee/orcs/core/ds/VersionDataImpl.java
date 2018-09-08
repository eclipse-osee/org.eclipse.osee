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

import static org.eclipse.osee.framework.core.data.RelationalConstants.IS_HISTORICAL_DEFAULT;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Roberto E. Escobar
 */
public class VersionDataImpl implements VersionData {

   private BranchId branch = BranchId.SENTINEL;
   private TransactionId txId = TransactionId.SENTINEL;
   private GammaId gamma = GammaId.SENTINEL;
   private boolean historical = IS_HISTORICAL_DEFAULT;
   private TransactionId stripeId = TransactionId.SENTINEL;

   public VersionDataImpl() {
      super();
   }

   @Override
   public GammaId getGammaId() {
      return gamma;
   }

   @Override
   public void setGammaId(GammaId gamma) {
      this.gamma = gamma;
   }

   @Override
   public TransactionId getTransactionId() {
      return txId;
   }

   @Override
   public void setTransactionId(TransactionId txId) {
      this.txId = txId;
   }

   @Override
   public TransactionId getStripeId() {
      return stripeId;
   }

   @Override
   public void setStripeId(TransactionId stripeId) {
      this.stripeId = stripeId;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   @Override
   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   @Override
   public boolean isInStorage() {
      return TransactionId.SENTINEL != txId;
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
      result = prime * result + branch.hashCode();
      result = prime * result + gamma.hashCode();
      result = prime * result + (historical ? 1231 : 1237);
      result = prime * result + stripeId.hashCode();
      result = prime * result + txId.hashCode();
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
      if (branch.notEqual(other.branch)) {
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
      return "Version [branch=" + branch + ", txId=" + txId + ", gamma=" + gamma + ", historical=" + historical + ", stripeId=" + stripeId + "]";
   }

   @Override
   public VersionData clone() {
      VersionData copy = new VersionDataImpl();
      copy.setBranch(getBranch());
      copy.setGammaId(getGammaId());
      copy.setHistorical(isHistorical());
      copy.setStripeId(getStripeId());
      copy.setTransactionId(getTransactionId());
      return copy;
   }

}
