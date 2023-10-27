package org.eclipse.osee.orcs.rest.model.transaction;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Huy A. Tran
 */

public class TransferTransaction {
   public BranchId branchId;
   public TransactionId importedTransId;
   public TransactionId sourceTransId;
   public TransactionId sourceUniqueTrans;
   public GammaId gammaId;
   public TransferOpType transferOp;

   public TransferTransaction(BranchId id, TransactionId sourceId, TransactionId uniqueTx, TransferOpType op) {
      this.branchId = id;
      this.importedTransId = null;
      this.sourceTransId = sourceId;
      this.sourceUniqueTrans = uniqueTx;
      this.gammaId = null;
      this.transferOp = op;
   }

   public void setImportedTransId(TransactionId transId) {
      this.importedTransId = transId;
   }

   public BranchId getBranchId() {
      return branchId;
   }
}
