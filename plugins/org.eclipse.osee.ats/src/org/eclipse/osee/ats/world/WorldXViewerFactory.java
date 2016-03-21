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
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.column.*;
import org.eclipse.osee.ats.column.ev.ActivityIdColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageGuidColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageIdColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageNameColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageProgramColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageTypeColumnUI;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
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
   private static AtsConfigurations atsConfigurations;

   public static final XViewerColumn[] getWorldViewColumns() {
      return new XViewerColumn[] {
         TypeColumn.getInstance(),
         StateColumnUI.getInstance(),
         PriorityColumn.getInstance(),
         ChangeTypeColumn.getInstance(),
         AssigneeColumnUI.getInstance(),
         getConfigColumn(AtsColumnToken.TitleColumn),
         ActionableItemsColumnUI.getInstance(),
         AtsIdColumnUI.getInstance(),
         CreatedDateColumn.getInstance(),
         TargetedVersionColumn.getInstance(),
         TeamColumn.getInstance(),
         NotesColumn.getInstance(),
         DeadlineColumn.getInstance(),
         AnnualCostAvoidanceColumn.getInstance(),
         DescriptionColumn.getInstance(),
         getConfigColumn(AtsColumnToken.LegacyPcrIdColumn),
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
         getConfigColumn(AtsColumnToken.PercentCompleteWorkflowColumn),
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
         AtsIdColumnUI.getInstance(),
         AgileFeatureGroupColumn.getInstance(),
         SprintOrderColumn.getInstance(),
         RemainingPointsNumericWorkflowColumn.getInstance(),
         RemainingPointsNumericTotalColumn.getInstance(),
         RemainingPointsWorkflowColumn.getInstance(),
         RemainingPointsTotalColumn.getInstance(),
         PercentCompleteReviewsColumn.getInstance(),
         PercentCompleteTasksColumnUI.getInstance(),
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
   }

   private static XViewerColumn getConfigColumn(AtsAttributeValueColumn titleColumn) {
      XViewerColumn result = null;
      if (atsConfigurations == null) {
         atsConfigurations = AtsClientService.getConfigEndpoint().get();
      }
      for (AtsAttributeValueColumn column : atsConfigurations.getViews().getAttrColumns()) {
         if (column.getNamespace().equals(NAMESPACE) && column.getId().equals(titleColumn.getId())) {
            result = new XViewerAtsAttributeValueColumn(column);
            break;
         }
      }
      if (result == null) {
         result = new XViewerAtsAttributeValueColumn(titleColumn);
      }
      return result;
   }

   public WorldXViewerFactory() {
      super(NAMESPACE);
      registerColumns(getWorldViewColumns());
      WorldXViewerUtil.registerOtherColumns(this);
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

}
