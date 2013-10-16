/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IntroduceArtifactOperation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Wilik Karol
 */
public class ReplaceArtifactWithBaselineOperation extends AbstractOperation {

   private final Collection<Change> changeReportChanges;
   private final Collection<Artifact> artifacts;

   public ReplaceArtifactWithBaselineOperation(Collection<Change> changeReportChanges, Collection<Artifact> artifacts) {
      super("Replace Artifact With Baseline Operation", Activator.PLUGIN_ID);
      this.changeReportChanges = changeReportChanges;
      this.artifacts = artifacts;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!monitor.isCanceled() && Conditions.notNull(changeReportChanges, artifacts)) {
         monitor.beginTask("Reverting artifact(s)", artifacts.size());
         if (!artifacts.isEmpty()) {
            Artifact firstArtifact = artifacts.iterator().next();
            IOseeBranch branch = firstArtifact.getBranch();
            SkynetTransaction transaction =
               TransactionManager.createTransaction(branch, ReplaceArtifactWithBaselineOperation.class.getSimpleName());

            TransactionRecord txRecord = firstArtifact.getFullBranch().getBaseTransaction();
            for (Artifact artifact : artifacts) {
               monitor.subTask("Reverting: " + artifact.getName());
               monitor.worked(1);
               Artifact sourceArtifact =
                  ArtifactQuery.checkHistoricalArtifactFromId(artifact.getArtId(), txRecord,
                     DeletionFlag.INCLUDE_DELETED);
               if (sourceArtifact != null) {
                  transaction.addArtifact(new IntroduceArtifactOperation(branch).introduce(sourceArtifact));
               } else {
                  artifact.delete();
                  transaction.addArtifact(artifact);
               }
               monitor.done();
            }
            monitor.subTask(String.format("Persisting %s artifact(s)", artifacts.size()));
            transaction.execute();
            persistAndReloadArtifacts();
         }
         monitor.done();
      }
   }

   private void persistAndReloadArtifacts() throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         artifact.reloadAttributesAndRelations();
      }
   }

}
