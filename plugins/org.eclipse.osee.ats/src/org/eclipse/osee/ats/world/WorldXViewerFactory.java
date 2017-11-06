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
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.agile.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.agile.SprintColumn;
import org.eclipse.osee.ats.agile.SprintOrderColumn;
import org.eclipse.osee.ats.api.column.AtsColumnIdValueColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.column.ActionableItemOwner;
import org.eclipse.osee.ats.column.AnnualCostAvoidanceColumn;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.column.AtsColumnIdUI;
import org.eclipse.osee.ats.column.BacklogColumnUI;
import org.eclipse.osee.ats.column.BacklogOrderColumn;
import org.eclipse.osee.ats.column.BranchStatusColumn;
import org.eclipse.osee.ats.column.CancelledByColumnUI;
import org.eclipse.osee.ats.column.CancelledDateColumnUI;
import org.eclipse.osee.ats.column.CategoryColumn;
import org.eclipse.osee.ats.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.column.ColorTeamColumnUI;
import org.eclipse.osee.ats.column.CompletedByColumnUI;
import org.eclipse.osee.ats.column.CompletedCancelledByColumnUI;
import org.eclipse.osee.ats.column.CompletedCancelledDateColumnUI;
import org.eclipse.osee.ats.column.CompletedDateColumnUI;
import org.eclipse.osee.ats.column.CountryColumnUI;
import org.eclipse.osee.ats.column.CreatedDateColumnUI;
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
import org.eclipse.osee.ats.column.LocChangedColumn;
import org.eclipse.osee.ats.column.LocReviewedColumn;
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
import org.eclipse.osee.ats.column.PercentCompleteReviewsColumn;
import org.eclipse.osee.ats.column.PercentCompleteSMAStateColumn;
import org.eclipse.osee.ats.column.PercentCompleteStateReviewColumn;
import org.eclipse.osee.ats.column.PercentCompleteStateTasksColumn;
import org.eclipse.osee.ats.column.PercentCompleteTasksColumnUI;
import org.eclipse.osee.ats.column.PercentCompleteTasksReviewsColumn;
import org.eclipse.osee.ats.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.column.PercentReworkColumn;
import org.eclipse.osee.ats.column.PointsColumn;
import org.eclipse.osee.ats.column.PriorityColumnUI;
import org.eclipse.osee.ats.column.ProgramColumnUI;
import org.eclipse.osee.ats.column.RelatedArtifactChangedColumn;
import org.eclipse.osee.ats.column.RelatedArtifactLastModifiedByColumn;
import org.eclipse.osee.ats.column.RelatedArtifactLastModifiedDateColumn;
import org.eclipse.osee.ats.column.RelatedToStateColumn;
import org.eclipse.osee.ats.column.ReleaseDateColumn;
import org.eclipse.osee.ats.column.RemainingHoursColumn;
import org.eclipse.osee.ats.column.RemainingPointsNumericTotalColumn;
import org.eclipse.osee.ats.column.RemainingPointsNumericWorkflowColumn;
import org.eclipse.osee.ats.column.RemainingPointsTotalColumn;
import org.eclipse.osee.ats.column.RemainingPointsWorkflowColumn;
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
import org.eclipse.osee.ats.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.column.TaskRelatedArtifactTypeColumnUI;
import org.eclipse.osee.ats.column.ValidationRequiredColumn;
import org.eclipse.osee.ats.column.WeeklyBenefitHrsColumn;
import org.eclipse.osee.ats.column.WorkDaysNeededColumn;
import org.eclipse.osee.ats.column.WorkPackageTextColumn;
import org.eclipse.osee.ats.column.WorkingBranchArchivedColumn;
import org.eclipse.osee.ats.column.WorkingBranchIdColumn;
import org.eclipse.osee.ats.column.WorkingBranchStateColumn;
import org.eclipse.osee.ats.column.WorkingBranchTypeColumn;
import org.eclipse.osee.ats.column.ev.WorkPackageColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageGuidColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageIdColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageNameColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageProgramColumnUI;
import org.eclipse.osee.ats.column.ev.WorkPackageTypeColumnUI;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTokenColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.GuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionCommentColumn;

//import org.eclipse.osee.ats.column.ActivityIdColumn;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerFactory extends SkynetXViewerFactory {

   public GoalArtifact soleGoalArtifact;
   public static final String COLUMN_NAMESPACE = "ats.column";
   public final static String NAMESPACE = "org.eclipse.osee.ats.WorldXViewer";

   public WorldXViewerFactory() {
      this(null);
   }

   public WorldXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      registerColumns(getWorldViewColumns());
      WorldXViewerUtil.registerOtherColumns(this);
   }

   public static final XViewerColumn[] getWorldViewColumns() {
      return new XViewerColumn[] {
         getColumnServiceColumn(AtsColumnToken.TypeColumn),
         getColumnServiceColumn(AtsColumnToken.StateColumn),
         PriorityColumnUI.getInstance(),
         ChangeTypeColumnUI.getInstance(),
         AssigneeColumnUI.getInstance(),
         getAttriubuteConfigColumn(AtsColumnToken.TitleColumn),
         getColumnServiceColumn(AtsColumnToken.ActionableItemsColumn),
         getColumnServiceColumn(AtsColumnToken.AtsIdColumnShow),
         CreatedDateColumnUI.getInstance(),
         TargetedVersionColumnUI.getInstance(),
         getColumnServiceColumn(AtsColumnToken.TeamColumn),
         getAttriubuteConfigColumn(AtsColumnToken.NotesColumn),
         DeadlineColumn.getInstance(),
         AnnualCostAvoidanceColumn.getInstance(),
         DescriptionColumn.getInstance(),
         getAttriubuteConfigColumn(AtsColumnToken.LegacyPcrIdColumn),
         DecisionColumn.getInstance(),
         ResolutionColumn.getInstance(),
         GroupsColumn.getInstance(),
         GoalsColumn.getInstance(),
         BacklogColumnUI.getInstance(),
         SprintColumn.getInstance(),
         EstimatedReleaseDateColumn.getInstance(),
         EstimatedCompletionDateColumn.getInstance(),
         ReleaseDateColumn.getInstance(),
         WorkPackageTextColumn.getInstance(),
         WorkingBranchIdColumn.getInstance(),
         WorkingBranchArchivedColumn.getInstance(),
         WorkingBranchStateColumn.getInstance(),
         WorkingBranchTypeColumn.getInstance(),
         WorkPackageColumnUI.getInstance(),
         WorkPackageIdColumnUI.getInstance(),
         WorkPackageNameColumnUI.getInstance(),
         WorkPackageTypeColumnUI.getInstance(),
         WorkPackageProgramColumnUI.getInstance(),
         WorkPackageGuidColumnUI.getInstance(),
         CategoryColumn.getCategory1Instance(),
         CategoryColumn.getCategory2Instance(),
         CategoryColumn.getCategory3Instance(),
         GoalOrderColumn.getInstance(),
         BacklogOrderColumn.getInstance(),
         GoalOrderVoteColumn.getInstance(),
         RelatedToStateColumn.getInstance(),
         EstimatedHoursColumn.getInstance(),
         WeeklyBenefitHrsColumn.getInstance(),
         RemainingHoursColumn.getInstance(),
         PercentCompleteSMAStateColumn.getInstance(),
         PercentCompleteStateTasksColumn.getInstance(),
         PercentCompleteStateReviewColumn.getInstance(),
         PercentCompleteTotalColumn.getInstance(),
         getAttriubuteConfigColumn(AtsColumnToken.PercentCompleteWorkflowColumn),
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
         CompletedDateColumnUI.getInstance(),
         CompletedByColumnUI.getInstance(),
         CancelledDateColumnUI.getInstance(),
         CancelledByColumnUI.getInstance(),
         CompletedCancelledByColumnUI.getInstance(),
         CompletedCancelledDateColumnUI.getInstance(),
         WorkDaysNeededColumn.getInstance(),
         PercentReworkColumn.getInstance(),
         BranchStatusColumn.getInstance(),
         NumberOfTasksColumn.getInstance(),
         NumberOfTasksRemainingColumn.getInstance(),
         new LastModifiedByColumn(false),
         new LastModifiedDateColumn(false),
         new LastModifiedTransactionColumn(false),
         new LastModifiedTransactionCommentColumn(false),
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
         getColumnServiceColumn(AtsColumnToken.AtsIdColumn),
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
         getColumnServiceColumn(AtsColumnToken.InsertionColumn),
         getColumnServiceColumn(AtsColumnToken.InsertionActivityColumn),
         ColorTeamColumnUI.getInstance(),
         RelatedArtifactChangedColumn.getInstance(),
         RelatedArtifactLastModifiedByColumn.getInstance(),
         RelatedArtifactLastModifiedDateColumn.getInstance(),
         TaskRelatedArtifactTypeColumnUI.getInstance(),
         new IdColumn(false),};
   }

   /**
    * Provides XViewerColumn for non-attribute based columns like Type and State
    */
   public static XViewerColumn getColumnServiceColumn(AtsColumnIdValueColumn columnToken) {
      return new AtsColumnIdUI(columnToken, AtsClientService.get().getServices());
   }

   /**
    * Provides XViewerColumn for attribute based columns like Legacy PCR Id and Change Type. These columns can be
    * overridden (or defined) by entries in the AtsConfig views.attrColumns entry.
    */
   private static XViewerColumn getAttriubuteConfigColumn(AtsAttributeValueColumn attrValueColumn) {
      XViewerColumn result = null;
      for (AtsAttributeValueColumn column : AtsClientService.get().getConfigService().getConfigurations().getViews().getAttrColumns()) {
         if (column.getNamespace().equals(NAMESPACE) && column.getId().equals(attrValueColumn.getId())) {
            result = new XViewerAtsAttributeValueColumn(column);
            break;
         }
      }
      if (result == null) {
         result = new XViewerAtsAttributeValueColumn(attrValueColumn);
      }
      return result;
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

}
