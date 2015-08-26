/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.column.PercentCompleteWorkflowColumn;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class WorkItemAndPackageReport extends XNavigateItemAction {

   public static final String TITLE = "Work Item and Package Report";

   public WorkItemAndPackageReport(XNavigateItem parent) {
      super(parent, TITLE, PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<IResultsEditorTab>();
               createWorkItemTab(tabs);
            }
            return tabs;
         }

      });
   }

   private void createWorkItemTab(List<IResultsEditorTab> tabs) {
      List<XViewerColumn> columns = getColumns();
      XResultData results = new XResultData(false);

      List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
      WorkPackageConfigLoader loader = new WorkPackageConfigLoader();
      loader.load();

      QueryBuilderArtifact queryBuilder = ArtifactQuery.createQueryBuilder(AtsUtilCore.getAtsBranch());
      queryBuilder.andIsOfType(AtsArtifactTypes.AbstractWorkflowArtifact).andExists(AtsAttributeTypes.WorkPackageGuid);
      for (Artifact workItemArt : queryBuilder.getResults()) {

         try {
            IAtsWorkItem item = AtsClientService.get().getWorkItemFactory().getWorkItem(workItemArt);
            String workPackageGuid = workItemArt.getSoleAttributeValue(AtsAttributeTypes.WorkPackageGuid);
            WorkPackageData data = loader.getWorkPackageData(workPackageGuid);
            if (data == null) {
               results.errorf("Work Package with guid [%s] from workflow %s does not exist.  Ignoring...\n",
                  workPackageGuid, item.toStringWithId());
            } else {
               rows.add(new ResultsXViewerRow(new String[] {
                  workItemArt.getName(),
                  workItemArt.getSoleAttributeValue(AtsAttributeTypes.AtsId, ""),
                  item.getParentTeamWorkflow().getTeamDefinition().getName(),
                  item.getStateMgr().getCurrentStateName(),
                  PercentCompleteWorkflowColumn.getInstance().getColumnText(workItemArt, null, 0),
                  PercentCompleteTotalColumn.getInstance().getColumnText(workItemArt, null, 0),
                  item.getStateMgr().getAssigneesStr(),
                  data.getCountryName(),
                  data.getProgramName(),
                  data.getInsertionName(),
                  data.getInsertionActivityName(),
                  data.getWorkPackageName(),
                  data.getColorTeam(),
                  data.getWorkPackageProgram(),
                  data.getWorkPackageId(),
                  data.getActivityId()}));
            }
         } catch (Exception ex) {
            String errorStr = String.format("Exception processing workflow %s - %s.  See log for details.\n",
               workItemArt.toStringWithId(), ex.getMessage());
            OseeLog.log(Activator.class, Level.SEVERE, errorStr, ex);
            results.errorf(errorStr);
         }
      }
      if (!results.isErrors()) {
         results.log("No Errors Found\n");
      }
      results.log("\nSee Next Tab for Results");
      XResultPage resultPage = XResultDataUI.getReport(results, getName(), Manipulations.HTML_MANIPULATIONS);
      tabs.add(new ResultsEditorHtmlTab(resultPage));
      tabs.add(new ResultsEditorTableTab(TITLE + " Report", columns, rows));
   }

   private List<XViewerColumn> getColumns() {
      List<XViewerColumn> columns = Arrays.asList( //
         WorkPackageReportColumns.getDefaultColumn("Title", 160),
         WorkPackageReportColumns.getDefaultColumn("ATS ID", 40), WorkPackageReportColumns.getDefaultColumn("Team", 40),
         WorkPackageReportColumns.getDefaultColumn("State", 40),
         WorkPackageReportColumns.getDefaultColumn("Workflow Percent Complete", 40),
         WorkPackageReportColumns.getDefaultColumn("Total Percent Complete", 40),
         WorkPackageReportColumns.getDefaultColumn("Assignees", 40), WorkPackageReportColumns.countryColumn,
         WorkPackageReportColumns.programColumn, WorkPackageReportColumns.insertionColumn,
         WorkPackageReportColumns.insertionActivityColumn, WorkPackageReportColumns.workPackageNameColumn,
         WorkPackageReportColumns.wpColorTeamColumn, WorkPackageReportColumns.wpProgramColumn,
         WorkPackageReportColumns.wpIdColumn, WorkPackageReportColumns.wpActivityIdColumn
      //
      );
      return columns;
   }

}
