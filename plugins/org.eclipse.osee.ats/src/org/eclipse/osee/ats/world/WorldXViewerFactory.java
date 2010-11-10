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
import org.eclipse.osee.ats.field.BranchStatusColumn;
import org.eclipse.osee.ats.field.CancelledDateColumn;
import org.eclipse.osee.ats.field.CategoryColumn;
import org.eclipse.osee.ats.field.ChangeTypeColumn;
import org.eclipse.osee.ats.field.CompletedDateColumn;
import org.eclipse.osee.ats.field.CreatedDateColumn;
import org.eclipse.osee.ats.field.DaysInCurrentStateColumn;
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
import org.eclipse.osee.ats.field.ImplementorColumn;
import org.eclipse.osee.ats.field.LastStatusedColumn;
import org.eclipse.osee.ats.field.LegacyPcrIdColumn;
import org.eclipse.osee.ats.field.NotesColumn;
import org.eclipse.osee.ats.field.NumberOfTasksColumn;
import org.eclipse.osee.ats.field.NumberOfTasksRemainingColumn;
import org.eclipse.osee.ats.field.NumericColumn;
import org.eclipse.osee.ats.field.OperationalImpactColumn;
import org.eclipse.osee.ats.field.OperationalImpactDesciptionColumn;
import org.eclipse.osee.ats.field.OperationalImpactWorkaroundColumn;
import org.eclipse.osee.ats.field.OperationalImpactWorkaroundDesciptionColumn;
import org.eclipse.osee.ats.field.OriginatingWorkFlowColumn;
import org.eclipse.osee.ats.field.OriginatorColumn;
import org.eclipse.osee.ats.field.ParentIdColumn;
import org.eclipse.osee.ats.field.ParentStateColumn;
import org.eclipse.osee.ats.field.PercentReworkColumn;
import org.eclipse.osee.ats.field.PointsColumn;
import org.eclipse.osee.ats.field.PriorityColumn;
import org.eclipse.osee.ats.field.RelatedToStateColumn;
import org.eclipse.osee.ats.field.ReleaseDateColumn;
import org.eclipse.osee.ats.field.RemainingHoursColumn;
import org.eclipse.osee.ats.field.ResolutionColumn;
import org.eclipse.osee.ats.field.ReviewAuthorColumn;
import org.eclipse.osee.ats.field.ReviewDeciderColumn;
import org.eclipse.osee.ats.field.ReviewModeratorColumn;
import org.eclipse.osee.ats.field.ReviewNumIssuesColumn;
import org.eclipse.osee.ats.field.ReviewNumMajorDefectsColumn;
import org.eclipse.osee.ats.field.ReviewNumMinorDefectsColumn;
import org.eclipse.osee.ats.field.ReviewReviewerColumn;
import org.eclipse.osee.ats.field.StateColumn;
import org.eclipse.osee.ats.field.TargetedVersionColumn;
import org.eclipse.osee.ats.field.TeamColumn;
import org.eclipse.osee.ats.field.TitleColumn;
import org.eclipse.osee.ats.field.TypeColumn;
import org.eclipse.osee.ats.field.UserCommunityColumn;
import org.eclipse.osee.ats.field.ValidationRequiredColumn;
import org.eclipse.osee.ats.field.WeeklyBenefitHrsColumn;
import org.eclipse.osee.ats.field.WorkDaysNeededColumn;
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

   public static final XViewerColumn[] WorldViewColumns = new XViewerColumn[] {
      TypeColumn.getInstance(),
      StateColumn.getInstance(),
      PriorityColumn.getInstance(),
      ChangeTypeColumn.getInstance(),
      AssigneeColumn.getInstance(),
      TitleColumn.getInstance(),
      ActionableItemsColumn.getInstance(),
      UserCommunityColumn.getInstance(),
      XViewerHridColumn.getInstance(),
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
      RemainingHoursColumn.getInstance(),
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
      OperationalImpactColumn.getInstance(),
      OperationalImpactDesciptionColumn.getInstance(),
      OperationalImpactWorkaroundColumn.getInstance(),
      OperationalImpactWorkaroundDesciptionColumn.getInstance(),
      ImplementorColumn.getInstance(),
      ReviewAuthorColumn.getInstance(),
      ReviewModeratorColumn.getInstance(),
      ReviewReviewerColumn.getInstance(),
      ReviewDeciderColumn.getInstance(),
      CompletedDateColumn.getInstance(),
      CancelledDateColumn.getInstance(),
      WorkDaysNeededColumn.getInstance(),
      PercentReworkColumn.getInstance(),
      BranchStatusColumn.getInstance(),
      NumberOfTasksColumn.getInstance(),
      NumberOfTasksRemainingColumn.getInstance(),
      new XViewerLastModifiedByColumn(false),
      new XViewerLastModifiedDateColumn(false),
      LastStatusedColumn.getInstance(),
      ValidationRequiredColumn.getInstance(),
      ReviewNumMajorDefectsColumn.getInstance(),
      ReviewNumMinorDefectsColumn.getInstance(),
      ReviewNumIssuesColumn.getInstance(),
      XViewerArtifactTypeColumn.getInstance(),
      OriginatingWorkFlowColumn.getInstance(),
      ParentIdColumn.getInstance(),
      DaysInCurrentStateColumn.getInstance(),
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
