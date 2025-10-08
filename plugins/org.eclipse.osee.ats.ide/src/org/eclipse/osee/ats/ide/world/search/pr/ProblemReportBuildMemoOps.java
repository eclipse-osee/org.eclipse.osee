/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.world.search.pr;

import static org.eclipse.nebula.widgets.xviewer.core.model.SortDataType.String;
import static org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign.Left;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.ExportResultEditorToWorkbook;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class ProblemReportBuildMemoOps {

   private final WorldEditor worldEditor;
   private final String TITLE = "Problem Report - Build Memo";

   public ProblemReportBuildMemoOps(WorldEditor worldEditor) {
      this.worldEditor = worldEditor;
   }

   public void open() {
      List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
      if (loadedArtifacts.isEmpty()) {
         AWorkbench.popup("Nothing Loaded");
         return;
      }
      openResultsEditor(loadedArtifacts, 0L);
   }

   private void openResultsEditor(List<Artifact> loadedArtifacts, final Long editorId) {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               tabs.add(createWorkflowTab(StateType.Working, "In-Work", loadedArtifacts));
               tabs.add(createWorkflowTab(StateType.Completed, "Closed", loadedArtifacts));
               tabs.add(createWorkflowTab(StateType.Cancelled, "Cancelled", loadedArtifacts));
               tabs.add(createDetailsHtmlTab());
            }
            return tabs;
         }

         @Override
         public Long getEditorId() {
            return editorId;
         }
      });
   }

   private IResultsEditorTab createWorkflowTab(StateType stateType, String title, List<Artifact> loadedArtifacts) {
      List<XViewerColumn> cols = new ArrayList<>();

      /**
       * ATS ID, Legacy Id, PCR Ids, Priority, Title, Issue Description, Operational Impact, Work around, Date of PR
       * origination, It would be nice to include COG for review by subsystem then to remove that field prior to
       * delivery. • Subsystem <- not currently on Prod ASIL, but would be nice to have information.
       */
      cols.add(new XViewerColumn("col.pr.id", "PR ID", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.state", "State", 100, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.legacy.id", "Legacy ID", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.legacy.pcr.id", "PCR ID(s)", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.priority", "Priority", 40, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.title", "Title", 200, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.description", "Description", 200, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.oper.impact", "Operational Impact", 200, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.work.around", "Work Around", 200, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.create.date", "Origination Date", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.cog.priority", "COG Priority", 40, Left, true, String, false, ""));

      AtsApi atsApi = AtsApiService.get();

      List<IResultsXViewerRow> artRows = new ArrayList<>();
      try {
         for (Artifact art : loadedArtifacts) {
            IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(art);
            if (teamWf.getCurrentStateType().equals(stateType)) {
               artRows.add(new ResultsXViewerRow(new String[] { //

                  teamWf.getAtsId(),
                  teamWf.getCurrentStateName(),
                  teamWf.getLegacyId(),
                  Collections.toString(",", teamWf.getPcrIds()),
                  atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""),
                  teamWf.getName(),
                  teamWf.getDescription(),
                  atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.OperationalImpact, ""),
                  atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Workaround, ""),
                  DateUtil.getMMDDYY(teamWf.getCreatedDate()),
                  atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.CogPriority, "")

               }, art));
            }
         }
      } catch (OseeCoreException ex) {
         // do nothing
      }

      return new ResultsEditorTableTab(stateType.name(), cols, artRows);

   }

   private IResultsEditorTab createDetailsHtmlTab() {
      return new ResultsEditorHtmlTab(TITLE, "Details", AHTML.simplePage(getHtmlReport()));
   }

   public String getHtmlReport() {

      XResultData rd = new XResultData();
      rd.log(TITLE + "\n\nCreated: " + DateUtil.getDateNow(DateUtil.MMDDYYHHMM) + "\n\n");
      return AHTML.simplePage(AHTML.textToHtml(rd.toString()));
   }

   public void openAndExport() {

      // Validate
      Long editorId = Lib.generateId();
      List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
      if (loadedArtifacts.isEmpty()) {
         AWorkbench.popup("Nothing Loaded");
         return;
      }

      // Open ResultsEditor Automatically
      ResultsEditor resultsEditor = null;
      openResultsEditor(loadedArtifacts, editorId);

      // Find ResultsEditor just opened
      for (ResultsEditor rEditor : ResultsEditor.getEditors()) {
         if (rEditor.getEditorInput().getEditorId().equals(editorId)) {
            resultsEditor = rEditor;
            break;
         }
      }

      // Run Export
      if (resultsEditor != null) {
         new ExportResultEditorToWorkbook(resultsEditor).run();

         // Close Editor
         resultsEditor.close(false);
      } else {
         AWorkbench.popup("Can not find opened ResultsEditor");
      }

   }

}
