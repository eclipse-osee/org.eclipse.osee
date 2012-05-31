/*
 * Created on Jun 14, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.data.Version;

public class VersionImpl implements Version {

   private static final int TRANSACTION_SENTINEL = -1;

   private long gamma;
   private int localId;
   private ModificationType modType;
   private int txId;
   private boolean historical;

   public VersionImpl(long gamma, int localId, ModificationType modType, int txId, boolean historical) {
      super();
      this.gamma = gamma;
      this.localId = localId;
      this.modType = modType;
      this.txId = txId;
      this.historical = historical;
   }

   @Override
   public int getLocalId() {
      return localId;
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
   public ModificationType getModificationType() {
      return modType;
   }

   @Override
   public void setModificationType(ModificationType modType) {
      this.modType = modType;
   }

   @Override
   public void setLocalId(int localId) {
      this.localId = localId;
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
   public boolean isInStorage() {
      return TRANSACTION_SENTINEL != txId;
   }

   @Override
   public boolean isHistorical() {
      return historical;
   }

   @Override
   public void setHistorical(boolean historical) {
      this.historical = historical;
   }

}
