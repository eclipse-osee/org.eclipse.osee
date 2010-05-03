package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

public class ChangeUiData {
   private final Collection<Change> changes;
   private Artifact associatedArtifact;
   private Branch branch;
   private TransactionRecord transaction;

   public ChangeUiData() {
      changes = new ArrayList<Change>();
   }

   public void reset() {
      changes.clear();
      associatedArtifact = null;
   }

   public Collection<Change> getChanges() {
      return changes;
   }

   public void setAssociatedArtifact(Artifact associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public Artifact getAssociatedArtifact() {
      return associatedArtifact;
   }

   public Branch getBranch() {
      return branch;
   }

   public boolean isBranchValid() {
      return branch != null;
   }

   public boolean isTransactionValid() {
      return transaction != null;
   }

   public void setBranch(Branch branch) {
      this.branch = branch;
   }

   public TransactionRecord getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionRecord transaction) {
      this.transaction = transaction;
   }

   public boolean isRebaseline() {
      return isBranchValid() ? getBranch().getBranchState().equals(BranchState.REBASELINED) : false;
   }
}