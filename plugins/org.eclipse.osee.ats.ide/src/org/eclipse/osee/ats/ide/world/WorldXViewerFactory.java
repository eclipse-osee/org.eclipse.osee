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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.column.AtsColumnUtil;
import org.eclipse.osee.ats.api.column.AtsCoreColumn;
import org.eclipse.osee.ats.api.column.AtsCoreColumnToken;
import org.eclipse.osee.ats.core.column.model.AtsCoreAttrTokenColumn;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.ats.ide.agile.AgileFeatureGroupColumnUI;
import org.eclipse.osee.ats.ide.agile.SprintColumnUI;
import org.eclipse.osee.ats.ide.agile.SprintOrderColumnUI;
import org.eclipse.osee.ats.ide.column.ActionableItemOwnerUI;
import org.eclipse.osee.ats.ide.column.AnnualCostAvoidanceColumnUI;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.BacklogColumnUI;
import org.eclipse.osee.ats.ide.column.BacklogOrderColumnUI;
import org.eclipse.osee.ats.ide.column.BranchStatusColumnUI;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.column.CompletedCancelledByColumnUI;
import org.eclipse.osee.ats.ide.column.CompletedCancelledDateColumnUI;
import org.eclipse.osee.ats.ide.column.CountryColumnUI;
import org.eclipse.osee.ats.ide.column.DaysInCurrentStateColumnUI;
import org.eclipse.osee.ats.ide.column.DeadlineColumnUI;
import org.eclipse.osee.ats.ide.column.GoalOrderColumnUI;
import org.eclipse.osee.ats.ide.column.GoalOrderVoteColumnUI;
import org.eclipse.osee.ats.ide.column.GoalsColumnUI;
import org.eclipse.osee.ats.ide.column.GroupsColumnUI;
import org.eclipse.osee.ats.ide.column.ImplementorColumnUI;
import org.eclipse.osee.ats.ide.column.LastStatusedColumnUI;
import org.eclipse.osee.ats.ide.column.NumberOfTasksColumnUI;
import org.eclipse.osee.ats.ide.column.NumberOfTasksRemainingColumnUI;
import org.eclipse.osee.ats.ide.column.OriginatingWorkFlowColumnUI;
import org.eclipse.osee.ats.ide.column.OriginatorColumnUI;
import org.eclipse.osee.ats.ide.column.ParentAtsIdColumnUI;
import org.eclipse.osee.ats.ide.column.ParentIdColumnUI;
import org.eclipse.osee.ats.ide.column.ParentStateColumnUI;
import org.eclipse.osee.ats.ide.column.ParentTopTeamColumnUI;
import org.eclipse.osee.ats.ide.column.ParentWorkDefColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteReviewsColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteStateReviewColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteStateTasksColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteTasksColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteTasksReviewsColumnUI;
import org.eclipse.osee.ats.ide.column.PercentCompleteTotalColumnUI;
import org.eclipse.osee.ats.ide.column.PointsColumnUI;
import org.eclipse.osee.ats.ide.column.ProgramColumnUI;
import org.eclipse.osee.ats.ide.column.RelatedArtifactChangedColumnUI;
import org.eclipse.osee.ats.ide.column.RelatedArtifactLastModifiedByColumnUI;
import org.eclipse.osee.ats.ide.column.RelatedArtifactLastModifiedDateColumnUI;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumnUI;
import org.eclipse.osee.ats.ide.column.RemainingHoursColumnUI;
import org.eclipse.osee.ats.ide.column.RemainingPointsNumericTotalColumnUI;
import org.eclipse.osee.ats.ide.column.RemainingPointsNumericWorkflowColumnUI;
import org.eclipse.osee.ats.ide.column.RemainingPointsTotalColumnUI;
import org.eclipse.osee.ats.ide.column.RemainingPointsWorkflowColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewAuthorColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewDeciderColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewFormalTypeColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewModeratorColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewNumIssuesColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewNumMajorDefectsColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewNumMinorDefectsColumnUI;
import org.eclipse.osee.ats.ide.column.ReviewReviewerColumnUI;
import org.eclipse.osee.ats.ide.column.SiblingAtsIdColumnUI;
import org.eclipse.osee.ats.ide.column.SiblingTeamDefColumnUI;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.column.TaskRelatedArtifactTypeColumnUI;
import org.eclipse.osee.ats.ide.column.TitleColumnUI;
import org.eclipse.osee.ats.ide.column.WorkDaysNeededColumnUI;
import org.eclipse.osee.ats.ide.column.WorkPackageTextColumnUI;
import org.eclipse.osee.ats.ide.column.WorkingBranchArchivedColumnUI;
import org.eclipse.osee.ats.ide.column.WorkingBranchIdColumnUI;
import org.eclipse.osee.ats.ide.column.WorkingBranchStateColumnUI;
import org.eclipse.osee.ats.ide.column.WorkingBranchTypeColumnUI;
import org.eclipse.osee.ats.ide.column.signby.ApproveRequestedByColumnUI;
import org.eclipse.osee.ats.ide.column.signby.ApproveRequestedByDateColumnUI;
import org.eclipse.osee.ats.ide.column.signby.ReviewedByColumnUI;
import org.eclipse.osee.ats.ide.column.signby.ReviewedByDateColumnUI;
import org.eclipse.osee.ats.ide.column.signby.SignedByColumnUI;
import org.eclipse.osee.ats.ide.column.signby.SignedByDateColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttrTokenXColumn;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.priority.PriorityColumnUI;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTokenColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;
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
   public static final String COLUMN_NAMESPACE = AtsColumnUtil.COLUMN_NAMESPACE;
   public final static String NAMESPACE = "WorldXViewer";
   protected Map<String, XViewerColumn> loadedColIds;
   protected AtsApi atsApi;

   public WorldXViewerFactory(String namespace, IOseeTreeReportProvider reportProvider) {
      super(namespace, reportProvider);
      atsApi = AtsApiService.get();
      registerColumnsAndSetDefaults();
   }

   public WorldXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      atsApi = AtsApiService.get();
      registerColumnsAndSetDefaults();
   }

   // Return default visible columns in default order.  Override to change defaults.
   public List<AtsCoreColumnToken> getDefaultVisibleColumns() {
      return Arrays.asList( //
         AtsColumnTokensDefault.TypeColumn, //
         AtsColumnTokensDefault.StateColumn, //
         AtsColumnTokensDefault.PriorityColumn, //
         AtsColumnTokensDefault.ChangeTypeColumn, //
         AtsColumnTokensDefault.AssigneeColumn, //
         AtsColumnTokensDefault.TitleColumn, //
         AtsColumnTokensDefault.ActionableItemsColumn, //
         AtsColumnTokensDefault.AtsIdColumn, //
         AtsColumnTokensDefault.CreatedDateColumn, //
         AtsColumnTokensDefault.TargetedVersionColumn, //
         AtsColumnTokensDefault.TeamColumn, //
         AtsColumnTokensDefault.NotesColumn //
      );
   }

   // Return default visible column widths.  Empty list or missing will use default token width.
   public List<Integer> getDefaultColumnWidths() {
      return Arrays.asList(150, 75, 20, 40, 100, 150, 80, 75, 80, 40, 50, 80);
   }

   protected List<XViewerColumn> getColumns(List<AtsCoreColumnToken> defaultVisibleColumns) {
      List<XViewerColumn> cols = new ArrayList<>();
      for (AtsCoreColumnToken colTok : defaultVisibleColumns) {
         XViewerColumn xCol = loadedColIds.get(colTok.getId());
         if (xCol == null) {
            OseeLog.log(WorldXViewerFactory.class, Level.SEVERE, "No registered column for " + colTok.getId());
         } else {
            cols.add(xCol);
         }
      }
      return cols;
   }

   private synchronized void loadColumns() {
      if (loadedColIds == null) {
         loadedColIds = new HashMap<>();

         // Load ATS IDE columns first as these should override core columns and have IDE/UI implications
         for (XViewerColumn xCol : getWorldViewIdeXColumns()) {
            XViewerColumn xColCopy = xCol.copy();
            loadedColIds.put(xCol.getId(), xColCopy);
         }

         XResultData loadResults = atsApi.getColumnService().getLoadResults();
         if (loadResults.isErrors()) {
            XResultDataUI.report(loadResults, getClass().getSimpleName() + " Load Columns");
         }

         // Register any other IDE columns from other plugins
         registerPluginColumns();

         // Create remainder IDE columns: core columns, provider columns, AtsConfig.views columns
         for (AtsCoreColumn col : atsApi.getColumnService().getColumns()) {
            // Skip any already loaded IDE columns; Match any attrType columns regardless of columnId
            if (!loadedColIds.containsKey(col.getId()) && !isCoreColumnTypeMatch(col)) {
               XViewerColumn xCol = createXColumn(col);
               if (xCol != null) {
                  loadedColIds.put(xCol.getId(), xCol);
               }
            }
         }
      }
   }

   // Register any columns from other plugins
   public void registerPluginColumns() {
      try {
         List<IAtsWorldEditorItem> worldItems = AtsWorldEditorItems.getItems();
         for (IAtsWorldEditorItem item : worldItems) {
            Collection<IUserGroupArtifactToken> itemGroups = item.getUserGroups();
            if (AtsApiService.get().userService().isInUserGroup(
               itemGroups.toArray(new IUserGroupArtifactToken[itemGroups.size()]))) {
               for (XViewerColumn xCol : item.getXViewerColumns()) {
                  XViewerColumn xColCopy = xCol.copy();
                  loadedColIds.put(xCol.getId(), xColCopy);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private boolean isCoreColumnTypeMatch(AtsCoreColumn col) {
      if (!(col instanceof AtsCoreAttrTokenColumn)) {
         return false;
      }
      for (XViewerColumn xCol : loadedColIds.values()) {
         if (xCol instanceof IAttributeColumn) {
            AttributeTypeToken attributeType = ((IAttributeColumn) xCol).getAttributeType();
            if (attributeType.getId().equals(((AtsCoreAttrTokenColumn) col).getColumnToken().getAttrTypeId())) {
               return true;
            }
         }
      }
      return false;
   }

   private void registerColumnsAndSetDefaults() {
      loadColumns();

      List<String> registeredColIds = new ArrayList<>();

      XResultData rd = atsApi.getColumnService().validateIdeColumns(getWorldViewIdeXColumns());
      if (rd.isErrors()) {
         OseeLog.log(WorldXViewerFactory.class, Level.SEVERE,
            String.format("%s: Column Errors: %s", getClass().getSimpleName(), rd.toString()));
      }

      // Register all default columns with defined widths and ensure show=true
      List<XViewerColumn> columns = getColumns(getDefaultVisibleColumns());
      List<Integer> defaultColumnWidths = getDefaultColumnWidths();
      int colNum = 0;
      for (XViewerColumn xCol : columns) {
         xCol.setShow(true);
         int width = xCol.getWidth();
         if (defaultColumnWidths.size() > (colNum)) {
            width = defaultColumnWidths.get(colNum++);
         }
         xCol.setWidth(width);
         registerColumns(xCol);
         registeredColIds.add(xCol.getId());
      }

      // Register all remaining loaded columns and ensure show=false
      for (XViewerColumn xCol : loadedColIds.values()) {
         // Skip already loaded default visible
         if (!registeredColIds.contains(xCol.getId())) {
            xCol.setShow(false);
            registerColumns(xCol);
            registeredColIds.add(xCol.getId());
         }
      }
   }

   private XViewerAtsColumn createXColumn(AtsCoreColumn col) {
      if (col instanceof AtsCoreAttrTokenColumn) {
         AtsCoreAttrTokenColumn atsCoreAttrTokenColumn = (AtsCoreAttrTokenColumn) col;
         XViewerAtsAttrTokenXColumn xCol = new XViewerAtsAttrTokenXColumn(atsCoreAttrTokenColumn.getColumnToken());
         if (xCol.getAttributeType().isInvalid()) {
            OseeLog.log(WorldXViewerFactory.class, Level.SEVERE, String.format(
               "%s: Invalid Attr Type for col [%s] from [%s]", getClass().getSimpleName(), col, col.getSource()));
            return null;
         }
         if (atsCoreAttrTokenColumn.getColumnToken().isColumnMultiEdit()) {
            xCol.setMultiColumnEditable(true);
         }
         return xCol;
      } else if (col instanceof AtsCoreCodeColumn) {
         AtsCoreCodeColumn atsCoreCodeColumn = (AtsCoreCodeColumn) col;
         XViewerAtsCoreCodeXColumn xCol = new XViewerAtsCoreCodeXColumn(atsCoreCodeColumn.getColumnToken(), atsApi);
         return xCol;
      } else {
         OseeLog.log(WorldXViewerFactory.class, Level.SEVERE,
            String.format("%s: Unhandled col [%s]", getClass().getSimpleName(), col));
      }
      return null;
   }

   /**
    * This is the legacy way of providing columns. DO NOT USE THIS METHOD OF CONTRIBUTING COLUMNS.<br/>
    * <br/>
    * These should eventually be converted to one of the AtsCoreColumns except anything with Keep which are specific to
    * IDE/UI.
    */
   public static final List<XViewerColumn> getWorldViewIdeXColumns() {
      return Arrays.asList(

      // @formatter:off
         ActionableItemOwnerUI.getInstance(),
         AgileFeatureGroupColumnUI.getInstance(),
         AnnualCostAvoidanceColumnUI.getInstance(),
         ArtifactTokenColumn.getInstance(),
         ArtifactTypeColumn.getInstance(),
         AssigneeColumnUI.getInstance(),
         BacklogColumnUI.getInstance(),
         BacklogOrderColumnUI.getInstance(),
         BranchStatusColumnUI.getInstance(),
         ChangeTypeColumnUI.getInstance(),
         CompletedCancelledByColumnUI.getInstance(),
         CompletedCancelledDateColumnUI.getInstance(),
         CountryColumnUI.getInstance(),
         DaysInCurrentStateColumnUI.getInstance(),
         DeadlineColumnUI.getInstance(),
         GoalOrderColumnUI.getInstance(),
         GoalOrderVoteColumnUI.getInstance(),
         GoalsColumnUI.getInstance(),
         GroupsColumnUI.getInstance(),
         IdColumn.getInstance(),
         ImplementorColumnUI.getInstance(),
         LastModifiedByColumn.getInstance(),
         LastModifiedCommentColumn.getInstance(),
         LastModifiedDateColumn.getInstance(),
         LastModifiedTransactionColumn.getInstance(),
         LastModifiedTransactionCommentColumn.getInstance(),
         LastStatusedColumnUI.getInstance(),
         NumberOfTasksColumnUI.getInstance(),
         NumberOfTasksRemainingColumnUI.getInstance(),
         OriginatingWorkFlowColumnUI.getInstance(),
         OriginatorColumnUI.getInstance(),
         ParentAtsIdColumnUI.getInstance(),
         ParentIdColumnUI.getInstance(),
         ParentStateColumnUI.getInstance(),
         ParentTopTeamColumnUI.getInstance(),
         ParentWorkDefColumnUI.getInstance(),
         PercentCompleteReviewsColumnUI.getInstance(),
         PercentCompleteStateReviewColumnUI.getInstance(),
         PercentCompleteStateTasksColumnUI.getInstance(),
         PercentCompleteTasksColumnUI.getInstance(),
         PercentCompleteTasksReviewsColumnUI.getInstance(),
         PercentCompleteTotalColumnUI.getInstance(),
         PointsColumnUI.getInstance(),
         PriorityColumnUI.getInstance(),
         ProgramColumnUI.getInstance(),
         RelatedArtifactChangedColumnUI.getInstance(),
         RelatedArtifactLastModifiedByColumnUI.getInstance(),
         RelatedArtifactLastModifiedDateColumnUI.getInstance(),
         RelatedToStateColumnUI.getInstance(),
         RemainingHoursColumnUI.getInstance(),
         RemainingPointsNumericTotalColumnUI.getInstance(),
         RemainingPointsNumericWorkflowColumnUI.getInstance(),
         RemainingPointsTotalColumnUI.getInstance(),
         RemainingPointsWorkflowColumnUI.getInstance(),
         ReviewAuthorColumnUI.getInstance(),
         ReviewDeciderColumnUI.getInstance(),
         ReviewFormalTypeColumnUI.getInstance(),
         ReviewModeratorColumnUI.getInstance(),
         ReviewNumIssuesColumnUI.getInstance(),
         ReviewNumMajorDefectsColumnUI.getInstance(),
         ReviewNumMinorDefectsColumnUI.getInstance(),
         ReviewReviewerColumnUI.getInstance(),
         SiblingAtsIdColumnUI.getInstance(),
         SiblingTeamDefColumnUI.getInstance(),
         SprintColumnUI.getInstance(),
         SprintOrderColumnUI.getInstance(),
         TargetedVersionColumnUI.getInstance(),
         TaskRelatedArtifactTypeColumnUI.getInstance(),
         TitleColumnUI.getInstance(),
         WorkDaysNeededColumnUI.getInstance(),
         WorkPackageTextColumnUI.getInstance(),
         WorkingBranchArchivedColumnUI.getInstance(),
         WorkingBranchIdColumnUI.getInstance(),
         WorkingBranchStateColumnUI.getInstance(),
         WorkingBranchTypeColumnUI.getInstance(),

         // Sign-by columns
         ApproveRequestedByColumnUI.getInstance(), // Keep
         ApproveRequestedByDateColumnUI.getInstance(), // Keep
         SignedByColumnUI.getInstance(), // Keep
         SignedByDateColumnUI.getInstance(), // Keep
         ReviewedByColumnUI.getInstance(), // Keep
         ReviewedByDateColumnUI.getInstance()); // Keep

      // @formatter:on

   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

   @Override
   public XViewerColumn getDefaultXViewerColumn(String id) {
      XViewerColumn xCol = super.getDefaultXViewerColumn(id);
      if (xCol == null) {
         String newId = getIdFromLegacyId(id);
         xCol = super.getDefaultXViewerColumn(newId);
      }
      return xCol;
   }

   private String getIdFromLegacyId(String legacyId) {
      return AtsApiService.get().getColumnService().getIdFromLegacyId(legacyId);
   }

}
