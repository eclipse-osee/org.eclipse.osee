/*********************************************************************
 * Copyright (c) 2004, 2007, 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.world;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.column.AtsValColumn;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.ide.agile.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.ide.agile.SprintColumn;
import org.eclipse.osee.ats.ide.agile.SprintOrderColumn;
import org.eclipse.osee.ats.ide.column.ActionableItemOwner;
import org.eclipse.osee.ats.ide.column.AnnualCostAvoidanceColumn;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.AtsColumnIdUi;
import org.eclipse.osee.ats.ide.column.BacklogColumnUI;
import org.eclipse.osee.ats.ide.column.BacklogOrderColumn;
import org.eclipse.osee.ats.ide.column.BranchStatusColumn;
import org.eclipse.osee.ats.ide.column.CancelReasonColumnUI;
import org.eclipse.osee.ats.ide.column.CancelledByColumnUI;
import org.eclipse.osee.ats.ide.column.CancelledDateColumnUI;
import org.eclipse.osee.ats.ide.column.CancelledReasonColumnUI;
import org.eclipse.osee.ats.ide.column.CancelledReasonDetailsColumnUI;
import org.eclipse.osee.ats.ide.column.CategoryColumn;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
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
import org.eclipse.osee.ats.ide.column.PercentCompleteStateReviewColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteStateTasksColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteTasksColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteTasksReviewsColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.ide.column.PercentReworkColumn;
import org.eclipse.osee.ats.ide.column.PointsColumn;
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
import org.eclipse.osee.ats.ide.column.SiblingAtsIdColumn;
import org.eclipse.osee.ats.ide.column.SiblingTeamDefColumn;
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
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.priority.PriorityColumnUI;
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

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerFactory extends SkynetXViewerFactory {

   public GoalArtifact soleGoalArtifact;
   public static final String COLUMN_NAMESPACE = "ats.column";
   public final static String NAMESPACE = "WorldXViewer";

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

         /**
          * Default show columns in default order; don't change this order
          */
         getColumnServiceColumn(AtsColumnTokens.TypeColumn),
         getColumnServiceColumn(AtsColumnTokens.StateColumn),
         PriorityColumnUI.getInstance(),
         ChangeTypeColumnUI.getInstance(),
         AssigneeColumnUI.getInstance(),
         getAttributeConfigColumn(AtsColumnTokens.TitleColumn),
         getColumnServiceColumn(AtsColumnTokens.ActionableItemsColumn),
         getColumnServiceColumn(AtsColumnTokens.AtsIdColumnShow),
         getColumnServiceColumn(AtsColumnTokens.WorkDefinitionColumn),
         CreatedDateColumnUI.getInstance(),
         TargetedVersionColumnUI.getInstance(),
         FoundInVersionColumnUI.getInstance(),
         getColumnServiceColumn(AtsColumnTokens.TeamColumn),
         getAttributeConfigColumn(AtsColumnTokens.NotesColumn),

         /**
          * The rest are non-show columns by default; Order doesn't matter
          */

         /**
          * These are default attribute columns; USE THIS METHOD IF POSSIBLEi; Normally this is if data can just be
          * shown straight from attr
          */
         getAttributeConfigColumn(AtsColumnTokens.LegacyPcrIdColumn),
         getAttributeConfigColumn(AtsColumnTokens.PercentCompleteWorkflowColumn),
         getAttributeConfigColumn(AtsColumnTokens.CrashOrBlankDisplayColumn),
         getAttributeConfigColumn(AtsColumnTokens.ExternalReferenceColumn),
         getAttributeConfigColumn(AtsColumnTokens.GitChangeId),
         getAttributeConfigColumn(AtsColumnTokens.RevisitDateColumn),
         getAttributeConfigColumn(AtsColumnTokens.NonFunctionalProblem),
         getAttributeConfigColumn(AtsColumnTokens.QuantityUnderReviewColumn),
         getAttributeConfigColumn(AtsColumnTokens.HowToReproduceProblemColumn),
         getAttributeConfigColumn(AtsColumnTokens.ProblemFirstObservedColumn),
         getAttributeConfigColumn(AtsColumnTokens.RiskAnalysisColumn),
         getAttributeConfigColumn(AtsColumnTokens.RootCauseColumn),
         getAttributeConfigColumn(AtsColumnTokens.ProposedResolutionColumn),
         getAttributeConfigColumn(AtsColumnTokens.ImpactToMissionOrCrewColumn),
         getAttributeConfigColumn(AtsColumnTokens.WorkaroundColumn),

         /**
          * These are computed columns where data is in multiple places and must be retrieved/loaded to be displayed
          */
         getColumnServiceColumn(AtsColumnTokens.AtsIdColumn),
         getColumnServiceColumn(AtsColumnTokens.InsertionColumn),
         getColumnServiceColumn(AtsColumnTokens.InsertionActivityColumn),
         getColumnServiceColumn(AtsColumnTokens.ParentTitleColumn),
         getColumnServiceColumn(AtsColumnTokens.FeatureImpactReferenceColumn),
         getColumnServiceColumn(AtsColumnTokens.IncorporatedInColumn),
         getColumnServiceColumn(AtsColumnTokens.TaskPointsColumn),
         getColumnServiceColumn(AtsColumnTokens.TaskRiskFactorsColumn),
         getColumnServiceColumn(AtsColumnTokens.DerivedFromAtsIdColumn),
         getColumnServiceColumn(AtsColumnTokens.DerivedFromTeamDefColumn),

         /**
          * This is the legacy way of providing columns. DO NOT USE THIS METHOD. These should eventually be converted to
          * one of the above
          */
         DeadlineColumn.getInstance(),
         AnnualCostAvoidanceColumn.getInstance(),
         DescriptionColumn.getInstance(),
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
         PercentCompleteStateTasksColumn.getInstance(),
         PercentCompleteStateReviewColumn.getInstance(),
         PercentCompleteTotalColumn.getInstance(),
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
         CancelReasonColumnUI.getInstance(),
         CancelledReasonColumnUI.getInstance(),
         CancelledReasonDetailsColumnUI.getInstance(),
         CancelledDateColumnUI.getInstance(),
         CancelledByColumnUI.getInstance(),
         CompletedCancelledByColumnUI.getInstance(),
         CompletedCancelledDateColumnUI.getInstance(),
         WorkDaysNeededColumn.getInstance(),
         PercentReworkColumn.getInstance(),
         BranchStatusColumn.getInstance(),
         SiblingAtsIdColumn.getInstance(),
         SiblingTeamDefColumn.getInstance(),
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
         RelatedArtifactChangedColumn.getInstance(),
         RelatedArtifactLastModifiedByColumn.getInstance(),
         RelatedArtifactLastModifiedDateColumn.getInstance(),
         TaskRelatedArtifactTypeColumnUI.getInstance(),
         new IdColumn(false),};
   }

   /**
    * Provides XViewerColumn for non-attribute based columns like Type and State
    */
   public static XViewerColumn getColumnServiceColumn(AtsValColumn columnToken) {
      return new AtsColumnIdUi(columnToken, AtsApiService.get());
   }

   /**
    * Provides XViewerColumn for attribute based columns like Legacy PCR Id and Change Type. These columns can be
    * overridden (or defined) by entries in the AtsConfig views.attrColumns entry.
    */
   public static XViewerColumn getAttributeConfigColumn(AtsAttrValCol attrValueColumn) {
      XViewerColumn result = null;
      for (AtsAttrValCol column : AtsApiService.get().getConfigService().getConfigurations().getViews().getAttrColumns()) {
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
