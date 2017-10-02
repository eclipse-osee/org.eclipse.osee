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
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
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
            TransactionEndpoint txBuilder = client.getTransactionEndpoint();
            Artifact firstArtifact = artifacts.iterator().next();
            TransactionRecord txRecord = BranchManager.getBaseTransaction(firstArtifact.getBranch());
            BranchId branch = firstArtifact.getBranch();

            for (Artifact artifact : artifacts) {
               monitor.subTask("Reverting: " + artifact.getName());
               monitor.worked(1);
               Artifact sourceArtifact =
                  ArtifactQuery.getHistoricalArtifactOrNull(artifact, txRecord, DeletionFlag.INCLUDE_DELETED);
               try {
                  if (sourceArtifact != null) {
                     String userId = ClientSessionManager.getCurrentUserToken().getUserId();
                     User user = UserManager.getUserByUserId(userId);
                     txBuilder.replaceWithBaselineTxVersion(user.getGuid(), branch, txRecord, sourceArtifact.getArtId(),
                        ReplaceArtifactWithBaselineOperation.class.getSimpleName());
                     monitor.done();
                  } else {
                     artifact.deleteAndPersist();
                  }
               } catch (Exception ex) {
                  throw JaxRsExceptions.asOseeException(ex);
               }
            }
            persistAndReloadArtifacts();
            monitor.done();
         }
      }
   }

   private void persistAndReloadArtifacts()  {
      for (Artifact artifact : artifacts) {
         artifact.reloadAttributesAndRelations();
      }
   }

}
