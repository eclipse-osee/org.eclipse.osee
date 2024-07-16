/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;

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
            OseeClient client = ServiceUtil.getOseeClient();
            TransactionEndpoint TransactionEndpoint = client.getTransactionEndpoint();
            Artifact firstArtifact = artifacts.iterator().next();
            TransactionRecord txRecord = BranchManager.getBaseTransaction(firstArtifact.getBranch());
            BranchId branch = firstArtifact.getBranch();

            for (Artifact artifact : artifacts) {
               monitor.subTask("Reverting: " + artifact.getName());
               monitor.worked(1);
               Artifact sourceArtifact =
                  ArtifactQuery.getHistoricalArtifactOrNull(artifact, txRecord, DeletionFlag.INCLUDE_DELETED);
               if (sourceArtifact != null) {
                  UserId userId = UserManager.getUser();
                  try (Response res = TransactionEndpoint.replaceWithBaselineTxVersion(userId, branch, txRecord,
                     sourceArtifact, ReplaceArtifactWithBaselineOperation.class.getSimpleName())) {
                     monitor.done();
                  }
               } else {
                  artifact.deleteAndPersist(getClass().getSimpleName());
               }
            }
            persistAndReloadArtifacts();
            monitor.done();
         }
      }
   }

   private void persistAndReloadArtifacts() {
      for (Artifact artifact : artifacts) {
         artifact.reloadAttributesAndRelations();
      }
   }

}
