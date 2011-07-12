/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

public class ReplaceArtifactWithBaselineOperation extends AbstractOperation {

   private final Collection<Artifact> artifacts;
   private Map<Branch, SkynetTransaction> transactions;

   public ReplaceArtifactWithBaselineOperation(Collection<Artifact> artifacts) {
      super("Replace artifact with baseline values", Activator.PLUGIN_ID);
      this.artifacts = artifacts;
   }

   private SkynetTransaction getTransaction(Branch branch) {
      SkynetTransaction transaction = null;
      if (transactions == null) {
         transactions = new HashMap<Branch, SkynetTransaction>();
      } else {
         transaction = transactions.get(branch);
      }
      if (transaction == null) {
         transaction = new SkynetTransaction(branch, "Replace artifact with baseline values");
         transactions.put(branch, transaction);
      }
      return transaction;
   }

   private Collection<SkynetTransaction> getTransactions() {
      return transactions != null ? transactions.values() : Collections.<SkynetTransaction> emptyList();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!artifacts.isEmpty()) {

         for (Artifact artifact : artifacts) {
            Branch branch = artifact.getBranch();

            TransactionRecord baseTx = branch.getBaseTransaction();
            Collection<Change> changes = ChangeManager.getChangesMadeOnCurrentBranch(artifact, monitor);
            for (Change change : changes) {
               if (change instanceof AttributeChange) {
                  processAttribute(artifact, baseTx, (AttributeChange) change);
               } else if (change instanceof RelationChange) {
                  processRelation(artifact, baseTx, (RelationChange) change);
               }
            }
            SkynetTransaction transaction = getTransaction(branch);
            artifact.persist(transaction);
         }

         for (SkynetTransaction transaction : getTransactions()) {
            transaction.execute();
         }

         for (Artifact artifact : artifacts) {
            artifact.reloadAttributesAndRelations();
         }
      }
   }

   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);
      transactions = null;
   }

   private void processAttribute(Artifact artifact, TransactionRecord baseTx, AttributeChange change) throws OseeCoreException {
      Attribute<?> attribute = artifact.getAttributeById(change.getItemId(), true);
      if (attribute != null) {
         if (isBaselineTransaction(change, baseTx)) {
            if (attribute.getGammaId() != change.getGamma()) {
               attribute.replaceWithVersion((int) change.getGamma());
            }
         } else {
            attribute.delete();
         }
      } else {
         //Do something's
      }
   }

   private void processRelation(Artifact artifact, TransactionRecord baseTx, RelationChange change) {
      RelationLink link =
         RelationManager.getLoadedRelationById(change.getItemId(), change.getArtId(), change.getBArtId(),
            artifact.getBranch());
      if (link != null) {
         if (isBaselineTransaction(change, baseTx)) {
            if (link.getGammaId() != change.getGamma()) {
               link.replaceWithVersion((int) change.getGamma());
            }
         } else {
            link.delete(false);
         }
      } else {
         //Do something's
      }
   }

   private boolean isBaselineTransaction(Change change, TransactionRecord baseTx) {
      return change.getTxDelta().getEndTx().getId() == baseTx.getId();
   }

}
