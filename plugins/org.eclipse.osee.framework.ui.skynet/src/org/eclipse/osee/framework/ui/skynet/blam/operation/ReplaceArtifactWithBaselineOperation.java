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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class ReplaceArtifactWithBaselineOperation extends AbstractOperation {

   private final Collection<Artifact> artifacts;

   public ReplaceArtifactWithBaselineOperation(Collection<Artifact> artifacts) {
      super("Replace artifact with baseline values", Activator.PLUGIN_ID);

      this.artifacts = artifacts;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      try {
         for (Artifact artifact : artifacts) {
            TransactionRecord baselineTransactionRecord = artifact.getBranch().getBaseTransaction();
            for (Change change : ChangeManager.getChangesPerArtifact(artifact, new NullProgressMonitor())) {
               if (change.getItemKind().equals("Attribute")) {
                  processAttribute(artifact, baselineTransactionRecord, change);
               } else if (change.getItemKind().equals("Relation")) {
                  processRelation(artifact, baselineTransactionRecord, change);
               }
            }
            artifact.persist("Replace artifact with baseline version");
            artifact.reloadAttributesAndRelations();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void processAttribute(Artifact artifact, TransactionRecord baselineTransactionRecord, Change change) throws OseeCoreException {
      Attribute<?> attribute = artifact.getAttributeById(change.getItemId(), true);
      if (isBaselineTransaction(change, baselineTransactionRecord)) {
         if (attribute.getGammaId() != change.getGamma()) {
            attribute.replaceWithVersion((int) change.getGamma());
         }
      } else {
         //Get changes returns changes from other branches
         if (artifact.getBranch().equals(change.getBranch())) {
            attribute.delete();
         }
      }
   }

   private void processRelation(Artifact artifact, TransactionRecord baselineTransactionRecord, Change change) {
      RelationChange relationChange = (RelationChange) change;
      RelationLink link =
         RelationManager.getLoadedRelationById(relationChange.getItemId(), relationChange.getArtId(),
            relationChange.getBArtId(), artifact.getBranch());
      if (isBaselineTransaction(change, baselineTransactionRecord)) {
         if (link.getGammaId() != change.getGamma()) {
            link.replaceWithVersion((int) change.getGamma());
         }
      } else {
         //Get changes returns changes from other branches
         if (artifact.getBranch().equals(change.getBranch())) {
            link.delete(false);
         }
      }
   }

   private boolean isBaselineTransaction(Change change, TransactionRecord baselineTransactionRecord) {
      return change.getTxDelta().getEndTx().getId() == baselineTransactionRecord.getId();
   }

}
