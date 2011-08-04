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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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

   public void doSomething() throws OseeCoreException {

      for (Artifact artifact : artifacts) {
         Branch branch = artifact.getBranch();
         TransactionRecord baseTx = branch.getBaseTransaction();

         Collection<Change> changes = ChangeManager.getChangesMadeOnCurrentBranch(artifact, new NullProgressMonitor());

         for (Change change : changes) {
            if (change instanceof RelationChange) {
               handleRelations(artifact, baseTx, (RelationChange) change);
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

   private Attribute<?> getRelationOrder(Artifact artifact) {
      Attribute<?> relationOrder = null;
      try {
         relationOrder = artifact.getAttributes(CoreAttributeTypes.RelationOrder).iterator().next();
      } catch (Exception ex) {
         //do nothing
      }
      return relationOrder;
   }

   private String handleOtherSideContainingGuid(Artifact artifact, RelationLink link) throws OseeCoreException {
      String relationOrderString = null;

      Artifact otherSideArtifact = link.getArtifactOnOtherSide(artifact);

      if (otherSideArtifact.getAttributeCount(CoreAttributeTypes.RelationOrder) > 0) {
         String otherSideRelationOrderString = getRelationOrder(otherSideArtifact).getDisplayableString();
         relationOrderString =
            ReplaceRelationsHelper.removeArtifactGuidFromRelationOrder(artifact.getGuid(), otherSideRelationOrderString);
      }
      return relationOrderString;
   }

   private String handleOtherSideDidContainGuid(Artifact artifact, RelationLink link, TransactionRecord baselineTx) throws OseeCoreException {
      String relationOrderString = null;

      //check before
      //check after
      //add insert to the bottom
      //
      //
      Artifact otherSideArtifact = link.getArtifactOnOtherSide(artifact);
      Artifact baselineOthersideArtifact = getBaselineArtifact(baselineTx, otherSideArtifact);

      if (baselineOthersideArtifact.getAttributeCount(CoreAttributeTypes.RelationOrder) > 0) {
         String otherSideRelationOrderString = getRelationOrder(baselineOthersideArtifact).getDisplayableString();

         if (otherSideRelationOrderString.contains(artifact.getGuid())) {
            relationOrderString =
               ReplaceRelationsHelper.addArtifactGuidToOrder(artifact.getGuid(),
                  ReplaceRelationsHelper.getBeforeOrderGuid(otherSideRelationOrderString, artifact.getGuid()),
                  otherSideRelationOrderString);
         }
      }
      return relationOrderString;
   }

   private void handleAttributeOrder(RelationLink link, Artifact artifact, boolean inBaseline, TransactionRecord baselineTransactionRecord) throws OseeCoreException {
      //this is not correct but will work for now
      if (link.getRelationType().isOrdered()) {

         Attribute<?> relationOrder = getRelationOrder(artifact);
         if (relationOrder != null) {
            Artifact baselineArtifact = getBaselineArtifact(baselineTransactionRecord, artifact);
            relationOrder.replaceWithVersion(getRelationOrder(baselineArtifact).getGammaId());
         }

         String newRelationOrder = handleOtherSideContainingGuid(artifact, link);

         if (newRelationOrder == null) {
            handleOtherSideDidContainGuid(artifact, link, baselineTransactionRecord);
         }
      }
   }

   public Artifact getBaselineArtifact(TransactionRecord transactionRecord, Artifact artifact) throws OseeCoreException {
      return ArtifactQuery.getHistoricalArtifactFromId(artifact.getArtId(), transactionRecord,
         DeletionFlag.INCLUDE_DELETED);
   }

   public void addArtifactGuid(String guid, String beforeGuid, String relationOrder) {
      ReplaceRelationsHelper.addArtifactGuidToOrder(guid, beforeGuid, relationOrder);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {

   }

   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);
      transactions = null;
   }

   private void handleRelations(Artifact artifact, TransactionRecord baselineTransactionRecord, RelationChange change) throws OseeCoreException {
      RelationLink link =
         RelationManager.getLoadedRelationById(change.getItemId(), change.getArtId(), change.getBArtId(),
            artifact.getBranch());
      if (link != null) {
         boolean isInBaselineTransaction = isBaselineTransaction(change, baselineTransactionRecord);
         if (isInBaselineTransaction) {
            link.internalSetModificationType(change.getModificationType());
            if (link.getGammaId() != change.getGamma()) {
               link.replaceWithVersion((int) change.getGamma());
            }
         } else {
            link.delete(false);
         }
         handleAttributeOrder(link, artifact, isInBaselineTransaction, baselineTransactionRecord);
      } else {
         throw new OseeStateException("The link %s for the replace with version should not come back as null ",
            change.getRelLinkId());
      }
   }

   private boolean isBaselineTransaction(Change change, TransactionRecord baseTx) {
      return change.getTxDelta().getEndTx().getId() == baseTx.getId();
   }
}
