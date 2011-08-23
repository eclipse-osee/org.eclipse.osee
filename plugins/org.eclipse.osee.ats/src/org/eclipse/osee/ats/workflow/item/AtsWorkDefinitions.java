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
package org.eclipse.osee.ats.workflow.item;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.task.TaskManager;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkflowProviders;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.ITeamWorkflowProvider;
import org.eclipse.osee.ats.util.widgets.XWorkingBranch;
import org.eclipse.osee.ats.util.widgets.commit.XCommitManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.FrameworkXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkDefinitionProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinitionMatch;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * Create all the default ATS work items. This keeps from having to create a class for each of these. Also implement
 * WorkDefinitionProvider which registers all definitions with the definitions factory
 * 
 * @author Donald G. Dunne
 */
public final class AtsWorkDefinitions implements IWorkDefinitionProvider {

   public static final String ATS_DESCRIPTION_NOT_REQUIRED_ID = AtsAttributeTypes.Description + ".notRequired";
   public static final String ATS_ESTIMATED_HOURS_NOT_REQUIRED_ID = AtsAttributeTypes.EstimatedHours + ".notRequired";
   public static final String TaskWorkflowDefinitionId = "osee.ats.taskWorkflow";
   public static final String GoalWorkflowDefinitionId = "osee.ats.goalWorkflow";
   public static final String PeerToPeerWorkflowDefinitionId = "osee.ats.peerToPeerReview";
   public static final String DecisionWorkflowDefinitionId = "osee.ats.decisionReview";
   public final static String DECISION_QUESTION_LABEL = "ats.Decision Question";
   public final static String DECISION_ANSWER_LABEL = "ats.Decision Answer";

   public static enum RuleWorkItemId {
      atsRequireStateHourSpentPrompt("Work Page Option: Will popup a dialog to prompt user for time spent in this state."),
      atsAddDecisionValidateBlockingReview("Work Page Option: Will auto-create a blocking decision review for this state requesting validation for this workflow."),
      atsAddDecisionValidateNonBlockingReview("Work Page Option: Will auto-create a non blocking decision review requesting validation of workflow changes."),
      atsAllowTransitionWithWorkingBranch("Work Page Option: Will allow transition to next state without committing current working branch."),
      atsForceAssigneesToTeamLeads("Work Page Option: Will force this state to be assigned back to the configured team leads.  Useful for authorization state."),
      atsRequireTargetedVersion("Work Page and Team Definition Option: Requires workflow to be targeted for version before transition is allowed."),
      atsAllowPriviledgedEditToTeamMember("Work Page and Team Definition Option: Allow team member to priviledged edit workflow assigned to team."),
      atsAllowPriviledgedEditToTeamMemberAndOriginator("Work Page and Team Definition Option: Allow team member to priviledged edit workflow assigned to team if user is originator."),
      atsAllowPriviledgedEditToAll("Work Page and Team Definition Option: Allow anyone to priviledged edit workflow assigned to team."),
      atsAllowEditToAll("Work Page and Team Definition Option: Allow anyone to edit workflow without being assignee."),
      atsAllowAssigneeToAll("Work Page and Team Definition Option: Allow anyone to change workflow assignee without being assignee.");

      public final String description;

      public String getDescription() {
         return description;
      }

      private RuleWorkItemId(String description) {
         this.description = description;
      }
   }

   public static void relatePageToBranchCommitRules(String pageId) throws OseeCoreException {
      WorkItemDefinitionFactory.relateWorkItemDefinitions(pageId, "ats.Working Branch");
      WorkItemDefinitionFactory.relateWorkItemDefinitions(pageId, "ats.Commit Manager");
   }

   @Override
   public Collection<WorkItemDefinition> getProgramaticWorkItemDefinitions() {
      return new ArrayList<WorkItemDefinition>();
   }

   public static WorkFlowDefinitionMatch getWorkFlowDefinitionFromArtifact(Artifact artifact) throws OseeCoreException {
      // If this artifact specifies it's own workflow definition, use it
      String workFlowDefId = artifact.getSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, null);
      if (Strings.isValid(workFlowDefId)) {
         WorkFlowDefinition workDef =
            (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workFlowDefId);
         if (workDef != null) {
            return new WorkFlowDefinitionMatch(workDef, String.format("from artifact [%s] for id [%s]", artifact,
               workFlowDefId));
         }
      }
      return new WorkFlowDefinitionMatch();
   }

   public WorkFlowDefinitionMatch getWorkFlowDefinitionForTask(TaskArtifact taskArt) throws OseeCoreException {
      WorkFlowDefinitionMatch match = new WorkFlowDefinitionMatch();
      for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getAtsTeamWorkflowProviders()) {
         String workFlowDefId = provider.getRelatedTaskWorkflowDefinitionId(taskArt.getParentTeamWorkflow());
         if (Strings.isValid(workFlowDefId)) {
            match.setWorkFlowDefinition((WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workFlowDefId));
            match.addTrace((String.format("from provider [%s] for id [%s] ", provider.getClass().getSimpleName(),
               workFlowDefId)));
            break;
         }
      }
      if (!match.isMatched()) {
         // If task specifies it's own workflow id, use it
         match = getWorkFlowDefinitionFromArtifact(taskArt);
      }
      if (!match.isMatched()) {
         // Else If parent SMA has a related task definition workflow id specified, use it
         WorkFlowDefinitionMatch match2 = getWorkFlowDefinitionFromArtifact(taskArt.getParentAWA());
         if (match2.isMatched()) {
            match2.addTrace(String.format("from task parent SMA [%s]", taskArt.getParentAWA()));
            match = match2;
         }
      }
      if (!match.isMatched()) {
         // Else If parent TeamWorkflow's TeamDefinition has a related task definition workflow id, use it
         match = getWorkFlowDefinitionFromArtifact(taskArt.getParentTeamWorkflow().getTeamDefinition());
      }
      if (!match.isMatched()) {
         // Else, use default Task workflow
         WorkFlowDefinition workDef =
            (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(TaskWorkflowDefinitionId);
         if (workDef != null) {
            return new WorkFlowDefinitionMatch(workDef, String.format("default TaskWorkflowDefinition ID [%s]",
               TaskWorkflowDefinitionId));
         }
      }
      return match;
   }

   @Override
   public WorkFlowDefinitionMatch getWorkFlowDefinition(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Task)) {
         return getWorkFlowDefinitionForTask(TaskManager.cast(artifact));
      }
      if (artifact instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact aba = (AbstractWorkflowArtifact) artifact;
         // Check extensions for definition handling
         for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getAtsTeamWorkflowProviders()) {
            String workFlowDefId = provider.getWorkflowDefinitionId(aba);
            if (Strings.isValid(workFlowDefId)) {
               WorkFlowDefinition workDef =
                  (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workFlowDefId);
               return new WorkFlowDefinitionMatch(workDef, (String.format("id [%s] from provider [%s]", workFlowDefId,
                  provider.getClass().getSimpleName())));
            }
         }
         // If this artifact specifies it's own workflow definition, use it
         WorkFlowDefinitionMatch match = getWorkFlowDefinitionFromArtifact(artifact);
         if (match.isMatched()) {
            return match;
         }
         // Otherwise, use workflow defined by WorkflowDefinition
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            WorkFlowDefinitionMatch match2 =
               getWorkFlowDefinitionFromTeamDefition(((TeamWorkFlowArtifact) artifact).getTeamDefinition());
            if (match2.isMatched()) {
               return match2;
            }
         }
         if (artifact.isOfType(AtsArtifactTypes.Goal)) {
            WorkFlowDefinition workDef =
               (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(GoalWorkflowDefinitionId);
            return new WorkFlowDefinitionMatch(workDef, (String.format("default GoalWorkflowDefinition ID [%s]",
               GoalWorkflowDefinitionId)));

         }
         if (artifact instanceof PeerToPeerReviewArtifact) {
            WorkFlowDefinition workDef =
               (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(PeerToPeerWorkflowDefinitionId);
            return new WorkFlowDefinitionMatch(workDef, (String.format("default PeerToPeerWorkflowDefinition ID [%s]",
               PeerToPeerWorkflowDefinitionId)));
         }
         if (artifact instanceof DecisionReviewArtifact) {
            WorkFlowDefinition workDef =
               (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(DecisionWorkflowDefinitionId);
            return new WorkFlowDefinitionMatch(workDef, (String.format("default DecisionWorkflowDefinition ID [%s]",
               DecisionWorkflowDefinitionId)));
         }
      }
      return null;
   }

   public WorkFlowDefinitionMatch getWorkFlowDefinitionFromTeamDefition(TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      Artifact teamDef = teamDefinition.getTeamDefinitionHoldingWorkFlow();
      if (teamDef == null) {
         return new WorkFlowDefinitionMatch();
      }
      Artifact workFlowArt = teamDefinition.getWorkflowArtifact(teamDef);
      if (workFlowArt == null) {
         return new WorkFlowDefinitionMatch();
      }
      WorkFlowDefinition workDef =
         (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workFlowArt.getName());
      return new WorkFlowDefinitionMatch(workDef, String.format("from teamDef [%s] related work child [%s]", teamDef,
         workFlowArt.getName()));

   }

   public static boolean isValidatePage(StateDefinition stateDefinition) {
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview)) {
         return true;
      }
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateNonBlockingReview)) {
         return true;
      }
      return false;
   }

   public static boolean isValidateReviewBlocking(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview);
   }

   public static boolean isForceAssigneesToTeamLeads(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.ForceAssigneesToTeamLeads);
   }

   public static boolean isAllowTransitionWithWorkingBranch(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch);
   }

   public static boolean isRequireStateHoursSpentPrompt(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.RequireStateHourSpentPrompt);
   }

   public static boolean isAllowCreateBranch(StateDefinition stateDefinition) {
      return stateDefinition.hasWidgetWithXWidgetName(XWorkingBranch.WIDGET_NAME);
   }

   public static boolean isAllowCommitBranch(StateDefinition stateDefinition) {
      return stateDefinition.hasWidgetWithXWidgetName(XCommitManager.WIDGET_NAME);
   }

   public static Result validateWorkItemDefinition(WorkItemDefinition workItemDefinition) {
      try {
         if (workItemDefinition instanceof WorkPageDefinition) {
            WorkPageDefinition workPageDefinition = (WorkPageDefinition) workItemDefinition;
            workPageDefinition.getWorkItems(true);
         }
         if (workItemDefinition instanceof WorkWidgetDefinition) {
            WorkWidgetDefinition workWidgetDefinition = (WorkWidgetDefinition) workItemDefinition;
            DynamicXWidgetLayoutData dynamicXWidgetLayoutData = workWidgetDefinition.get();
            XWidget xWidget = FrameworkXWidgetProvider.getInstance().createXWidget(dynamicXWidgetLayoutData);
            if (xWidget == null) {
               throw new OseeStateException("XWidget.createXWidget came back null");
            }
         }
         if (workItemDefinition instanceof WorkFlowDefinition) {
            WorkFlowDefinition workFlowDefinition = (WorkFlowDefinition) workItemDefinition;
            if (workFlowDefinition.getPagesOrdered().isEmpty()) {
               throw new OseeStateException("Work Flow must have at least one state.");
            }
            if (workFlowDefinition.getStartPage() == null) {
               throw new OseeStateException("Work Flow must have a single start page");
            }
         }
      } catch (Exception ex) {
         return new Result(ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }
}
