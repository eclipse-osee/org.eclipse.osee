package org.eclipse.osee.orcs.rest.model.transaction;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Huy A. Tran
 */

public class TransferBranch {
   private final BranchId branchId;
   private TransactionId prevTx;
   private TransactionId uniqueTx;
   private final List<TransferTransaction> txList;

   public TransferBranch(BranchId id) {
      this.branchId = id;
      this.txList = new ArrayList<>();
   }

   public TransactionId getPrevTx() {
      return prevTx;
   }

   public void setPrevTx(TransactionId prevTx) {
      this.prevTx = prevTx;
   }

   public TransactionId getUniqueTx() {
      return uniqueTx;
   }

   public void setUniqueTx(TransactionId uniqueTx) {
      this.uniqueTx = uniqueTx;
   }

   public BranchId getBranchId() {
      return branchId;
   }

   public List<TransferTransaction> getTxList() {
      return txList;
   }

   public void addTransferTransaction(TransferTransaction toAdd) {
      txList.add(toAdd);
   }
}
