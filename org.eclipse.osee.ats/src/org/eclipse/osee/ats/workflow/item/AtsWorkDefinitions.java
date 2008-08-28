/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionDecisionWorkPageDefinition;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkDefinitionProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * Create all the default ATS work items. This keeps from having to create a class for each of these. Also implement
 * WorkDefinitionProvider which registers all definitions with the definitions factory
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitions implements IWorkDefinitionProvider {

   public static String ATS_TITLE_ID = "ats.Title";
   public static String ATS_DESCRIPTION_NOT_REQUIRED_ID =
         ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName() + ".notRequired";
   public static enum RuleWorkItemId {
      atsRequireStateHourSpentPrompt("Work Page Option: Will popup a dialog to prompt user for time spent in this state."),
      atsAddDecisionValidateBlockingReview("Work Page Option: Will auto-create a blocking decision review for this state requesting validation for this workflow."),
      atsAddDecisionValidateNonBlockingReview("Work Page Option: Will auto-create a non blocking decision review requesting validation of workflow changes."),
      atsAllowTransitionWithWorkingBranch("Work Page Option: Will allow transition to next state without committing current working branch."),
      atsAllowCreateBranch("Work Page Option: Allows a working branch to be created in this state."),
      atsAllowCommitBranch("Work Page Option: Allows a working branch to be committed in this state."),
      atsForceAssigneesToTeamLeads("Work Page Option: Will force this state to be assigned back to the configured team leads.  Useful for authorization state."),
      atsRequireTargetedVersion("Work Page and Team Definition Option: Requires workflow to be targeted for version before transition is allowed."),
      atsAllowPriviledgedEditToTeamMember("Work Page and Team Definition Option: Allow team member to priviledged edit workflow assigned to team."),
      atsAllowPriviledgedEditToTeamMemberAndOriginator("Work Page and Team Definition Option: Allow team member to priviledged edit workflow assigned to team if user is originator."),
      atsAllowPriviledgedEditToAll("Work Page and Team Definition Option: Allow anyone to priviledged edit workflow assigned to team."),
      atsAllowEditToAll("Work Page and Team Definition Option: Allow anyone to edit workflow.");

      public final String description;

      public String getDescription() {
         return description;
      }

      private RuleWorkItemId(String description) {
         this.description = description;
      }
   }

   public static void relatePageToBranchCommitRules(String pageId) throws OseeCoreException, SQLException {
      WorkItemDefinitionFactory.relateWorkItemDefinitions(pageId, RuleWorkItemId.atsAllowCommitBranch.name());
      WorkItemDefinitionFactory.relateWorkItemDefinitions(pageId, RuleWorkItemId.atsAllowCreateBranch.name());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Create rule work items
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsRequireStateHourSpentPrompt.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAddDecisionValidateBlockingReview.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAddDecisionValidateNonBlockingReview.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowTransitionWithWorkingBranch.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowCreateBranch.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowCommitBranch.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsForceAssigneesToTeamLeads.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsRequireTargetedVersion.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowPriviledgedEditToTeamMember.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowPriviledgedEditToTeamMemberAndOriginator.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowPriviledgedEditToAll.name()));
      workItems.add(new WorkRuleDefinition(RuleWorkItemId.atsAllowEditToAll.name()));

      // Create XWidget work items
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.LOCATION_ATTRIBUTE, "XTextDam", XOption.REQUIRED,
            XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE, "XTextDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.CATEGORY_ATTRIBUTE, "XTextDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE, "XTextDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.SMA_NOTE_ATTRIBUTE, "XTextDam",
            XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem("Title", "ats.Title", "Name", "XTextDam", XOption.REQUIRED));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE, "XTextDam",
            XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE, "XTextDam",
            XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.PROBLEM_ATTRIBUTE, "XTextDam",
            XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem("Question",
            AtsDecisionDecisionWorkPageDefinition.DECISION_XWIDGET_ID, "Name", "XLabelDam"));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.PROPOSED_RESOLUTION_ATTRIBUTE, "XTextDam",
            XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.DESCRIPTION_ATTRIBUTE, "XTextDam", XOption.REQUIRED,
            XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.DESCRIPTION_ATTRIBUTE.getDisplayName(),
            ATS_DESCRIPTION_NOT_REQUIRED_ID, ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "XTextDam",
            XOption.NOT_REQUIRED, XOption.FILL_VERTICALLY));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.BLOCKING_REVIEW_ATTRIBUTE, "XComboBooleanDam",
            XOption.REQUIRED, XOption.HORIZONTAL_LABEL));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.DECISION_ATTRIBUTE, "XComboDam(1,2,3)",
            XOption.REQUIRED, XOption.HORIZONTAL_LABEL));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, "XFloatDam",
            XOption.REQUIRED));
      workItems.add(new AtsAttributeSoleComboXWidgetWorkItem(ATSAttributes.CHANGE_TYPE_ATTRIBUTE,
            "OPTIONS_FROM_ATTRIBUTE_VALIDITY", XOption.REQUIRED, XOption.BEGIN_COMPOSITE_6));
      workItems.add(new AtsAttributeSoleComboXWidgetWorkItem(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE,
            "OPTIONS_FROM_ATTRIBUTE_VALIDITY", XOption.REQUIRED));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.DEADLINE_ATTRIBUTE, "XDateDam",
            XOption.HORIZONTAL_LABEL, XOption.END_COMPOSITE));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE, "XCheckBoxDam",
            XOption.HORIZONTAL_LABEL, XOption.LABEL_BEFORE));
      workItems.add(new AtsAttributeXWidgetWorkItem(ATSAttributes.USER_COMMUNITY_ATTRIBUTE,
            "XListDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)", XOption.HORIZONTAL_LABEL, XOption.REQUIRED));
      workItems.add(new AtsAttributeReviewDefectXWidgetWorkItem(ATSAttributes.REVIEW_DEFECT_ATTRIBUTE));
      workItems.add(new AtsAttributeReviewRolesXWidgetWorkItem(ATSAttributes.ROLE_ATTRIBUTE));

      return workItems;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkDefinitionProvider#getDynamicWorkItemDefinitionsForPage()
    */
   @Override
   public Collection<WorkItemDefinition> getDynamicWorkItemDefinitionsForPage(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, Object data) throws OseeCoreException, SQLException {
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      if (data instanceof SMAManager) {
         SMAManager smaMgr = (SMAManager) data;
         if (workPageDefinition.isInstanceOfPage(AtsCancelledWorkPageDefinition.ID)) {
            defs.add(new AtsCancelledFromStateWorkItem(smaMgr));
            defs.add(new AtsCancellationReasonStateWorkItem(smaMgr));
         }
         if (workPageDefinition.isInstanceOfPage(AtsCompletedWorkPageDefinition.ID)) {
            defs.add(new AtsCompletedFromStateWorkItem(smaMgr));
         }
      }
      return defs;
   }

   @Override
   public Collection<WorkItemDefinition> getProgramaticWorkItemDefinitions() throws OseeCoreException, SQLException {
      return new ArrayList<WorkItemDefinition>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkDefinitionProvider#getWorkFlowDefinition(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public WorkFlowDefinition getWorkFlowDefinition(Artifact artifact) throws OseeCoreException, SQLException {
      if (artifact instanceof TeamWorkFlowArtifact) {
         // return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(TeamWorkflowDefinition.ID);
         return ((TeamWorkFlowArtifact) artifact).getTeamDefinition().getWorkFlowDefinition();
      }
      if (artifact instanceof TaskArtifact) return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(TaskWorkflowDefinition.ID);
      if (artifact instanceof PeerToPeerReviewArtifact) return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(PeerToPeerWorkflowDefinition.ID);
      if (artifact instanceof DecisionReviewArtifact) return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(DecisionWorkflowDefinition.ID);
      return null;
   }

   public static boolean isValidatePage(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      if (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAddDecisionValidateBlockingReview.name()) != null) return true;
      if (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAddDecisionValidateNonBlockingReview.name()) != null) return true;
      return false;
   }

   public static boolean isValidateReviewBlocking(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAddDecisionValidateBlockingReview.name()) != null);
   }

   public static boolean isForceAssigneesToTeamLeads(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsForceAssigneesToTeamLeads.name()) != null);
   }

   public static boolean isAllowTransitionWithWorkingBranch(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAllowTransitionWithWorkingBranch.name()) != null);
   }

   public static boolean isRequireStateHoursSpentPrompt(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsRequireStateHourSpentPrompt.name()) != null);
   }

   public static boolean isAllowCreateBranch(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAllowCreateBranch.name()) != null);
   }

   public static boolean isAllowCommitBranch(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return (workPageDefinition.getWorkItemDefinition(AtsWorkDefinitions.RuleWorkItemId.atsAllowCommitBranch.name()) != null);
   }

   public static void importWorkItemDefinitionsIntoDb(WriteType writeType, XResultData resultData, WorkItemDefinition workItemDefinition) throws OseeCoreException, SQLException {
      importWorkItemDefinitionsIntoDb(writeType, resultData,
            Arrays.asList(new WorkItemDefinition[] {workItemDefinition}));
   }

   public static void importWorkItemDefinitionsIntoDb(final WriteType writeType, final XResultData resultData, final Collection<? extends WorkItemDefinition> workItemDefinitions) throws OseeCoreException, SQLException {
      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

         @Override
         protected void handleTxWork() throws OseeCoreException, SQLException {
            // Items must be imported in order due to the relations that are created between items
            for (Class<?> clazz : new Class[] {WorkRuleDefinition.class, WorkWidgetDefinition.class,
                  WorkPageDefinition.class, WorkFlowDefinition.class}) {
               for (WorkItemDefinition wid : workItemDefinitions) {
                  if (clazz.isInstance(wid)) {
                     // System.out.println("Adding " + wid.getId() + " as class " + clazz);
                     Artifact art = wid.toArtifact(writeType);
                     // Relate if not already related
                     if (art.getArtifacts(AtsRelation.WorkItem__Parent, Artifact.class).size() == 0) {
                        if (wid instanceof WorkPageDefinition) {
                           relateIfNotRelated(AtsConfig.getInstance().getOrCreateWorkPagesFolderArtifact(), art);
                        }
                        if (wid instanceof WorkRuleDefinition) {
                           relateIfNotRelated(AtsConfig.getInstance().getOrCreateWorkRulesFolderArtifact(), art);
                        }
                        if (wid instanceof WorkWidgetDefinition) {
                           relateIfNotRelated(AtsConfig.getInstance().getOrCreateWorkWidgetsFolderArtifact(), art);
                        }
                        if (wid instanceof WorkFlowDefinition) {
                           relateIfNotRelated(AtsConfig.getInstance().getOrCreateWorkFlowsFolderArtifact(), art);
                        }
                     }
                     if (art.isDirty(true) && resultData != null) {
                        resultData.log("Updated artifact " + art);
                     }
                     art.persistAttributesAndRelations();
                  }
               }
            }
         }
      };
      try {
         newActionTx.execute();
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   private static void relateIfNotRelated(Artifact parent, Artifact child) throws OseeCoreException, SQLException {
      if (!parent.getArtifacts(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, Artifact.class).contains(child)) {
         parent.addChild(child);
      }
   }
}
