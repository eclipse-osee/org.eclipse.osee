/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;

/**
 * Builder class to handle how a group of arbitrary <code>Artifact</code>'s and <code>IRelationLink</code>'s are
 * placed into <code>Transaction</code>'s for the Skynet versioning system.<br/><br/> <b>This class is used
 * internal to the Skynet system, and is not supported as part of the Skynet API</b>
 * 
 * @author Robert A. Fisher
 */
public class SkynetTransactionBuilder {
   private ArtifactPersistenceManager artifactManager;
   private RelationPersistenceManager relationManager;
   private Collection<SkynetTransaction> transactions;
   private SkynetTransaction versionedTransaction;
   private Branch branch;
   private boolean finished;
   private IProgressMonitor monitor;
   private final Object transactionKey;

   protected SkynetTransactionBuilder(Branch branch) throws SQLException {
      this(branch, new NullProgressMonitor());
   }

   /**
    * @throws SQLException
    */
   protected SkynetTransactionBuilder(Branch branch, IProgressMonitor monitor) throws SQLException {
      this.transactions = new LinkedList<SkynetTransaction>();
      this.versionedTransaction = null;
      this.artifactManager = ArtifactPersistenceManager.getInstance();
      this.relationManager = RelationPersistenceManager.getInstance();
      this.branch = branch;
      this.finished = false;
      this.monitor = monitor;
      this.transactionKey = this;

      ConnectionHandler.startTransactionLevel(transactionKey);
   }

   public void addArtifact(Artifact artifact) throws SQLException {
      artifactManager.doSave(artifact, getTransaction(artifact.isVersionControlled()), true);
   }

   public void addArtifacts(Collection<Artifact> artifacts) throws SQLException {
      for (Artifact artifact : artifacts)
         addArtifact(artifact);
   }

   public void addLink(IRelationLink link) throws SQLException {
      relationManager.doSave(link, getTransaction(link.isVersionControlled()));
   }

   public void addLinks(Collection<IRelationLink> links) throws SQLException {
      for (IRelationLink link : new ArrayList<IRelationLink>(links))
         addLink(link);
   }

   public void deleteArtifact(Artifact artifact) throws SQLException {
      // No work is recorded since this reports children, and the number of children
      // to the bottom of the tree can not be calculated with out incurring a lot of
      // work
      monitor.subTask("Deleting " + artifact.getDescriptiveName());
      artifactManager.doDelete(artifact, getTransaction(artifact.isVersionControlled()), this);
   }

   public void deleteLink(IRelationLink link) throws SQLException {
      relationManager.doDelete(link, getTransaction(link.isVersionControlled()));
   }

   /**
    * Finish and commit the Skynet Transactions. This method performs clean up and either terminate, or this method must
    * be called, so use in accordance with a try/finally block.
    */
   protected void execute() throws SQLException {
      checkFinished();

      // Start at a non-zero value to accommodate work contributed at this level
      int totalWork = 1;
      for (SkynetTransaction transaction : transactions) {
         totalWork += transaction.getWork();
      }

      monitor.beginTask("Process Transaction", totalWork);

      try {
         for (SkynetTransaction transaction : transactions) {
            transaction.execute(monitor);
         }
         monitor.subTask("Update head transaction data");
         monitor.worked(1);
         ConnectionHandler.setTransactionLevelAsSuccessful(transactionKey);

      } finally {
         ConnectionHandler.endTransactionLevel(transactionKey);
         monitor.done();
      }
   }

   private SkynetTransaction getTransaction(boolean isVersioned) throws SQLException {
      SkynetTransaction transaction;

      if (isVersioned) {
         transaction = getVersionedTransaction();
      } else {
         transaction = new SkynetTransaction(branch);
         transactions.add(transaction);
      }

      return transaction;
   }

   private SkynetTransaction getVersionedTransaction() throws SQLException {
      if (versionedTransaction == null) {
         versionedTransaction = new SkynetTransaction(branch);
         transactions.add(versionedTransaction);
      }

      return versionedTransaction;
   }

   /**
    * Cancel the built up Skynet Transactions. This method performs clean up and either execute, or this method must be
    * called, so use in accordance with a try/finally block.
    */
   protected void terminateBatch() {
      checkFinished();

      transactions.clear();
      ConnectionHandler.endTransactionLevel(transactionKey);
   }

   private void checkFinished() {
      if (finished) throw new IllegalStateException("This builder has already been finished.");
      finished = true;
   }

   /**
    * @return the transactions
    */
   protected Collection<SkynetTransaction> getTransactions() {
      return transactions;
   }
}
