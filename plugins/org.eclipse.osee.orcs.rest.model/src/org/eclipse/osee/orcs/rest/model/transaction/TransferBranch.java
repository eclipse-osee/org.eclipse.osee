package org.eclipse.osee.orcs.rest.model.transaction;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Huy A. Tran
 */

public class TransferBranch {
   public BranchId branchId;
   public TransactionId prevTx;
   public TransactionId curTx;
   public GammaId gammaId;
   public List<TransferTransaction> txList;

   public TransferBranch(BranchId id) {
      this.branchId = id;
      this.gammaId = null;
      this.txList = new ArrayList<>();
   }

   public BranchId getBranchId() {
      return branchId;
   }

   public void setPrevTX(TransactionId prevTx) {
      this.prevTx = prevTx;
   }

   public void setCurTX(TransactionId curTx) {
      this.curTx = curTx;
   }

   public void setGammaID(GammaId gammaId) {
      this.gammaId = gammaId;
   }
}
