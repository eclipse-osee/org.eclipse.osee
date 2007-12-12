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
package org.eclipse.osee.ats.workflow;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.ImportWorkflowAction;
import org.eclipse.osee.ats.config.WorkflowDiagramFactory;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkFlowFactory {

   private static AtsWorkFlowFactory instance = new AtsWorkFlowFactory();

   /**
    * 
    */
   private AtsWorkFlowFactory() {
      super();
   }

   public static AtsWorkFlowFactory getInstance() {
      return instance;
   }

   public static String DEFAULT_TEAM_WORKFLOW = "osee_ats_defaultTeam_workflow";
   public static String DEFAULT_TASK_WORKFLOW = "osee_ats_defaultTask_workflow";
   public static String DECISION_REVIEW_WORKFLOW = "osee_ats_decisionReview_workflow";
   public static String PEERTOPEER_REVIEW_WORKFLOW = "osee_ats_peerToPeerReview_workflow";

   public AtsWorkFlow getWorkflow(StateMachineArtifact sma) throws SQLException, IOException {
      if (AtsPlugin.isAtsUseWorkflowFiles()) {
         String workflowId = "";
         String workflowXml = "";
         System.err.println("ATS Admin - Retrieving WorkFlows from Files");
         /**
          * NOTE: Loading from files will only work if DB has workflow configured and loaded into DB via a DB wipe. This
          * load uses the name of the workflow which needs to correspond to the id declared in the AtsWorkflow extension
          * point. Loading from files is a developers tool for rapid creation of workflows only and should not be used
          * as a deployment solution.
          */
         if (sma instanceof TeamWorkFlowArtifact) {
            workflowId = DEFAULT_TEAM_WORKFLOW;
            workflowXml = ImportWorkflowAction.getWorkflowXmlFromFile(workflowId);
         } else if (sma instanceof TaskArtifact) {
            workflowId = DEFAULT_TASK_WORKFLOW;
            workflowXml = ImportWorkflowAction.getWorkflowXmlFromFile(DEFAULT_TASK_WORKFLOW);
         } else if (sma instanceof DecisionReviewArtifact) {
            workflowId = DECISION_REVIEW_WORKFLOW;
            workflowXml = ImportWorkflowAction.getWorkflowXmlFromFile(DECISION_REVIEW_WORKFLOW);
         } else if (sma instanceof PeerToPeerReviewArtifact) {
            workflowId = PEERTOPEER_REVIEW_WORKFLOW;
            workflowXml = ImportWorkflowAction.getWorkflowXmlFromFile(PEERTOPEER_REVIEW_WORKFLOW);
         } else
            throw new IllegalStateException("Can't retrieve workflow file xml");
         System.err.println("Workflow Id: " + workflowId);
         return WorkflowDiagramFactory.getInstance().getWorkFlowFromFileContents(workflowId, workflowXml);
      } else {
         // If TaskArtifact, walk up team definition tree till find a workflow diagram or
         // exception
         // if not found
         NativeArtifact nativeArt = null;
         if (sma instanceof TaskArtifact) {
            nativeArt =
                  getTeamDefinitionWorkflowDiagram(RelationSide.TeamDefinitionToTaskWorkflowDiagram_WorkflowDiagram,
                        ((TeamWorkFlowArtifact) ((TaskArtifact) sma).getParentSMA()).getTeamDefinition());
         } else if (sma instanceof DecisionReviewArtifact) {
            nativeArt =
                  getTeamDefinitionWorkflowDiagram(
                        RelationSide.TeamDefinitionToDecisionReviewWorkflowDiagram_WorkflowDiagram,
                        ((TeamWorkFlowArtifact) ((DecisionReviewArtifact) sma).getParentSMA()).getTeamDefinition());
         } else if (sma instanceof PeerToPeerReviewArtifact) {
            PeerToPeerReviewArtifact peerArt = (PeerToPeerReviewArtifact) sma;
            if (peerArt.getParentSMA() == null)
               nativeArt =
                     getTeamDefinitionWorkflowDiagram(
                           RelationSide.TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram,
                           TeamDefinitionArtifact.getHeadTeamDefinition());
            else
               nativeArt =
                     getTeamDefinitionWorkflowDiagram(
                           RelationSide.TeamDefinitionToPeerToPeerReviewWorkflowDiagram_WorkflowDiagram,
                           ((TeamWorkFlowArtifact) ((PeerToPeerReviewArtifact) sma).getParentSMA()).getTeamDefinition());
         }
         // If TeamWorkflowArtifact, walk up team tree till find a workflow diagram or exception
         // if not found
         else if (sma instanceof TeamWorkFlowArtifact) {
            nativeArt =
                  getTeamDefinitionWorkflowDiagram(RelationSide.TeamDefinitionToWorkflowDiagram_WorkflowDiagram,
                        ((TeamWorkFlowArtifact) sma).getTeamDefinition());
         }
         if (nativeArt != null)
            return WorkflowDiagramFactory.getInstance().getAtsWorkflowFromArtifact(nativeArt);
         else
            throw new IllegalArgumentException("Unhandled artifact type for team workflow diagram");
      }
   }

   private NativeArtifact getTeamDefinitionWorkflowDiagram(RelationSide side, TeamDefinitionArtifact teamDef) throws SQLException {
      Collection<NativeArtifact> arts = teamDef.getArtifacts(side, NativeArtifact.class);
      if (arts.size() > 1) throw new IllegalArgumentException("Expected 1 Workflow diagram.  Retrieved " + arts.size());
      if (arts.size() == 1) return arts.iterator().next();
      if (teamDef.getParent() instanceof TeamDefinitionArtifact)
         return getTeamDefinitionWorkflowDiagram(side, (TeamDefinitionArtifact) teamDef.getParent());
      else
         throw new IllegalArgumentException("No Workflow Diagram configured for path");
   }

}
