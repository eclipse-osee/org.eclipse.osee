/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.artifact.AtsArtifactChecks;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
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
   protected void doWork(IProgressMonitor monitor)  {
      if (sourceTeamWorkflows.isEmpty()) {
         throw new OseeArgumentException("ERROR", "Must provide Source Team Workflow(s).");
      }
      if (destTeamWorkflow == null) {
         throw new OseeArgumentException("ERROR", "Must provide Destination Team Workflow.");
      }

      try {
         AtsArtifactChecks.setDeletionChecksEnabled(false);

         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), getName());
         if (Strings.isValid(destActionTitle)) {
            destTeamWorkflow.getParentActionArtifact().setName(destActionTitle);
         }

         for (TeamWorkFlowArtifact teamArt : sourceTeamWorkflows) {
            ActionArtifact parentAction = teamArt.getParentActionArtifact();
            teamArt.deleteRelations(AtsRelationTypes.ActionToWorkflow_Action);
            teamArt.clearCaches();
            teamArt.addRelation(AtsRelationTypes.ActionToWorkflow_Action, destTeamWorkflow.getParentActionArtifact());
            teamArt.persist(transaction);
            boolean allDeleted = true;
            for (RelationLink link : parentAction.getRelations(AtsRelationTypes.ActionToWorkflow_WorkFlow)) {
               if (!link.isDeleted()) {
                  allDeleted = false;
               }
            }
            if (allDeleted) {
               parentAction.deleteAndPersist(transaction);
            }
         }
         destTeamWorkflow.getParentActionArtifact().persist(transaction);
         transaction.execute();
      } finally {
         AtsArtifactChecks.setDeletionChecksEnabled(true);
      }
   }
}
