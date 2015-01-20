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

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;

import org.eclipse.osee.ats.agile.SprintColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.column.ActionableItemOwner;
import org.eclipse.osee.ats.column.ActionableItemsColumnUI;
import org.eclipse.osee.ats.column.AnnualCostAvoidanceColumn;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.column.AtsIdColumn;
import org.eclipse.osee.ats.column.BranchStatusColumn;
import org.eclipse.osee.ats.column.CancelledByColumn;
import org.eclipse.osee.ats.column.CancelledDateColumn;
import org.eclipse.osee.ats.column.CategoryColumn;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.CompletedByColumn;
import org.eclipse.osee.ats.column.CompletedCancelledByColumn;
import org.eclipse.osee.ats.column.CompletedCancelledDateColumn;
import org.eclipse.osee.ats.column.CompletedDateColumn;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.column.DaysInCurrentStateColumn;
import org.eclipse.osee.ats.column.DeadlineColumn;
import org.eclipse.osee.ats.column.DecisionColumn;
import org.eclipse.osee.ats.column.DescriptionColumn;
import org.eclipse.osee.ats.column.EndDateColumn;
import org.eclipse.osee.ats.column.EstimatedCompletionDateColumn;
import org.eclipse.osee.ats.column.EstimatedHoursColumn;
import org.eclipse.osee.ats.column.EstimatedReleaseDateColumn;
import org.eclipse.osee.ats.column.GoalOrderColumn;
import org.eclipse.osee.ats.column.GoalOrderVoteColumn;
import org.eclipse.osee.ats.column.GoalsColumn;
import org.eclipse.osee.ats.column.GroupsColumn;
import org.eclipse.osee.ats.column.HoursSpentSMAStateColumn;
import org.eclipse.osee.ats.column.HoursSpentStateReviewColumn;
import org.eclipse.osee.ats.column.HoursSpentStateTasksColumn;
import org.eclipse.osee.ats.column.HoursSpentStateTotalColumn;
import org.eclipse.osee.ats.column.HoursSpentTotalColumn;
import org.eclipse.osee.ats.column.ImplementorColumnUI;
import org.eclipse.osee.ats.column.LastStatusedColumn;
import org.eclipse.osee.ats.column.LegacyPcrIdColumn;
import org.eclipse.osee.ats.column.LocChangedColumn;
import org.eclipse.osee.ats.column.LocReviewedColumn;
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
import org.eclipse.osee.ats.column.PagesChangedColumn;
import org.eclipse.osee.ats.column.PagesReviewedColumn;
import org.eclipse.osee.ats.column.ParentAtsIdColumn;
import org.eclipse.osee.ats.column.ParentIdColumn;
import org.eclipse.osee.ats.column.ParentStateColumn;
import org.eclipse.osee.ats.column.ParentTopTeamColumnUI;
import org.eclipse.osee.ats.column.ParentWorkDefColumn;
import org.eclipse.osee.ats.column.PercentCompleteSMAStateColumn;
import org.eclipse.osee.ats.column.PercentCompleteStateReviewColumn;
import org.eclipse.osee.ats.column.PercentCompleteStateTasksColumn;
import org.eclipse.osee.ats.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.column.PercentCompleteWorkflowColumn;
import org.eclipse.osee.ats.column.PercentReworkColumn;
import org.eclipse.osee.ats.column.PointsColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.ats.column.RelatedToStateColumn;
import org.eclipse.osee.ats.column.ReleaseDateColumn;
import org.eclipse.osee.ats.column.RemainingHoursColumn;
import org.eclipse.osee.ats.column.ResolutionColumn;
import org.eclipse.osee.ats.column.ReviewAuthorColumn;
import org.eclipse.osee.ats.column.ReviewDeciderColumn;
import org.eclipse.osee.ats.column.ReviewFormalTypeColumn;
import org.eclipse.osee.ats.column.ReviewModeratorColumn;
import org.eclipse.osee.ats.column.ReviewNumIssuesColumn;
import org.eclipse.osee.ats.column.ReviewNumMajorDefectsColumn;
import org.eclipse.osee.ats.column.ReviewNumMinorDefectsColumn;
import org.eclipse.osee.ats.column.ReviewReviewerColumn;
import org.eclipse.osee.ats.column.StartDateColumn;
import org.eclipse.osee.ats.column.StateColumn;
import org.eclipse.osee.ats.column.TargetedVersionColumn;
import org.eclipse.osee.ats.column.TeamColumn;
import org.eclipse.osee.ats.column.TitleColumn;
import org.eclipse.osee.ats.column.TypeColumn;
import org.eclipse.osee.ats.column.ValidationRequiredColumn;
import org.eclipse.osee.ats.column.WeeklyBenefitHrsColumn;
import org.eclipse.osee.ats.column.WorkDaysNeededColumn;
import org.eclipse.osee.ats.column.WorkPackageColumn;
import org.eclipse.osee.ats.column.WorkingBranchArchivedColumn;
import org.eclipse.osee.ats.column.WorkingBranchStateColumn;
import org.eclipse.osee.ats.column.WorkingBranchTypeColumn;
import org.eclipse.osee.ats.column.WorkingBranchUuidColumn;
import org.eclipse.osee.ats.column.ev.ActivityIdColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageGuidColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageIdColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageNameColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageProgramColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageTypeColumnUI;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.GuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;

//import org.eclipse.osee.ats.column.ActivityIdColumn;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerFactory extends SkynetXViewerFactory {

   public GoalArtifact soleGoalArtifact;
   public static final String COLUMN_NAMESPACE = "ats.column";
   public final static String NAMESPACE = "org.eclipse.osee.ats.WorldXViewer";

   public static final XViewerColumn[] WorldViewColumns = new XViewerColumn[] {
      TypeColumn.getInstance(),
      StateColumn.getInstance(),
      PriorityColumn.getInstance(),
      ChangeTypeColumn.getInstance(),
      AssigneeColumnUI.getInstance(),
      TitleColumn.getInstance(),
      ActionableItemsColumnUI.getInstance(),
      AtsIdColumn.getInstance(),
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
      SprintColumn.getInstance(),
      EstimatedReleaseDateColumn.getInstance(),
      EstimatedCompletionDateColumn.getInstance(),
      ReleaseDateColumn.getInstance(),
      WorkPackageColumn.getInstance(),
      WorkingBranchUuidColumn.getInstance(),
      WorkingBranchArchivedColumn.getInstance(),
      WorkingBranchStateColumn.getInstance(),
      WorkingBranchTypeColumn.getInstance(),
      ActivityIdColumnUI.getInstance(),
      WorkPackageIdColumnUI.getInstance(),
      WorkPackageNameColumnUI.getInstance(),
      WorkPackageTypeColumnUI.getInstance(),
      WorkPackageProgramColumnUI.getInstance(),
      WorkPackageGuidColumnUI.getInstance(),
      CategoryColumn.getCategory1Instance(),
      CategoryColumn.getCategory2Instance(),
      CategoryColumn.getCategory3Instance(),
      GoalOrderColumn.getInstance(),
      GoalOrderVoteColumn.getInstance(),
      RelatedToStateColumn.getInstance(),
      EstimatedHoursColumn.getInstance(),
      WeeklyBenefitHrsColumn.getInstance(),
      RemainingHoursColumn.getInstance(),
      PercentCompleteSMAStateColumn.getInstance(),
      PercentCompleteStateTasksColumn.getInstance(),
      PercentCompleteStateReviewColumn.getInstance(),
      PercentCompleteTotalColumn.getInstance(),
      PercentCompleteWorkflowColumn.getInstance(),
      HoursSpentSMAStateColumn.getInstance(),
      HoursSpentStateTasksColumn.getInstance(),
      HoursSpentStateReviewColumn.getInstance(),
      HoursSpentStateTotalColumn.getInstance(),
      HoursSpentTotalColumn.getInstance(),
      OriginatorColumn.getInstance(),
      OperationalImpactColumn.getInstance(),
      OperationalImpactDesciptionColumn.getInstance(),
      OperationalImpactWorkaroundColumn.getInstance(),
      OperationalImpactWorkaroundDesciptionColumn.getInstance(),
      ImplementorColumnUI.getInstance(),
      ReviewFormalTypeColumn.getInstance(),
      ReviewAuthorColumn.getInstance(),
      ReviewModeratorColumn.getInstance(),
      ReviewReviewerColumn.getInstance(),
      ReviewDeciderColumn.getInstance(),
      StartDateColumn.getInstance(),
      EndDateColumn.getInstance(),
      CompletedDateColumn.getInstance(),
      CompletedByColumn.getInstance(),
      CancelledDateColumn.getInstance(),
      CancelledByColumn.getInstance(),
      CompletedCancelledByColumn.getInstance(),
      CompletedCancelledDateColumn.getInstance(),
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
      ParentAtsIdColumn.getInstance(),
      DaysInCurrentStateColumn.getInstance(),
      ParentStateColumn.getInstance(),
      ParentWorkDefColumn.getInstance(),
      PointsColumn.getInstance(),
      NumericColumn.getNumeric1Instance(),
      NumericColumn.getNumeric2Instance(),
      LocChangedColumn.getInstance(),
      LocReviewedColumn.getInstance(),
      PagesChangedColumn.getInstance(),
      PagesReviewedColumn.getInstance(),
      new GuidColumn(false),
      ParentTopTeamColumnUI.getInstance(),
      ActionableItemOwner.getInstance(),
      AtsIdColumn.getInstance()};

   public WorldXViewerFactory() {
      super(NAMESPACE);
      registerColumns(WorldViewColumns);
      WorldXViewerUtil.registerOtherColumns(this);
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

}
