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
import org.eclipse.osee.ats.column.ActionableItemsColumn;
import org.eclipse.osee.ats.column.AnnualCostAvoidanceColumn;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.column.BranchStatusColumn;
import org.eclipse.osee.ats.column.CancelledDateColumn;
import org.eclipse.osee.ats.column.CategoryColumn;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.CompletedDateColumn;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.column.DaysInCurrentStateColumn;
import org.eclipse.osee.ats.column.DeadlineColumn;
import org.eclipse.osee.ats.column.DecisionColumn;
import org.eclipse.osee.ats.column.DescriptionColumn;
import org.eclipse.osee.ats.column.EstimatedCompletionDateColumn;
import org.eclipse.osee.ats.column.EstimatedHoursColumn;
import org.eclipse.osee.ats.column.EstimatedReleaseDateColumn;
import org.eclipse.osee.ats.column.GoalOrderColumn;
import org.eclipse.osee.ats.column.GoalOrderVoteColumn;
import org.eclipse.osee.ats.column.GoalsColumn;
import org.eclipse.osee.ats.column.GroupsColumn;
import org.eclipse.osee.ats.column.ImplementorColumn;
import org.eclipse.osee.ats.column.LastStatusedColumn;
import org.eclipse.osee.ats.column.LegacyPcrIdColumn;
import org.eclipse.osee.ats.column.NotesColumn;
import org.eclipse.osee.ats.column.NumberOfTasksColumn;
import org.eclipse.osee.ats.column.NumberOfTasksRemainingColumn;
import org.eclipse.osee.ats.column.NumericColumn;
import org.eclipse.osee.ats.column.OperationalImpactColumn;
import org.eclipse.osee.ats.column.OperationalImpactDesciptionColumn;
import org.eclipse.osee.ats.column.OperationalImpactWorkaroundColumn;
import org.eclipse.osee.ats.column.OperationalImpactWorkaroundDesciptionColumn;
import org.eclipse.osee.ats.column.OriginatingWorkFlowColumn;
import org.eclipse.osee.ats.column.OriginatorColumn;
import org.eclipse.osee.ats.column.ParentIdColumn;
import org.eclipse.osee.ats.column.ParentStateColumn;
import org.eclipse.osee.ats.column.PercentReworkColumn;
import org.eclipse.osee.ats.column.PointsColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.ats.column.RelatedToStateColumn;
import org.eclipse.osee.ats.column.ReleaseDateColumn;
import org.eclipse.osee.ats.column.RemainingHoursColumn;
import org.eclipse.osee.ats.column.ResolutionColumn;
import org.eclipse.osee.ats.column.ReviewAuthorColumn;
import org.eclipse.osee.ats.column.ReviewDeciderColumn;
import org.eclipse.osee.ats.column.ReviewModeratorColumn;
import org.eclipse.osee.ats.column.ReviewNumIssuesColumn;
import org.eclipse.osee.ats.column.ReviewNumMajorDefectsColumn;
import org.eclipse.osee.ats.column.ReviewNumMinorDefectsColumn;
import org.eclipse.osee.ats.column.ReviewReviewerColumn;
import org.eclipse.osee.ats.column.StateColumn;
import org.eclipse.osee.ats.column.TargetedVersionColumn;
import org.eclipse.osee.ats.column.TeamColumn;
import org.eclipse.osee.ats.column.TitleColumn;
import org.eclipse.osee.ats.column.TypeColumn;
import org.eclipse.osee.ats.column.UserCommunityColumn;
import org.eclipse.osee.ats.column.ValidationRequiredColumn;
import org.eclipse.osee.ats.column.WeeklyBenefitHrsColumn;
import org.eclipse.osee.ats.column.WorkDaysNeededColumn;
import org.eclipse.osee.ats.column.WorkPackageColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.GuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.HridColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
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
      HridColumn.getInstance(),
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
      new LastModifiedByColumn(false),
      new LastModifiedDateColumn(false),
      LastStatusedColumn.getInstance(),
      ValidationRequiredColumn.getInstance(),
      ReviewNumMajorDefectsColumn.getInstance(),
      ReviewNumMinorDefectsColumn.getInstance(),
      ReviewNumIssuesColumn.getInstance(),
      ArtifactTypeColumn.getInstance(),
      OriginatingWorkFlowColumn.getInstance(),
      ParentIdColumn.getInstance(),
      DaysInCurrentStateColumn.getInstance(),
      ParentStateColumn.getInstance(),
      PointsColumn.getInstance(),
      NumericColumn.getNumeric1Instance(),
      NumericColumn.getNumeric2Instance(),
      new GuidColumn(false)};
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
