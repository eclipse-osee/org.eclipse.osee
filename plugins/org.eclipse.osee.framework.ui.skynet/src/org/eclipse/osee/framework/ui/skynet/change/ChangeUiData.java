package org.eclipse.osee.framework.ui.skynet.change;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

public class ChangeUiData {
   private final Collection<Change> changes = new ArrayList<Change>();
   private Artifact associatedArtifact;
   private Branch branch;
   private TransactionRecord transaction;
   private boolean isLoaded;
   private boolean loadOnOpen;

   public ChangeUiData() {
   }

   public void reset() {
      changes.clear();
      setAssociatedArtifact(null);
      setIsLoaded(false);
   }

   public boolean isLoaded() {
      return isLoaded;
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((branch == null) ? 0 : branch.hashCode());
      result = prime * result + ((transaction == null) ? 0 : transaction.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ChangeUiData other = (ChangeUiData) obj;
      if (branch == null) {
         if (other.branch != null) return false;
      } else if (!branch.equals(other.branch)) return false;
      if (transaction == null) {
         if (other.transaction != null) return false;
      } else if (!transaction.equals(other.transaction)) return false;
      return true;
   }

   public void setIsLoaded(boolean isLoaded) {
      this.isLoaded = isLoaded;
   }

   public void setLoadOnOpen(boolean loadOnOpen) {
      this.loadOnOpen = loadOnOpen;
   }

   public boolean isLoadOnOpenEnabled() {
      return loadOnOpen;
   }
}