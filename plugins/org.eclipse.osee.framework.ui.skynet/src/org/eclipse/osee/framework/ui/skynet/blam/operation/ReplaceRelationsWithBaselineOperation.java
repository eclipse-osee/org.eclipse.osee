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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceRelationsWithBaselineOperation extends AbstractOperation {

   private final Collection<Artifact> artifacts;
   private Map<Branch, SkynetTransaction> transactions;

   public ReplaceRelationsWithBaselineOperation(Collection<Artifact> artifacts) {
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

   //for each relation change
   //if in baseline

   //if gammas are diff
   //replace with baseline version
   //handle deleted
   //undelete
   //change mod type baseline versions

   //else if not in baseline
   //remove - delete relation

   //This is to handle the order attribute
   //-------------------------------------------------------
   //if the relation is ordered

   //Add the artifact guid to relation order attribute- guid, when it existed in the baseline transaction 
   //Remove - guid, when it did not exist in the baseline
   //correct releation order for the org side a artifact

   // try find location i.e. before or after previous location
   // --- else leave it out
   // transaction delete attribute - when none of the left attributes are ordered

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!artifacts.isEmpty()) {
         for (Artifact artifact : artifacts) {
            Branch branch = artifact.getBranch();
            TransactionRecord baseTx = branch.getBaseTransaction();
            Collection<Change> changes = ChangeManager.getChangesMadeOnCurrentBranch(artifact, monitor);

            for (Change change : changes) {
               if (change instanceof RelationChange) {
                  processRelation(artifact, baseTx, (RelationChange) change);
                  fixParentAttributeOrdering(artifact, monitor, baseTx);//need a way to make sure this only needs to happen once for each artifact
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

   private void fixParentAttributeOrdering(Artifact artifact, IProgressMonitor monitor, TransactionRecord baseTx) throws OseeCoreException {
      Artifact parentArtifact = artifact.getParent();
      Collection<Change> changes = ChangeManager.getChangesMadeOnCurrentBranch(parentArtifact, monitor);

      for (Change change : changes) {
         if (change instanceof AttributeChange) {
            AttributeChange attributeChange = (AttributeChange) change;

            if (attributeChange.getAttributeType().equals(CoreAttributeTypes.RelationOrder)) {
               Attribute<?> attribute = attributeChange.getAttribute();

               //if the order already existed replace
               //How do I know what to do with this guy .... it could of been created for another change and we do not want to remove it because of a relation replace
               //relation orders might not be from default hir
               //check relation types if the are ordered and handle adding and removing the relation orders
               if (isBaselineTransaction(change, baseTx)) {
                  if (attribute.getGammaId() != change.getGamma()) {
                     attribute.replaceWithVersion((int) change.getGamma());
                     parentArtifact.persist(getTransaction(parentArtifact.getBranch()));
                     break;
                  }
               } else { //else delete it because it was not in the baseline
                  attribute.delete();
               }
            }
         }
      }
   }

   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);
      transactions = null;
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
         //this would be an issue
         //Do something's
      }
   }

   private boolean isBaselineTransaction(Change change, TransactionRecord baseTx) {
      return change.getTxDelta().getEndTx().getId() == baseTx.getId();
   }
}
