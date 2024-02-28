/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

public class ValidateCommitResult {
   private boolean commitable = false;
   private int conflictCount = 0;
   private int conflictsResolved = 0;
   private TransactionToken tx = TransactionToken.SENTINEL;
   private BranchId sourceBranch = BranchId.SENTINEL;
   private BranchId destinationBranch = BranchId.SENTINEL;

   public ValidateCommitResult(int conflictCount, int conflictsResolved, TransactionToken tx, boolean commitable, BranchId sourceBranch, BranchId destBranch) {
      this.setConflictCount(conflictCount);
      this.setTx(tx);
      this.setCommitable(commitable);
      this.setSourceBranch(sourceBranch);
      this.setDestinationBranch(destBranch);
      this.setConflictsResolved(conflictsResolved);
   }

   public ValidateCommitResult() {
      // for jax-rs
   }

   public boolean isCommitable() {
      return commitable;
   }

   public void setCommitable(boolean commitable) {
      this.commitable = commitable;
   }

   public int getConflictCount() {
      return conflictCount;
   }

   public void setConflictCount(int conflictCount) {
      this.conflictCount = conflictCount;
   }

   public TransactionToken getTx() {
      return tx;
   }

   public void setTx(TransactionToken tx) {
      this.tx = tx;
   }

   public BranchId getSourceBranch() {
      return sourceBranch;
   }

   public void setSourceBranch(BranchId sourceBranch) {
      this.sourceBranch = sourceBranch;
   }

   public BranchId getDestinationBranch() {
      return destinationBranch;
   }

   public void setDestinationBranch(BranchId destinationBranch) {
      this.destinationBranch = destinationBranch;
   }

   public int getConflictsResolved() {
      return conflictsResolved;
   }

   public void setConflictsResolved(int conflictsResolved) {
      this.conflictsResolved = conflictsResolved;
   }
}
