package org.eclipse.osee.orcs.rest.model.transaction;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Huy A. Tran
 */

public class TransferTransaction {
   private BranchId branchId;
   private TransactionId importedTransId;
   private TransactionId sourceTransId;
   private TransactionId sourceUniqueTrans;
   private GammaId gammaId;
   private TransferOpType transferOp;

   public TransferTransaction(BranchId id, TransactionId sourceId, TransactionId uniqueTx, TransferOpType op) {
      this.branchId = id;
      this.importedTransId = null;
      this.sourceTransId = sourceId;
      this.sourceUniqueTrans = uniqueTx;
      this.gammaId = null;
      this.transferOp = op;
   }

   public BranchId getBranchId() {
      return branchId;
   }

   public void setBranchId(BranchId branchId) {
      this.branchId = branchId;
   }

   public TransactionId getImportedTransId() {
      return importedTransId;
   }

   public void setImportedTransId(TransactionId importedTransId) {
      this.importedTransId = importedTransId;
   }

   public TransactionId getSourceTransId() {
      return sourceTransId;
   }

   public void setSourceTransId(TransactionId sourceTransId) {
      this.sourceTransId = sourceTransId;
   }

   public TransactionId getSourceUniqueTrans() {
      return sourceUniqueTrans;
   }

   public void setSourceUniqueTrans(TransactionId sourceUniqueTrans) {
      this.sourceUniqueTrans = sourceUniqueTrans;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public TransferOpType getTransferOp() {
      return transferOp;
   }

   public void setTransferOp(TransferOpType transferOp) {
      this.transferOp = transferOp;
   }

}
