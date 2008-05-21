/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config;

import java.sql.Connection;
import java.sql.SQLException;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.workflow.AtsWorkFlowFactory;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

public class AtsDatabaseConfig extends DbInitializationTask {

   public void run(Connection connection) throws Exception {
      createAtsTopLevelConfigObjects();
      // Imports workflow vue diagrams as id specified in extension point
      (new ImportWorkflowAction(false)).run();
      // Creates Actionable Items and Teams
      // Teams are related to workflow by id specified in team object in VUE diagram
      (new LoadAIsAndTeamsAction(false)).run();
      linkHeadTeamDefinitionWithReviewsAndTaskWorkflowDiagrams();
   }

   public static void linkHeadTeamDefinitionWithReviewsAndTaskWorkflowDiagrams() throws Exception {
      TeamDefinitionArtifact teamDef = TeamDefinitionArtifact.getHeadTeamDefinition();

      // Relate task workflow
      Artifact taskWorkflow =
            ArtifactQuery.getArtifactFromTypeAndName("General Document", AtsWorkFlowFactory.DEFAULT_TASK_WORKFLOW,
                  AtsPlugin.getAtsBranch());
      teamDef.relate(CoreRelationEnumeration.TeamDefinitionToTaskWorkflowDiagram_WorkflowDiagram, taskWorkflow, true);

      // Relate peer to Peer review
      Artifact peerWorkflow =
            ArtifactQuery.getArtifactFromTypeAndName("General Document", AtsWorkFlowFactory.PEERTOPEER_REVIEW_WORKFLOW,
                  AtsPlugin.getAtsBranch());
      teamDef.relate(CoreRelationEnumeration.TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram, peerWorkflow, true);

      // Relate peer to Peer review
      Artifact decisionWorkflow =
            ArtifactQuery.getArtifactFromTypeAndName("General Document", AtsWorkFlowFactory.DECISION_REVIEW_WORKFLOW,
                  AtsPlugin.getAtsBranch());
      teamDef.relate(CoreRelationEnumeration.TeamDefinitionToDecisionReviewWorkflowDiagram_WorkflowDiagram, decisionWorkflow, true);

      teamDef.persistAttributesAndRelations();
   }

   private void createAtsTopLevelConfigObjects() throws SQLException {
      AtsConfig.getInstance().getOrCreateAtsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
      AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateWorkflowDiagramsArtifact();
   }
}