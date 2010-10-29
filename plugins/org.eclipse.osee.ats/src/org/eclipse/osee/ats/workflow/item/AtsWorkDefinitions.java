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

import static org.eclipse.osee.framework.ui.skynet.widgets.XOption.FILL_VERTICALLY;
import static org.eclipse.osee.framework.ui.skynet.widgets.XOption.HORIZONTAL_LABEL;
import static org.eclipse.osee.framework.ui.skynet.widgets.XOption.NONE;
import static org.eclipse.osee.framework.ui.skynet.widgets.XOption.REQUIRED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.field.CategoryColumn;
import org.eclipse.osee.ats.field.OperationalImpactWithWorkaroundXWidget.XOperationalImpactWithWorkaroundRequiredXWidgetWorkItem;
import org.eclipse.osee.ats.field.OperationalImpactWithWorkaroundXWidget.XOperationalImpactWithWorkaroundXWidgetWorkItem;
import org.eclipse.osee.ats.field.OperationalImpactXWidget.XOperationalImpactRequiredXWidgetWorkItem;
import org.eclipse.osee.ats.field.OperationalImpactXWidget.XOperationalImpactXWidgetWorkItem;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.XWorkingBranch;
import org.eclipse.osee.ats.util.widgets.commit.XCommitManager;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.GoalWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionDecisionWorkPageDefinition;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkDefinitionProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.XWidgetFactory;

/**
 * Create all the default ATS work items. This keeps from having to create a class for each of these. Also implement
 * WorkDefinitionProvider which registers all definitions with the definitions factory
 * 
 * @author Donald G. Dunne
 */
public final class AtsWorkDefinitions implements IWorkDefinitionProvider {

   public static final String ATS_DESCRIPTION_NOT_REQUIRED_ID = AtsAttributeTypes.Description + ".notRequired";
   public static final String ATS_ESTIMATED_HOURS_NOT_REQUIRED_ID = AtsAttributeTypes.EstimatedHours + ".notRequired";

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
      WorkItemDefinitionFactory.relateWorkItemDefinitions(pageId, XWorkingBranch.WIDGET_ID);
      WorkItemDefinitionFactory.relateWorkItemDefinitions(pageId, XCommitManager.WIDGET_ID);
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      getWorkItemRules(workItems);

      // Create XWidget work items
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.Location, "XTextDam", XOption.REQUIRED,
         FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.LegacyPcrId, "XTextDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.GoalOrderVote, "XTextDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(CategoryColumn.Category1, "XTextDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.RelatedToState, "XTextDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.SmaNote, "XTextDam", FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem("Title", "ats.Title", CoreAttributeTypes.Name, "XTextDam", REQUIRED));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.Resolution, "XTextDam", FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.DecisionReviewOptions, "XTextDam",
         FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.Problem, "XTextDam", FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.WorkPackage, "XTextDam", HORIZONTAL_LABEL));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.WORKING_BRANCH_WIDGET, "XWorkingBranch", NONE));
      workItems.add(new XOperationalImpactXWidgetWorkItem());
      workItems.add(new XOperationalImpactRequiredXWidgetWorkItem());
      workItems.add(new XOperationalImpactWithWorkaroundXWidgetWorkItem());
      workItems.add(new XOperationalImpactWithWorkaroundRequiredXWidgetWorkItem());
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.VALIDATE_REQ_CHANGES_WIDGET,
         "XValidateReqChangesButton", NONE));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.CREATE_CODE_TEST_TASKS_OFF_REQUIREMENTS,
         "XCreateCodeTestTasksButton", NONE));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.CHECK_SIGNALS_VIA_CDB_WIDGET,
         "XCheckSiganlsViaCDBButton", NONE));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.SHOW_CDB_DIFF_REPORT_WIDGET,
         "XShowCdbDiffReportButton", NONE));

      workItems.add(new AtsAttributeXWidgetWorkItem("Question",
         AtsDecisionDecisionWorkPageDefinition.DECISION_QUESTION_LABEL, CoreAttributeTypes.Name, "XLabelDam"));

      workItems.add(new AtsAttributeXWidgetWorkItem("Decision",
         AtsDecisionDecisionWorkPageDefinition.DECISION_ANSWER_LABEL, AtsAttributeTypes.Decision, "XLabelDam"));

      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.ProposedResolution, "XTextDam", FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.Description, "XTextDam", XOption.REQUIRED,
         FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.Description.getUnqualifiedName(),
         ATS_DESCRIPTION_NOT_REQUIRED_ID, AtsAttributeTypes.Description, "XTextDam", XOption.NOT_REQUIRED,
         FILL_VERTICALLY));

      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.ReviewBlocks,
         "XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)", XOption.REQUIRED, XOption.HORIZONTAL_LABEL));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.Decision, "XComboDam(1,2,3)", REQUIRED,
         HORIZONTAL_LABEL));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.EstimatedHours, "XFloatDam", REQUIRED));

      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.EstimatedHours.getUnqualifiedName(),
         ATS_ESTIMATED_HOURS_NOT_REQUIRED_ID, AtsAttributeTypes.EstimatedHours, "XFloatDam", XOption.NOT_REQUIRED));

      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.EstimatedCompletionDate, "XDateDam",
         XOption.HORIZONTAL_LABEL));
      workItems.add(new AtsAttributeSoleComboXWidgetWorkItem(AtsAttributeTypes.ChangeType,
         "OPTIONS_FROM_ATTRIBUTE_VALIDITY", XOption.REQUIRED, XOption.BEGIN_COMPOSITE_6));
      workItems.add(new AtsAttributeSoleComboXWidgetWorkItem(AtsAttributeTypes.PriorityType,
         "OPTIONS_FROM_ATTRIBUTE_VALIDITY", XOption.REQUIRED));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.NeedBy, "XDateDam", XOption.HORIZONTAL_LABEL,
         XOption.END_COMPOSITE));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.ValidationRequired, "XCheckBoxDam",
         XOption.HORIZONTAL_LABEL, XOption.LABEL_BEFORE));
      workItems.add(new AtsAttributeXWidgetWorkItem(AtsAttributeTypes.UserCommunity,
         "XListDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)", XOption.HORIZONTAL_LABEL));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.COMMIT_MANAGER_WIDGET, "XCommitManager"));
      workItems.add(new AtsAttributeReviewDefectXWidgetWorkItem(AtsAttributeTypes.ReviewDefect));
      workItems.add(new AtsAttributeReviewRolesXWidgetWorkItem(AtsAttributeTypes.Role));

      return workItems;
   }

   private static void getWorkItemRules(List<WorkItemDefinition> workItems) {
      // Create rule work items
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsRequireStateHourSpentPrompt.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAddDecisionValidateBlockingReview.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAddDecisionValidateNonBlockingReview.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowTransitionWithWorkingBranch.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsForceAssigneesToTeamLeads.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsRequireTargetedVersion.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowPriviledgedEditToTeamMember.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowPriviledgedEditToTeamMemberAndOriginator.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowPriviledgedEditToAll.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowEditToAll.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowAssigneeToAll.name()));
      workItems.add(new AtsAddDecisionReviewRule());
      workItems.add(new AtsAddPeerToPeerReviewRule());
      workItems.add(new AtsStatePercentCompleteWeightRule());
      workItems.add(new AtsStatePercentCompleteWeightDefaultWorkflowRule());
      workItems.add(new AtsStatePercentCompleteWeightSimpleWorkflowRule());
      workItems.add(new AtsStatePercentCompleteWeightDecisionReviewRule());
      workItems.add(new AtsStatePercentCompleteWeightPeerToPeerReviewRule());
   }

   @Override
   public Collection<WorkItemDefinition> getProgramaticWorkItemDefinitions() {
      return new ArrayList<WorkItemDefinition>();
   }

   @Override
   public WorkFlowDefinition getWorkFlowDefinition(Artifact artifact) throws OseeCoreException {
      if (artifact instanceof TeamWorkFlowArtifact) {
         // return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(TeamWorkflowDefinition.ID);
         return ((TeamWorkFlowArtifact) artifact).getTeamDefinition().getWorkFlowDefinition();
      }
      if (artifact instanceof TaskArtifact) {
         return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(TaskWorkflowDefinition.ID);
      }
      if (artifact instanceof GoalArtifact) {
         return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(GoalWorkflowDefinition.ID);
      }
      if (artifact instanceof PeerToPeerReviewArtifact) {
         return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(PeerToPeerWorkflowDefinition.ID);
      }
      if (artifact instanceof DecisionReviewArtifact) {
         return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(DecisionWorkflowDefinition.ID);
      }
      return null;
   }

   public static boolean isValidatePage(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      if (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAddDecisionValidateBlockingReview.name()) != null) {
         return true;
      }
      if (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAddDecisionValidateNonBlockingReview.name()) != null) {
         return true;
      }
      return false;
   }

   public static boolean isValidateReviewBlocking(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      return workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAddDecisionValidateBlockingReview.name()) != null;
   }

   public static boolean isForceAssigneesToTeamLeads(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      return workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsForceAssigneesToTeamLeads.name()) != null;
   }

   public static boolean isAllowTransitionWithWorkingBranch(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      return workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAllowTransitionWithWorkingBranch.name()) != null;
   }

   public static boolean isRequireStateHoursSpentPrompt(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      return workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsRequireStateHourSpentPrompt.name()) != null;
   }

   public static boolean isAllowCreateBranch(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      return workPageDefinition.getWorkItemDefinition(ATSAttributes.WORKING_BRANCH_WIDGET.getWorkItemId()) != null;
   }

   public static boolean isAllowCommitBranch(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      return workPageDefinition.getWorkItemDefinition(ATSAttributes.COMMIT_MANAGER_WIDGET.getWorkItemId()) != null;
   }

   public static void importWorkItemDefinitionsIntoDb(WriteType writeType, XResultData resultData, WorkItemDefinition workItemDefinition) throws OseeCoreException {
      importWorkItemDefinitionsIntoDb(writeType, resultData,
         Arrays.asList(new WorkItemDefinition[] {workItemDefinition}));
   }

   public static void importWorkItemDefinitionsIntoDb(final WriteType writeType, final XResultData resultData, final Collection<? extends WorkItemDefinition> workItemDefinitions) throws OseeCoreException {

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Import ATS Work Item Definitions");
      // Items must be imported in order due to the relations that are created between items
      for (Class<?> clazz : new Class[] {
         WorkRuleDefinition.class,
         WorkWidgetDefinition.class,
         WorkPageDefinition.class,
         WorkFlowDefinition.class}) {
         for (WorkItemDefinition wid : workItemDefinitions) {
            if (clazz.isInstance(wid)) {
               // System.out.println("Adding " + wid.getId() + " as class " + clazz);
               Artifact art = wid.toArtifact(writeType);
               addUpdateWorkItemToDefaultHeirarchy(art, transaction);
               if (art.isDirty() && resultData != null) {
                  resultData.log("Updated [" + art.getArtifactTypeName() + "][" + art + "]");
               }
               art.persist(transaction);
            }
         }
      }
      transaction.execute();
   }

   public static void addUpdateWorkItemToDefaultHeirarchy(Artifact art, SkynetTransaction transaction) throws OseeCoreException {
      // Relate if not already related
      if (art.getRelatedArtifacts(CoreRelationTypes.WorkItem__Parent, Artifact.class).isEmpty()) {
         if (art.isOfType(CoreArtifactTypes.WorkPageDefinition)) {
            relateIfNotRelated(AtsFolderUtil.getFolder(AtsFolder.WorkPages), art, transaction);
         }
         if (art.isOfType(CoreArtifactTypes.WorkRuleDefinition)) {
            relateIfNotRelated(AtsFolderUtil.getFolder(AtsFolder.WorkRules), art, transaction);
         }
         if (art.isOfType(CoreArtifactTypes.WorkWidgetDefinition)) {
            relateIfNotRelated(AtsFolderUtil.getFolder(AtsFolder.WorkWidgets), art, transaction);
         }
         if (art.isOfType(CoreArtifactTypes.WorkFlowDefinition)) {
            relateIfNotRelated(AtsFolderUtil.getFolder(AtsFolder.WorkFlow), art, transaction);
         }
      }
   }

   private static void relateIfNotRelated(Artifact parent, Artifact child, SkynetTransaction transaction) throws OseeCoreException {
      if (!parent.getChildren().contains(child) && !child.hasParent()) {
         parent.addChild(child);
         parent.persist(transaction);
      }
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
            XWidget xWidget = XWidgetFactory.getInstance().createXWidget(dynamicXWidgetLayoutData);
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
