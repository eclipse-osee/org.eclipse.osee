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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceAttributeWithBaselineOperation extends AbstractOperation {
   private final Collection<Attribute<?>> attributes;
   private Map<IOseeBranch, SkynetTransaction> transactions;

   public ReplaceAttributeWithBaselineOperation(Collection<Attribute<?>> attributes) {
      super("Replace artifact with baseline values", Activator.PLUGIN_ID);
      this.attributes = attributes;
   }

   private SkynetTransaction getTransaction(IOseeBranch branch) throws OseeCoreException {
      SkynetTransaction transaction = null;
      if (transactions == null) {
         transactions = new HashMap<IOseeBranch, SkynetTransaction>();
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

   private void persistAndReloadArtifacts(Collection<Artifact> artifacts) throws OseeCoreException {
      for (SkynetTransaction transaction : getTransactions()) {
         transaction.execute();
      }
      for (Artifact artifact : artifacts) {
         artifact.reloadAttributesAndRelations();
      }

      for (Artifact artifact : artifacts) {
         try {
            artifact.persist(getTransaction(null));
            artifact.reloadAttributesAndRelations();
         } catch (OseeCoreException ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      for (Attribute<?> attribute : attributes) {
         boolean itemFoundInBaseline = false;
         try {
            Branch fullBranch = BranchManager.getBranch(attribute.getArtifact().getBranch());
            TransactionRecord baselineTransactionRecord = fullBranch.getBaseTransaction();
            for (Change change : ChangeManager.getChangesPerArtifact(attribute.getArtifact(), new NullProgressMonitor())) {
               if (change.getTxDelta().getEndTx().getId() == baselineTransactionRecord.getId()) {
                  if (change.getItemKind().equals("Attribute") && change.getItemId() == attribute.getId()) {
                     attribute.replaceWithVersion((int) change.getGamma());
                     attribute.getArtifact().persist(getTransaction(attribute.getArtifact().getBranch()));
                     artifacts.add(attribute.getArtifact());
                     itemFoundInBaseline = true;
                  }
               }
            }

            if (!itemFoundInBaseline) {
               attribute.delete();
               attribute.getArtifact().persist(getTransaction(attribute.getArtifact().getBranch()));
               artifacts.add(attribute.getArtifact());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
      persistAndReloadArtifacts(artifacts);
   }

   @Override
   protected void doFinally(IProgressMonitor monitor) {
      super.doFinally(monitor);
      transactions = null;
   }
}
