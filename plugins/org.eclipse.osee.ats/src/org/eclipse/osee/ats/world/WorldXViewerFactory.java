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
package org.eclipse.osee.ats.world;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.field.CategoryColumn;
import org.eclipse.osee.ats.field.ChangeTypeColumn;
import org.eclipse.osee.ats.field.GoalsColumn;
import org.eclipse.osee.ats.field.OperationalImpactColumn;
import org.eclipse.osee.ats.field.OperationalImpactDesciptionColumn;
import org.eclipse.osee.ats.field.OperationalImpactWorkaroundColumn;
import org.eclipse.osee.ats.field.OperationalImpactWorkaroundDesciptionColumn;
import org.eclipse.osee.ats.field.PriorityColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerSmaCreatedDateColumn;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerGuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerHridColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerLastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerLastModifiedDateColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerFactory extends SkynetXViewerFactory {

   public GoalArtifact soleGoalArtifact;
   public static final String COLUMN_NAMESPACE = "ats.column";
   public static final XViewerColumn Type_Col = new XViewerColumn("ats.column.type", "Type", 150, SWT.LEFT, true,
      SortDataType.String, false, null);
   public static final XViewerColumn State_Col = new XViewerColumn("ats.column.state", "State", 75, SWT.LEFT, true,
      SortDataType.String, false, null);
   public static final XViewerColumn Assignees_Col = new XViewerAtsAttributeColumn(ATSAttributes.ASSIGNEE_ATTRIBUTE,
      100, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Title_Col = new XViewerArtifactNameColumn("Title");
   public static final XViewerColumn Actionable_Items_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".actionableItems", AtsAttributeTypes.ActionableItem, 80, SWT.LEFT, true, SortDataType.String,
      false);
   public static final XViewerColumn User_Community_Col = new XViewerAtsAttributeColumn(
      AtsAttributeTypes.UserCommunity, 60, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Parent_ID_Col = new XViewerColumn(COLUMN_NAMESPACE + ".parenthrid", "Parent HRID",
      75, SWT.LEFT, false, SortDataType.String, false, "Human Readable ID of Parent Action or Team Workflow");
   public static final XViewerColumn Parent_State_Col = new XViewerColumn(COLUMN_NAMESPACE + ".parentstate",
      "Parent State", 75, SWT.LEFT, false, SortDataType.String, false, "State of the Parent Team Workflow or Action");
   public static final XViewerColumn Created_Date_Col = new XViewerSmaCreatedDateColumn();
   public static final XViewerColumn Version_Target_Col = new XViewerColumn(COLUMN_NAMESPACE + ".versionTarget",
      "Version Target", 40, SWT.LEFT, true, SortDataType.String, false,
      "Date this workflow transitioned to the Completed state.");
   public static final XViewerColumn Team_Col = new XViewerColumn(COLUMN_NAMESPACE + ".team", "Team", 50, SWT.LEFT,
      true, SortDataType.String, false, "Team that has been assigned to work this Action.");
   // Can't be an XViewerAtsAttributeColumn cause display name is not same as attribute name
   public static final XViewerColumn Notes_Col = new XViewerColumn(COLUMN_NAMESPACE + ".notes", "Notes", 80, SWT.LEFT,
      true, SortDataType.String, true, "");
   public static final XViewerColumn Deadline_Col = new XViewerAtsAttributeColumn(COLUMN_NAMESPACE + ".deadline",
      AtsAttributeTypes.NeedBy, 75, SWT.LEFT, true, SortDataType.Date, true);

   // Aren't shown by default
   public static final XViewerColumn Annual_Cost_Avoidance_Col =
      new XViewerColumn(
         COLUMN_NAMESPACE + ".annualCostAvoidance",
         "Annual Cost Avoidance",
         50,
         SWT.LEFT,
         false,
         SortDataType.Float,
         false,
         "Hours that would be saved for the first year if this change were completed.\n\n" + "(Weekly Benefit Hours * 52 weeks) - Remaining Hours\n\n" + "If number is high, benefit is great given hours remaining.");
   public static final XViewerColumn Description_Col = new XViewerAtsAttributeColumn(AtsAttributeTypes.Description,
      150, SWT.LEFT, false, SortDataType.String, true);
   public static XViewerColumn Legacy_PCR_Col = new XViewerAtsAttributeColumn(COLUMN_NAMESPACE + ".legacyPcr",
      AtsAttributeTypes.LegacyPcrId, 40, SWT.LEFT, false, SortDataType.String, false);
   public static final XViewerColumn Decision_Col = new XViewerAtsAttributeColumn(AtsAttributeTypes.Decision, 150,
      SWT.LEFT, false, SortDataType.String, false);
   public static final XViewerColumn Resolution_Col = new XViewerAtsAttributeColumn(AtsAttributeTypes.Resolution, 150,
      SWT.LEFT, false, SortDataType.String, true);
   public static XViewerColumn Estimated_Hours_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".estimatedHours", AtsAttributeTypes.EstimatedHours, 40, SWT.CENTER, false,
      SortDataType.Float, true);
   public static XViewerColumn Estimated_Release_Date_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".estimatedReleaseDate", AtsAttributeTypes.EstimatedReleaseDate, 80, SWT.LEFT, false,
      SortDataType.Date, true);
   public static XViewerColumn Estimated_Completion_Date_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".estimatedCompletionDate", AtsAttributeTypes.EstimatedCompletionDate, 80, SWT.LEFT, false,
      SortDataType.Date, true);
   public static final XViewerColumn Release_Date_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".releaseDate", AtsAttributeTypes.ReleaseDate, 80, SWT.LEFT, false, SortDataType.Date, false);
   public static final XViewerColumn Work_Package_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".workPackage", AtsAttributeTypes.WorkPackage, 80, SWT.LEFT, false, SortDataType.String, true);
   public static final XViewerColumn Points_Col = new XViewerAtsAttributeColumn(AtsAttributeTypes.Points, 40, SWT.LEFT,
      false, SortDataType.Integer, true);
   public static final XViewerColumn Numeric1_Col = new XViewerAtsAttributeColumn(AtsAttributeTypes.Numeric1, 40,
      SWT.LEFT, false, SortDataType.Float, true);
   public static final XViewerColumn Numeric2_Col = new XViewerAtsAttributeColumn(AtsAttributeTypes.Numeric2, 40,
      SWT.LEFT, false, SortDataType.Float, true);
   public static final XViewerColumn Goal_Order = new XViewerColumn(COLUMN_NAMESPACE + ".goalOrder", "Goal Order", 45,
      SWT.LEFT, false, SortDataType.Integer, true,
      "Order of item within displayed goal.  Editing this field changes order.");
   public static final XViewerColumn Goal_Order_Vote_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".goalOrderVote", AtsAttributeTypes.GoalOrderVote, 40, SWT.LEFT, false, SortDataType.String,
      true);

   public static XViewerColumn Related_To_State_Col = new XViewerColumn(COLUMN_NAMESPACE + ".relatedToState",
      AtsAttributeTypes.RelatedToState.getUnqualifiedName(), 80, SWT.LEFT, false, SortDataType.String, true,
      "Parent workflow state that task is to be worked in.");
   public static final XViewerColumn Weekly_Benefit_Hrs_Col = new XViewerAtsAttributeColumn(
      COLUMN_NAMESPACE + ".weeklyBenefitHrs", AtsAttributeTypes.WeeklyBenefit, 40, SWT.CENTER, false,
      SortDataType.Float, true);
   public static final XViewerColumn Remaining_Hours_Col = new XViewerColumn(COLUMN_NAMESPACE + ".remainingHours",
      "Remaining Hours", 40, SWT.CENTER, false, SortDataType.Float, false,
      "Hours that remain to complete the changes.\n\nEstimated Hours - (Estimated Hours * Percent Complete).");

   public static final XViewerColumn Percent_Complete_State_Col = new XViewerColumn(
      COLUMN_NAMESPACE + ".statePercentComplete", "State Percent Complete", 40, SWT.CENTER, false,
      SortDataType.Percent, false,
      "Percent Complete for the changes to the current state.\n\nAmount entered from user.");
   public static final XViewerColumn Percent_Complete_State_Task_Col =
      new XViewerColumn(
         COLUMN_NAMESPACE + ".stateTaskPercentComplete",
         "State Task Percent Complete",
         40,
         SWT.CENTER,
         false,
         SortDataType.Percent,
         false,
         "Percent Complete for the tasks related to the current state.\n\nCalculation: total percent of all tasks related to state / number of tasks related to state");
   public static final XViewerColumn Percent_Complete_State_Review_Col =
      new XViewerColumn(
         COLUMN_NAMESPACE + ".stateReviewPercentComplete",
         "State Review Percent Complete",
         40,
         SWT.CENTER,
         false,
         SortDataType.Percent,
         false,
         "Percent Complete for the reviews related to the current state.\n\nCalculation: total percent of all reviews related to state / number of reviews related to state");
   public static final XViewerColumn Percent_Complete_Total_Col = new XViewerColumn(
      COLUMN_NAMESPACE + ".totalPercentComplete", "Total Percent Complete", 40, SWT.CENTER, false,
      SortDataType.Percent, false, "Percent Complete for the reviews related to the current state.");

   public static final XViewerColumn Hours_Spent_State_Col = new XViewerColumn(COLUMN_NAMESPACE + ".stateHoursSpent",
      "State Hours Spent", 40, SWT.CENTER, false, SortDataType.Float, false,
      "Hours spent in performing the changes to the current state.");
   public static final XViewerColumn Hours_Spent_State_Task_Col = new XViewerColumn(
      COLUMN_NAMESPACE + ".stateTaskHoursSpent", "State Task Hours Spent", 40, SWT.CENTER, false, SortDataType.Float,
      false, "Hours spent in performing the changes for the tasks related to the current state.");
   public static final XViewerColumn Hours_Spent_State_Review_Col = new XViewerColumn(
      COLUMN_NAMESPACE + ".stateReviewHoursSpent", "State Review Hours Spent", 40, SWT.CENTER, false,
      SortDataType.Float, false, "Hours spent in performing the changes for the reveiws related to the current state.");
   public static final XViewerColumn Hours_Spent_Total_Col = new XViewerColumn(
      COLUMN_NAMESPACE + ".stateTotalHoursSpent", "State Total Hours Spent", 40, SWT.CENTER, false, SortDataType.Float,
      false, "Hours spent for all work related to the current state.");

   public static final XViewerColumn Total_Hours_Spent_Col = new XViewerColumn(COLUMN_NAMESPACE + ".totalHoursSpent",
      "Total Hours Spent", 40, SWT.CENTER, false, SortDataType.Float, false,
      "Hours spent for all work related to all states.");

   public static final XViewerColumn Originator_Col = new XViewerColumn(COLUMN_NAMESPACE + ".originator", "Originator",
      80, SWT.LEFT, false, SortDataType.String, false, null);
   public static final XViewerColumn Implementor_Col = new XViewerColumn(COLUMN_NAMESPACE + ".implementer",
      "Implementer", 80, SWT.LEFT, false, SortDataType.String, false,
      "User assigned to the Implementation of the changes.");
   public static final XViewerColumn Review_Author_Col = new XViewerColumn(COLUMN_NAMESPACE + ".reviewAuthor",
      "Review Author", 100, SWT.LEFT, false, SortDataType.String, false, "Review Author(s)");
   public static final XViewerColumn Review_Moderator_Col = new XViewerColumn(COLUMN_NAMESPACE + ".reviewModerator",
      "Review Moderator", 100, SWT.LEFT, false, SortDataType.String, false, "Review Moderator(s)");
   public static final XViewerColumn Groups_Col = new XViewerColumn(COLUMN_NAMESPACE + ".groups", "Groups", 100,
      SWT.LEFT, false, SortDataType.String, true, "Groups");
   public static final XViewerColumn Review_Reviewer_Col = new XViewerColumn(COLUMN_NAMESPACE + ".reviewReviewer",
      "Review Reviewer", 100, SWT.LEFT, false, SortDataType.String, false, "Review Reviewer(s)");
   public static final XViewerColumn Review_Decider_Col = new XViewerColumn(COLUMN_NAMESPACE + ".reviewDecider",
      "Review Decider", 100, SWT.LEFT, false, SortDataType.String, false, "Review Decider");
   public static final XViewerColumn Completed_Date_Col = new XViewerColumn(COLUMN_NAMESPACE + ".completedDate",
      "Completed Date", 80, SWT.CENTER, false, SortDataType.Date, false, null);
   public static final XViewerColumn Cancelled_Date_Col = new XViewerColumn(COLUMN_NAMESPACE + ".cancelledDate",
      "Cancelled Date", 80, SWT.CENTER, false, SortDataType.Date, false, null);
   public static final XViewerColumn Work_Days_Needed_Col = new XViewerColumn(COLUMN_NAMESPACE + ".workDaysNeeded",
      "Hours Per Work Day", 40, SWT.CENTER, false, SortDataType.Float, false, null);
   public static final XViewerColumn Days_In_Current_State = new XViewerColumn(COLUMN_NAMESPACE + ".daysInCurrState",
      "Days in Current State", 40, SWT.CENTER, false, SortDataType.Float, false, null);
   public static final XViewerColumn Percent_Rework_Col = new XViewerColumn(COLUMN_NAMESPACE + ".percentRework",
      "Percent Rework", 40, SWT.CENTER, false, SortDataType.Percent, false, null);
   public static final XViewerColumn Branch_Status_Col = new XViewerColumn(COLUMN_NAMESPACE + ".branchStatus",
      "Branch Status", 40, SWT.CENTER, false, SortDataType.String, false, null);
   public static final XViewerColumn Number_of_Tasks_Col = new XViewerColumn(COLUMN_NAMESPACE + ".numberOfTasks",
      "Number of Tasks", 40, SWT.CENTER, false, SortDataType.Integer, false, null);
   public static final XViewerColumn Number_of_Tasks_Remining_Col = new XViewerColumn(
      COLUMN_NAMESPACE + ".numberOfTasksRemain", "Number of Tasks Remaining", 40, SWT.CENTER, false,
      SortDataType.Integer, false, null);
   public static final XViewerColumn Last_Modified_By_Col = new XViewerColumn(COLUMN_NAMESPACE + ".lastModifiedBy",
      "Last Modified By", 40, SWT.CENTER, false, SortDataType.String, false,
      "Retrieves user of last attribute update of this artifact.");
   public static final XViewerColumn Last_Statused_Col = new XViewerColumn(COLUMN_NAMESPACE + ".lastStatused",
      "Last Statused", 40, SWT.CENTER, false, SortDataType.Date, false,
      "Retrieves timestamp of status (percent completed or hours spent).");
   public static final XViewerColumn Validation_Required_Col = new XViewerColumn(
      COLUMN_NAMESPACE + ".validationRequired", "Validation Required", 80, SWT.LEFT, false, SortDataType.String, false,
      "If set, Originator will be asked to perform a review to\nensure changes are as expected.");
   public static final XViewerColumn Review_Minor_Defects = new XViewerColumn(COLUMN_NAMESPACE + ".reviewMinorDefects",
      "Review Minor Defects", 40, SWT.CENTER, false, SortDataType.Integer, false,
      "Number of Minor Defects found in Review");
   public static final XViewerColumn Review_Major_Defects = new XViewerColumn(COLUMN_NAMESPACE + ".reviewMajorDefects",
      "Review Major Defects", 40, SWT.CENTER, false, SortDataType.Integer, false,
      "Number of Major Defects found in Review");
   public static final XViewerColumn Review_Issues = new XViewerColumn(COLUMN_NAMESPACE + ".reviewIssues",
      "Review Issues", 40, SWT.CENTER, false, SortDataType.Integer, false, "Number of Issues found in Review");
   public static final XViewerColumn Originating_Workflow = new XViewerColumn("ats.column.origWf",
      "Originating Workflow", 150, SWT.LEFT, false, SortDataType.String, false,
      "Team Workflow(s) that were created upon origination of this Action.  Cancelled workflows not included.");
   public static final XViewerColumn Actions_Initiating_Workflow_Col = new XViewerColumn("ats.column.initWf",
      "Action's Initiating Workflow", 150, SWT.LEFT, false, SortDataType.String, false,
      "This is the first workflow(s) that created the initiation of the Action");
   public static final XViewerColumn Artifact_Type_Col = new XViewerArtifactTypeColumn(true);
   public static final XViewerColumn[] WorldViewColumns = new XViewerColumn[] {
      Type_Col,
      State_Col,
      PriorityColumn.getInstance(),
      ChangeTypeColumn.getInstance(),
      Assignees_Col,
      Title_Col,
      Actionable_Items_Col,
      User_Community_Col,
      new XViewerHridColumn(),
      Created_Date_Col,
      Version_Target_Col,
      Team_Col,
      Notes_Col,
      Deadline_Col,
      Annual_Cost_Avoidance_Col,
      Description_Col,
      Legacy_PCR_Col,
      Decision_Col,
      Resolution_Col,
      Groups_Col,
      GoalsColumn.getInstance(),
      Estimated_Release_Date_Col,
      Estimated_Completion_Date_Col,
      Release_Date_Col,
      Work_Package_Col,
      new CategoryColumn(CategoryColumn.Category1Attribute),
      new CategoryColumn(CategoryColumn.Category2Attribute),
      new CategoryColumn(CategoryColumn.Category3Attribute),
      Goal_Order,
      Goal_Order_Vote_Col,
      Related_To_State_Col,
      Estimated_Hours_Col,
      Weekly_Benefit_Hrs_Col,
      Remaining_Hours_Col,
      Percent_Complete_State_Col,
      Percent_Complete_State_Task_Col,
      Percent_Complete_State_Review_Col,
      Percent_Complete_Total_Col,
      Hours_Spent_State_Col,
      Hours_Spent_State_Task_Col,
      Hours_Spent_State_Review_Col,
      Hours_Spent_Total_Col,
      Total_Hours_Spent_Col,
      Originator_Col,
      new OperationalImpactColumn(),
      new OperationalImpactDesciptionColumn(),
      new OperationalImpactWorkaroundColumn(),
      new OperationalImpactWorkaroundDesciptionColumn(),
      Implementor_Col,
      Review_Author_Col,
      Review_Moderator_Col,
      Review_Reviewer_Col,
      Review_Decider_Col,
      Completed_Date_Col,
      Cancelled_Date_Col,
      Work_Days_Needed_Col,
      Percent_Rework_Col,
      Branch_Status_Col,
      Number_of_Tasks_Col,
      Number_of_Tasks_Remining_Col,
      new XViewerLastModifiedByColumn(false),
      new XViewerLastModifiedDateColumn(false),
      Last_Statused_Col,
      Validation_Required_Col,
      Review_Major_Defects,
      Review_Minor_Defects,
      Review_Issues,
      Actions_Initiating_Workflow_Col,
      Artifact_Type_Col,
      Originating_Workflow,
      Parent_ID_Col,
      Days_In_Current_State,
      Parent_State_Col,
      Points_Col,
      Numeric1_Col,
      Numeric2_Col,
      new XViewerGuidColumn(false)};
   private final static String NAMESPACE = "org.eclipse.osee.ats.WorldXViewer";

   public WorldXViewerFactory() {
      super(NAMESPACE);
      registerColumns(WorldViewColumns);
      // Register all ats.* attribute columns
      try {
         for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
            if (attributeType.getName().startsWith("ats.")) {
               registerColumns(getAttributeColumn(attributeType));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      // Register any columns from other plugins
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            for (XViewerColumn xCol : item.getXViewerColumns()) {
               registerColumns(xCol);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

}
