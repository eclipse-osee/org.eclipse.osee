/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Torin Grenda
 */
public class ReplaceRelationWithBaselineOperation extends AbstractOperation {

   private final Collection<Change> changes;

   public ReplaceRelationWithBaselineOperation(Collection<Change> changeReportChanges) {
      super("Replace Relation With Baseline Operation", Activator.PLUGIN_ID);
      changes = changeReportChanges;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!monitor.isCanceled() && Conditions.notNull(changes) && !changes.isEmpty()) {
         monitor.beginTask("Reverting relation", changes.size());

         Change firstChange = changes.iterator().next();
         SkynetTransaction transaction = TransactionManager.createTransaction(firstChange.getBranch(),
            ReplaceRelationWithBaselineOperation.class.getSimpleName());

         for (Change change : changes) {
            if (change instanceof RelationChange) {
               RelationChange relChange = (RelationChange) change;
               monitor.subTask("Reverting: " + changes.toString());
               monitor.worked(1);
               Artifact sideA = ArtifactQuery.getArtifactFromId(relChange.getArtId(), relChange.getBranch());
               Artifact sideB = ArtifactQuery.getArtifactFromId(relChange.getBArtId(), relChange.getBranch());
               if (!sideA.isDeleted() && !sideB.isDeleted()) {
                  if (relChange.getChangeItem().isDeleted()) {
                     RelationLink relation = RelationManager.getOrCreate(sideA, sideB, relChange.getRelationType(),
                        relChange.getRelLinkId(), relChange.getBaselineGamma(), relChange.getRationale(),
                        relChange.getModificationType(), ApplicabilityId.BASE, 0, ArtifactId.SENTINEL);
                     relation.undelete();
                     sideA.persist(transaction);
                  }
               }
               monitor.done();
            }
         }

         transaction.execute();

         monitor.done();
      }
   }

}
