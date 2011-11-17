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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.replace.ChangeCombiner;

/**
 * @author Jeff C. Phillips
 */
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
            Branch branch = artifact.getFullBranch();

            TransactionRecord baseTx = branch.getBaseTransaction();
            Collection<Change> changes =
               ChangeCombiner.combine(ChangeManager.getChangesMadeOnCurrentBranch(artifact, monitor), baseTx);

            boolean changeMade = false;
            for (Change change : changes) {
               if (change instanceof AttributeChange) {
                  changeMade |= processAttribute(artifact, baseTx, (AttributeChange) change);
               } else if (change instanceof ArtifactChange) {
                  changeMade |= processArtifact(artifact, baseTx, (ArtifactChange) change);
               }
            }

            if (changeMade) {
               SkynetTransaction transaction = getTransaction(branch);
               artifact.persist(transaction);
            }
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

   private boolean processArtifact(Artifact artifact, TransactionRecord baseTx, ArtifactChange change) throws OseeCoreException {
      boolean changeMade = false;
      if (artifact != null) {
         if (isBaselineTransaction(change, baseTx)) {
            if (artifact.getGammaId() != change.getGamma() || (artifact.getModType().isDeleted())) {
               artifact.replaceWithVersion((int) change.getGamma());
               changeMade = true;
            }
         } else if (!artifact.getModType().equals(ModificationType.REPLACED_WITH_VERSION)) {
            artifact.delete();
            changeMade = true;
         }
      } else {
         //Do something's
         //like an error
      }
      return changeMade;
   }

   private boolean processAttribute(Artifact artifact, TransactionRecord baseTx, AttributeChange change) throws OseeCoreException {
      boolean changeMade = false;
      Attribute<?> attribute = artifact.getAttributeById(change.getItemId(), true);
      if (attribute != null) {
         if (isBaselineTransaction(change, baseTx)) {
            if (attribute.getGammaId() != change.getGamma() || (attribute.getModificationType().isDeleted())) {
               attribute.replaceWithVersion((int) change.getGamma());
               changeMade = true;
            }
         } else if (!attribute.getModificationType().equals(ModificationType.REPLACED_WITH_VERSION)) {
            attribute.delete();
            changeMade = true;
         }
      } else {
         //Do something's
         //like an error
      }
      return changeMade;
   }

   private boolean isBaselineTransaction(Change change, TransactionRecord baseTx) {
      return change.getTxDelta().getEndTx().getId() == baseTx.getId();
   }
}
