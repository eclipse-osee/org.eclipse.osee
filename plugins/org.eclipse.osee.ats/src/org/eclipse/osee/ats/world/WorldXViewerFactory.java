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
import org.eclipse.osee.ats.agile.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.agile.SprintColumn;
import org.eclipse.osee.ats.agile.SprintOrderColumn;
import org.eclipse.osee.ats.column.*;
import org.eclipse.osee.ats.column.ev.ActivityIdColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageGuidColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageIdColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageNameColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageProgramColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageTypeColumnUI;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTokenColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.GuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.UuidColumn;

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
      BacklogColumnUI.getInstance(),
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
      LastModifiedCommentColumn.getInstance(),
      LastStatusedColumn.getInstance(),
      ValidationRequiredColumn.getInstance(),
      ReviewNumMajorDefectsColumn.getInstance(),
      ReviewNumMinorDefectsColumn.getInstance(),
      ReviewNumIssuesColumn.getInstance(),
      ArtifactTypeColumn.getInstance(),
      ArtifactTokenColumn.getInstance(),
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
      AtsIdColumn.getInstance(),
      AgileFeatureGroupColumn.getInstance(),
      SprintOrderColumn.getInstance(),
      RemainingPointsNumericWorkflowColumn.getInstance(),
      RemainingPointsNumericTotalColumn.getInstance(),
      RemainingPointsWorkflowColumn.getInstance(),
      RemainingPointsTotalColumn.getInstance(),
      PercentCompleteReviewsColumn.getInstance(),
      PercentCompleteTasksColumn.getInstance(),
      PercentCompleteTasksReviewsColumn.getInstance(),
      CountryColumnUI.getInstance(),
      ProgramColumnUI.getInstance(),
      InsertionColumnUI.getInstance(),
      InsertionActivityColumnUI.getInstance(),
      ColorTeamColumnUI.getInstance(),
      RelatedArtifactChangedColumn.getInstance(),
      RelatedArtifactLastModifiedByColumn.getInstance(),
      RelatedArtifactLastModifiedDateColumn.getInstance(),
      new UuidColumn(false)};

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
