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
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.field.ActionableItemsColumn;
import org.eclipse.osee.ats.field.AnnualCostAvoidanceColumn;
import org.eclipse.osee.ats.field.AssigneeColumn;
import org.eclipse.osee.ats.field.CategoryColumn;
import org.eclipse.osee.ats.field.ChangeTypeColumn;
import org.eclipse.osee.ats.field.CreatedDateColumn;
import org.eclipse.osee.ats.field.DeadlineColumn;
import org.eclipse.osee.ats.field.DecisionColumn;
import org.eclipse.osee.ats.field.DescriptionColumn;
import org.eclipse.osee.ats.field.EstimatedCompletionDateColumn;
import org.eclipse.osee.ats.field.EstimatedHoursColumn;
import org.eclipse.osee.ats.field.EstimatedReleaseDateColumn;
import org.eclipse.osee.ats.field.GoalOrderColumn;
import org.eclipse.osee.ats.field.GoalOrderVoteColumn;
import org.eclipse.osee.ats.field.GoalsColumn;
import org.eclipse.osee.ats.field.GroupsColumn;
import org.eclipse.osee.ats.field.LegacyPcrIdColumn;
import org.eclipse.osee.ats.field.NotesColumn;
import org.eclipse.osee.ats.field.NumericColumn;
import org.eclipse.osee.ats.field.OperationalImpactColumn;
import org.eclipse.osee.ats.field.OperationalImpactDesciptionColumn;
import org.eclipse.osee.ats.field.OperationalImpactWorkaroundColumn;
import org.eclipse.osee.ats.field.OperationalImpactWorkaroundDesciptionColumn;
import org.eclipse.osee.ats.field.OriginatorColumn;
import org.eclipse.osee.ats.field.ParentIdColumn;
import org.eclipse.osee.ats.field.ParentStateColumn;
import org.eclipse.osee.ats.field.PointsColumn;
import org.eclipse.osee.ats.field.PriorityColumn;
import org.eclipse.osee.ats.field.RelatedToStateColumn;
import org.eclipse.osee.ats.field.ReleaseDateColumn;
import org.eclipse.osee.ats.field.ResolutionColumn;
import org.eclipse.osee.ats.field.StateColumn;
import org.eclipse.osee.ats.field.TargetedVersionColumn;
import org.eclipse.osee.ats.field.TeamColumn;
import org.eclipse.osee.ats.field.TitleColumn;
import org.eclipse.osee.ats.field.TypeColumn;
import org.eclipse.osee.ats.field.UserCommunityColumn;
import org.eclipse.osee.ats.field.WeeklyBenefitHrsColumn;
import org.eclipse.osee.ats.field.WorkPackageColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
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

   public static final XViewerColumn Implementor_Col = new XViewerColumn(COLUMN_NAMESPACE + ".implementer",
      "Implementer", 80, SWT.LEFT, false, SortDataType.String, false,
      "User assigned to the Implementation of the changes.");
   public static final XViewerColumn Review_Author_Col = new XViewerColumn(COLUMN_NAMESPACE + ".reviewAuthor",
      "Review Author", 100, SWT.LEFT, false, SortDataType.String, false, "Review Author(s)");
   public static final XViewerColumn Review_Moderator_Col = new XViewerColumn(COLUMN_NAMESPACE + ".reviewModerator",
      "Review Moderator", 100, SWT.LEFT, false, SortDataType.String, false, "Review Moderator(s)");
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
      TypeColumn.getInstance(),
      StateColumn.getInstance(),
      PriorityColumn.getInstance(),
      ChangeTypeColumn.getInstance(),
      AssigneeColumn.getInstance(),
      TitleColumn.getInstance(),
      ActionableItemsColumn.getInstance(),
      UserCommunityColumn.getInstance(),
      new XViewerHridColumn(),
      CreatedDateColumn.getInstance(),
      TargetedVersionColumn.getInstance(),
      TeamColumn.getInstance(),
      NotesColumn.getInstance(),
      DeadlineColumn.getInstance(),
      AnnualCostAvoidanceColumn.getInstance(),
      DescriptionColumn.getInstance(),
      LegacyPcrIdColumn.getInstance(),
      DecisionColumn.getInstance(),
      ResolutionColumn.getInstance(),
      GroupsColumn.getInstance(),
      GoalsColumn.getInstance(),
      EstimatedReleaseDateColumn.getInstance(),
      EstimatedCompletionDateColumn.getInstance(),
      ReleaseDateColumn.getInstance(),
      WorkPackageColumn.getInstance(),
      CategoryColumn.getCategory1Instance(),
      CategoryColumn.getCategory2Instance(),
      CategoryColumn.getCategory3Instance(),
      GoalOrderColumn.getInstance(),
      GoalOrderVoteColumn.getInstance(),
      RelatedToStateColumn.getInstance(),
      EstimatedHoursColumn.getInstance(),
      WeeklyBenefitHrsColumn.getInstance(),
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
      OriginatorColumn.getInstance(),
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
      ParentIdColumn.getInstance(),
      Days_In_Current_State,
      ParentStateColumn.getInstance(),
      PointsColumn.getInstance(),
      NumericColumn.getNumeric1Instance(),
      NumericColumn.getNumeric2Instance(),
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
