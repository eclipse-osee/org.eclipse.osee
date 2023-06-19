/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.access.AtsArtifactChecks;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class MoveTeamWorkflowsOperation extends AbstractOperation {

   private final TeamWorkFlowArtifact destTeamWorkflow;
   private final Collection<TeamWorkFlowArtifact> sourceTeamWorkflows;
   private final String destActionTitle;

   public MoveTeamWorkflowsOperation(String operationName, TeamWorkFlowArtifact destTeamWorkflows, Collection<TeamWorkFlowArtifact> sourceTeamWorkflows, String destActionTitle) {
      super(operationName, Activator.PLUGIN_ID);
      this.destTeamWorkflow = destTeamWorkflows;
      this.sourceTeamWorkflows = sourceTeamWorkflows;
      this.destActionTitle = destActionTitle;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) {
      if (sourceTeamWorkflows.isEmpty()) {
         throw new OseeArgumentException("ERROR", "Must provide Source Team Workflow(s).");
      }
      if (destTeamWorkflow == null) {
         throw new OseeArgumentException("ERROR", "Must provide Destination Team Workflow.");
      }

      try {
         AtsArtifactChecks.setDeletionChecksEnabled(false);

         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), getName());
         if (Strings.isValid(destActionTitle)) {
            ((Artifact) destTeamWorkflow.getParentAction().getStoreObject()).setName(destActionTitle);
         }

         for (TeamWorkFlowArtifact teamArt : sourceTeamWorkflows) {
            IAtsAction parentAction = teamArt.getParentAction();
            teamArt.deleteRelations(AtsRelationTypes.ActionToWorkflow_Action);
            teamArt.clearCaches();
            teamArt.addRelation(AtsRelationTypes.ActionToWorkflow_Action,
               (Artifact) destTeamWorkflow.getParentAction().getStoreObject());
            teamArt.persist(transaction);
            boolean allDeleted = true;
            for (IRelationLink link : AtsApiService.get().getRelationResolver().getRelations(
               parentAction.getArtifactId(), AtsRelationTypes.ActionToWorkflow_TeamWorkflow)) {
               if (!link.isDeleted()) {
                  allDeleted = false;
               }
            }
            if (allDeleted) {
               AtsApiService.get().getQueryServiceIde().getArtifact(parentAction.getArtifactId()).deleteAndPersist(
                  transaction);
            }
         }
         ((Artifact) destTeamWorkflow.getParentAction().getStoreObject()).persist(transaction);
         transaction.execute();
      } finally {
         AtsArtifactChecks.setDeletionChecksEnabled(true);
      }
   }
}
