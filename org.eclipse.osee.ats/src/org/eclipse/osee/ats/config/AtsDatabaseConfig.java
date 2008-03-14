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
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.workflow.AtsWorkFlowFactory;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerCustomizationArtifact;

public class AtsDatabaseConfig extends DbInitializationTask {

   public void run(Connection connection) throws Exception {
      createXViewerGlobalCustomization();
      createAtsTopLevelConfigObjects();
      // Imports workflow vue diagrams as id specified in extension point
      (new ImportWorkflowAction(false)).run();
      // Creates Actionable Items and Teams
      // Teams are related to workflow by id specified in team object in VUE diagram
      (new LoadAIsAndTeamsAction(false)).run();
      linkHeadTeamDefinitionWithReviewsAndTaskWorkflowDiagrams();
   }

   public static void linkHeadTeamDefinitionWithReviewsAndTaskWorkflowDiagrams() throws SQLException {
      TeamDefinitionArtifact teamDef = TeamDefinitionArtifact.getHeadTeamDefinition();

      // Relate task workflow
      Artifact taskWorkflow =
            (new ArtifactTypeNameSearch("General Document", AtsWorkFlowFactory.DEFAULT_TASK_WORKFLOW,
                  BranchPersistenceManager.getInstance().getAtsBranch())).getSingletonArtifactOrException(Artifact.class);
      teamDef.relate(RelationSide.TeamDefinitionToTaskWorkflowDiagram_WorkflowDiagram, taskWorkflow, true);

      // Relate peer to Peer review
      Artifact peerWorkflow =
            (new ArtifactTypeNameSearch("General Document", AtsWorkFlowFactory.PEERTOPEER_REVIEW_WORKFLOW,
                  BranchPersistenceManager.getInstance().getAtsBranch())).getSingletonArtifactOrException(Artifact.class);
      teamDef.relate(RelationSide.TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram, peerWorkflow, true);

      // Relate peer to Peer review
      Artifact decisionWorkflow =
            (new ArtifactTypeNameSearch("General Document", AtsWorkFlowFactory.DECISION_REVIEW_WORKFLOW,
                  BranchPersistenceManager.getInstance().getAtsBranch())).getSingletonArtifactOrException(Artifact.class);
      teamDef.relate(RelationSide.TeamDefinitionToDecisionReviewWorkflowDiagram_WorkflowDiagram, decisionWorkflow, true);

      teamDef.persist(true);
   }

   private void createAtsTopLevelConfigObjects() throws SQLException {
      AtsConfig.getInstance().getOrCreateAtsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
      AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateWorkflowDiagramsArtifact();
   }

   private void createXViewerGlobalCustomization() throws SQLException {
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(XViewerCustomizationArtifact.ARTIFACT_TYPE_NAME, "",
                  BranchPersistenceManager.getInstance().getAtsBranch());
      if (srch.getArtifacts(XViewerCustomizationArtifact.class).size() == 0) {
         XViewerCustomizationArtifact art =
               (XViewerCustomizationArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     XViewerCustomizationArtifact.ARTIFACT_TYPE_NAME).makeNewArtifact(
                     BranchPersistenceManager.getInstance().getAtsBranch());
         art.persistAttributes();
      }
   }

}
