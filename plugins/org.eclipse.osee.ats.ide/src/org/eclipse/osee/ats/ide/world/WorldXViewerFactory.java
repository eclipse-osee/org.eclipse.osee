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
package org.eclipse.osee.ats.ide.world;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnIdValueColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.agile.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.ide.agile.SprintColumn;
import org.eclipse.osee.ats.ide.agile.SprintOrderColumn;
import org.eclipse.osee.ats.ide.column.ActionableItemOwner;
import org.eclipse.osee.ats.ide.column.AnnualCostAvoidanceColumn;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.AtsColumnIdUI;
import org.eclipse.osee.ats.ide.column.BacklogColumnUI;
import org.eclipse.osee.ats.ide.column.BacklogOrderColumn;
import org.eclipse.osee.ats.ide.column.BranchStatusColumn;
import org.eclipse.osee.ats.ide.column.CancelledByColumnUI;
import org.eclipse.osee.ats.ide.column.CancelledDateColumnUI;
import org.eclipse.osee.ats.ide.column.CategoryColumn;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.column.ColorTeamColumnUI;
import org.eclipse.osee.ats.ide.column.CompletedByColumnUI;
import org.eclipse.osee.ats.ide.column.CompletedCancelledByColumnUI;
import org.eclipse.osee.ats.ide.column.CompletedCancelledDateColumnUI;
import org.eclipse.osee.ats.ide.column.CompletedDateColumnUI;
import org.eclipse.osee.ats.ide.column.CountryColumnUI;
import org.eclipse.osee.ats.ide.column.CreatedDateColumnUI;
import org.eclipse.osee.ats.ide.column.DaysInCurrentStateColumn;
import org.eclipse.osee.ats.ide.column.DeadlineColumn;
import org.eclipse.osee.ats.ide.column.DecisionColumn;
import org.eclipse.osee.ats.ide.column.DescriptionColumn;
import org.eclipse.osee.ats.ide.column.EndDateColumn;
import org.eclipse.osee.ats.ide.column.EstimatedCompletionDateColumn;
import org.eclipse.osee.ats.ide.column.EstimatedHoursColumn;
import org.eclipse.osee.ats.ide.column.EstimatedReleaseDateColumn;
import org.eclipse.osee.ats.ide.column.FoundInVersionColumnUI;
import org.eclipse.osee.ats.ide.column.GoalOrderColumn;
import org.eclipse.osee.ats.ide.column.GoalOrderVoteColumn;
import org.eclipse.osee.ats.ide.column.GoalsColumn;
import org.eclipse.osee.ats.ide.column.GroupsColumn;
import org.eclipse.osee.ats.ide.column.HoursSpentSMAStateColumn;
import org.eclipse.osee.ats.ide.column.HoursSpentStateReviewColumn;
import org.eclipse.osee.ats.ide.column.HoursSpentStateTasksColumn;
import org.eclipse.osee.ats.ide.column.HoursSpentStateTotalColumn;
import org.eclipse.osee.ats.ide.column.HoursSpentTotalColumn;
import org.eclipse.osee.ats.ide.column.ImplementorColumnUI;
import org.eclipse.osee.ats.ide.column.LastStatusedColumn;
import org.eclipse.osee.ats.ide.column.LocChangedColumn;
import org.eclipse.osee.ats.ide.column.LocReviewedColumn;
import org.eclipse.osee.ats.ide.column.NumberOfTasksColumn;
import org.eclipse.osee.ats.ide.column.NumberOfTasksRemainingColumn;
import org.eclipse.osee.ats.ide.column.NumericColumn;
import org.eclipse.osee.ats.ide.column.OperationalImpactColumn;
import org.eclipse.osee.ats.ide.column.OperationalImpactDesciptionColumn;
import org.eclipse.osee.ats.ide.column.OperationalImpactWorkaroundColumn;
import org.eclipse.osee.ats.ide.column.OperationalImpactWorkaroundDesciptionColumn;
import org.eclipse.osee.ats.ide.column.OriginatingWorkFlowColumn;
import org.eclipse.osee.ats.ide.column.OriginatorColumn;
import org.eclipse.osee.ats.ide.column.PagesChangedColumn;
import org.eclipse.osee.ats.ide.column.PagesReviewedColumn;
import org.eclipse.osee.ats.ide.column.ParentAtsIdColumn;
import org.eclipse.osee.ats.ide.column.ParentIdColumn;
import org.eclipse.osee.ats.ide.column.ParentStateColumn;
import org.eclipse.osee.ats.ide.column.ParentTopTeamColumnUI;
import org.eclipse.osee.ats.ide.column.ParentWorkDefColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteReviewsColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteSMAStateColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteStateReviewColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteStateTasksColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteTasksColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteTasksReviewsColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.ide.column.PercentReworkColumn;
import org.eclipse.osee.ats.ide.column.PointsColumn;
import org.eclipse.osee.ats.ide.column.PriorityColumnUI;
import org.eclipse.osee.ats.ide.column.ProgramColumnUI;
import org.eclipse.osee.ats.ide.column.RelatedArtifactChangedColumn;
import org.eclipse.osee.ats.ide.column.RelatedArtifactLastModifiedByColumn;
import org.eclipse.osee.ats.ide.column.RelatedArtifactLastModifiedDateColumn;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumn;
import org.eclipse.osee.ats.ide.column.ReleaseDateColumn;
import org.eclipse.osee.ats.ide.column.RemainingHoursColumn;
import org.eclipse.osee.ats.ide.column.RemainingPointsNumericTotalColumn;
import org.eclipse.osee.ats.ide.column.RemainingPointsNumericWorkflowColumn;
import org.eclipse.osee.ats.ide.column.RemainingPointsTotalColumn;
import org.eclipse.osee.ats.ide.column.RemainingPointsWorkflowColumn;
import org.eclipse.osee.ats.ide.column.ResolutionColumn;
import org.eclipse.osee.ats.ide.column.ReviewAuthorColumn;
import org.eclipse.osee.ats.ide.column.ReviewDeciderColumn;
import org.eclipse.osee.ats.ide.column.ReviewFormalTypeColumn;
import org.eclipse.osee.ats.ide.column.ReviewModeratorColumn;
import org.eclipse.osee.ats.ide.column.ReviewNumIssuesColumn;
import org.eclipse.osee.ats.ide.column.ReviewNumMajorDefectsColumn;
import org.eclipse.osee.ats.ide.column.ReviewNumMinorDefectsColumn;
import org.eclipse.osee.ats.ide.column.ReviewReviewerColumn;
import org.eclipse.osee.ats.ide.column.StartDateColumn;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.column.TaskRelatedArtifactTypeColumnUI;
import org.eclipse.osee.ats.ide.column.ValidationRequiredColumn;
import org.eclipse.osee.ats.ide.column.WeeklyBenefitHrsColumn;
import org.eclipse.osee.ats.ide.column.WorkDaysNeededColumn;
import org.eclipse.osee.ats.ide.column.WorkPackageTextColumn;
import org.eclipse.osee.ats.ide.column.WorkingBranchArchivedColumn;
import org.eclipse.osee.ats.ide.column.WorkingBranchIdColumn;
import org.eclipse.osee.ats.ide.column.WorkingBranchStateColumn;
import org.eclipse.osee.ats.ide.column.WorkingBranchTypeColumn;
import org.eclipse.osee.ats.ide.column.ev.WorkPackageColumnUI;
import org.eclipse.osee.ats.ide.column.ev.WorkPackageIdColumnUI;
import org.eclipse.osee.ats.ide.column.ev.WorkPackageNameColumnUI;
import org.eclipse.osee.ats.ide.column.ev.WorkPackageProgramColumnUI;
import org.eclipse.osee.ats.ide.column.ev.WorkPackageTypeColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTokenColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionCommentColumn;

//import org.eclipse.osee.ats.ide.column.ActivityIdColumn;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerFactory extends SkynetXViewerFactory {

   public GoalArtifact soleGoalArtifact;
   public static final String COLUMN_NAMESPACE = "ats.column";
   public final static String NAMESPACE = "org.eclipse.osee.ats.ide.WorldXViewer";

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
         getColumnServiceColumn(AtsColumnToken.WorkDefinitionColumn),
         CreatedDateColumnUI.getInstance(),
         TargetedVersionColumnUI.getInstance(),
         FoundInVersionColumnUI.getInstance(),
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
         getColumnServiceColumn(AtsColumnToken.ParentTitleColumn),
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
