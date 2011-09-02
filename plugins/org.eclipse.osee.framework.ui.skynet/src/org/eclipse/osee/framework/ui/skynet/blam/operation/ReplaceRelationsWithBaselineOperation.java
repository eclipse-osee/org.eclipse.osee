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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.replace.AttributeOrderHandlerContainedGuid;
import org.eclipse.osee.framework.ui.skynet.replace.AttributeOrderHandlerNotContainedGuid;
import org.eclipse.osee.framework.ui.skynet.replace.ChangeCombiner;
import org.eclipse.osee.framework.ui.skynet.replace.HandleAttributeOrderData;
import org.eclipse.osee.framework.ui.skynet.replace.RelationHandler;
import org.eclipse.osee.framework.ui.skynet.replace.ReplaceUtil;

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

   private void handleAttributeOrder(HandleAttributeOrderData orderData) throws OseeCoreException {
      //this is not correct but will work for now
      if (orderData.getLink().getRelationType().isOrdered()) {

         Attribute<?> relationOrder = ReplaceUtil.getRelationOrder(orderData.getArtifact());
         Artifact baselineArtifact =
            ReplaceUtil.getBaselineArtifact(orderData.getBaselineTransactionRecord(), orderData.getArtifact());

         if (ReplaceUtil.getRelationOrder(baselineArtifact) != null) {
            if (relationOrder != null) {
               relationOrder.replaceWithVersion(ReplaceUtil.getRelationOrder(baselineArtifact).getGammaId());
            }

            String newRelationOrder =
               new AttributeOrderHandlerContainedGuid().handleAttributeOrder(orderData.getArtifact(),
                  orderData.getLink());
            if (newRelationOrder == null) {
               newRelationOrder =
                  new AttributeOrderHandlerNotContainedGuid().handleAttributeOrder(orderData.getArtifact(),
                     orderData.getLink(), orderData.getBaselineTransactionRecord());
            }

            if (newRelationOrder != null) {
               orderData.getArtifact().setAttributeValues(CoreAttributeTypes.RelationOrder,
                  Collections.singletonList(newRelationOrder));
            }
         }
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      runDoWork();
   }

   public void runDoWork() throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         Branch branch = artifact.getBranch();
         TransactionRecord baseTx;
         baseTx = branch.getBaseTransaction();

         Collection<Change> changes = ChangeManager.getChangesMadeOnCurrentBranch(artifact, new NullProgressMonitor());
         changes = ChangeCombiner.combine(changes, baseTx);

         for (Change change : changes) {
            if (change instanceof RelationChange) {
               HandleAttributeOrderData orderData =
                  new RelationHandler().handleRelations(artifact, baseTx, (RelationChange) change,
                     getTransaction(artifact.getBranch()));
               handleAttributeOrder(orderData);
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

   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);
      transactions = null;
   }
}
