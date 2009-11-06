/*
 * Created on Sep 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.revision.acquirer;

import java.util.ArrayList;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionRecord;

/**
 * @author Jeff C. Phillips
 *
 */
public abstract class ChangeAcquirer {
   private Branch sourceBranch;
   private TransactionRecord transactionId;
   private IProgressMonitor monitor;
   private Artifact specificArtifact;
   private Set<Integer> artIds;
   private ArrayList<ChangeBuilder> changeBuilders;
   private Set<Integer> newAndDeletedArtifactIds;
   
   public ChangeAcquirer(Branch sourceBranch, TransactionRecord transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super();
      this.sourceBranch = sourceBranch;
      this.transactionId = transactionId;
      this.monitor = monitor;
      this.specificArtifact = specificArtifact;
      this.artIds = artIds;
      this.changeBuilders = changeBuilders;
      this.newAndDeletedArtifactIds = newAndDeletedArtifactIds;
   }

   protected Branch getSourceBranch() {
      return sourceBranch;
   }

   protected TransactionRecord getTransaction() {
      return transactionId;
   }

   protected IProgressMonitor getMonitor() {
      return monitor;
   }

   protected Artifact getSpecificArtifact() {
      return specificArtifact;
   }

   protected Set<Integer> getArtIds() {
      return artIds;
   }

   protected ArrayList<ChangeBuilder> getChangeBuilders() {
      return changeBuilders;
   }

   protected Set<Integer> getNewAndDeletedArtifactIds() {
      return newAndDeletedArtifactIds;
   }

   public abstract ArrayList<ChangeBuilder> acquireChanges() throws OseeCoreException;
}
