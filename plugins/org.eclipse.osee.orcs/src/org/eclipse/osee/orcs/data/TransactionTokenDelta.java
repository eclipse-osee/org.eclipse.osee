package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.data.TransactionToken;

/**
 * @author Roberto E. Escobar
 */
public final class TransactionTokenDelta {
   private final TransactionToken startTx;
   private final TransactionToken endTx;

   public TransactionTokenDelta(TransactionToken startTx, TransactionToken endTx) {
      super();
      this.startTx = startTx;
      this.endTx = endTx;
   }

   public TransactionToken getStartTx() {
      return startTx;
   }

   public TransactionToken getEndTx() {
      return endTx;
   }

   public boolean areOnTheSameBranch() {
      return startTx.isOnSameBranch(endTx);
   }

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof TransactionTokenDelta) {
         TransactionTokenDelta other = (TransactionTokenDelta) obj;
         boolean left = startTx == null ? other.startTx == null : startTx.equals(other.startTx);
         boolean right = endTx == null ? other.endTx == null : endTx.equals(other.endTx);
         result = left && right;
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = 17;
      if (startTx != null) {
         result = prime * result + startTx.hashCode();
      } else {
         result = prime * result;
      }
      if (endTx != null) {
         result = prime * result + endTx.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   @Override
   public String toString() {
      String firstString = String.valueOf(getStartTx());
      String secondString = String.valueOf(getEndTx());
      return String.format("[start:%s, end:%s]", firstString, secondString);
   }
}