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
import org.eclipse.osee.ats.ide.world.WorldEditorInput;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItemProvider;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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

   protected final WorldEditor worldEditor;
   private final String title;
   protected WorldSearchItem worldSearchItem;

   public ProblemReportBuildMemoOps(WorldEditor worldEditor, String title) {
      this.worldEditor = worldEditor;
      this.title = title;
   }

   public void validateParameters(XResultData rd) {
      // for sub-class
   }

   public void run() {
      WorldEditorInput weimp = (WorldEditorInput) worldEditor.getEditorInput();
      WorldEditorParameterSearchItemProvider editorInp =
         (WorldEditorParameterSearchItemProvider) weimp.getIWorldEditorProvider();
      worldSearchItem = editorInp.getWorldSearchItem();

      XResultData rd = new XResultData();
      validateParameters(rd);
      if (rd.isErrors()) {
         AWorkbench.popup(rd.toString());
         return;
      }

      List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
      if (loadedArtifacts.isEmpty()) {
         AWorkbench.popup("Nothing Loaded; Enter Parameters and Search");
         return;
      }
      openResultsEditor(loadedArtifacts, 0L);
   }

   private void openResultsEditor(List<Artifact> loadedArtifacts, final Long editorId) {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return title;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               tabs.add(createDetailsHtmlTab());
               tabs.add(createWorkflowTab(StateType.Working, "In-Work", loadedArtifacts));
               tabs.add(createWorkflowTab(StateType.Completed, "Closed", loadedArtifacts));
               tabs.add(createWorkflowTab(StateType.Cancelled, "Cancelled", loadedArtifacts));
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

      List<XViewerColumn> cols = createTableColumns();

      AtsApi atsApi = AtsApiService.get();

      List<IResultsXViewerRow> artRows = new ArrayList<>();
      try {
         for (Artifact art : loadedArtifacts) {
            IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(art);
            if (teamWf.getCurrentStateType().equals(stateType)) {
               addTableRow(atsApi, artRows, art, teamWf);
            }
         }
      } catch (OseeCoreException ex) {
         // do nothing
      }

      return new ResultsEditorTableTab(stateType.name(), cols, artRows);

   }

   private void addTableRow(AtsApi atsApi, List<IResultsXViewerRow> artRows, Artifact art, IAtsTeamWorkflow teamWf) {

      artRows.add(new ResultsXViewerRow(new String[] { //

         teamWf.getAtsId(),
         teamWf.getCurrentStateName(),
         teamWf.getLegacyId(),
         Collections.toString(",", teamWf.getPcrIds()),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""),
         teamWf.getName(),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, getDescriptionAttrType(worldSearchItem), ""),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, getOperationalImpactAttrType(worldSearchItem), ""),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, getWorkaroundAttrType(worldSearchItem), ""),
         DateUtil.getMMDDYY(teamWf.getCreatedDate()),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, getSubsystemAttrType(worldSearchItem), ""),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.CogPriority, "")

      }, art));
   }

   protected AttributeTypeToken getDescriptionAttrType(WorldSearchItem worldSearchItem) {
      return CoreAttributeTypes.Description;
   }

   protected AttributeTypeToken getSubsystemAttrType(WorldSearchItem worldSearchItem) {
      return CoreAttributeTypes.Subsystem;
   }

   protected AttributeTypeToken getWorkaroundAttrType(WorldSearchItem worldSearchItem) {
      return AtsAttributeTypes.Workaround;
   }

   protected AttributeTypeToken getOperationalImpactAttrType(WorldSearchItem worldSearchItem) {
      return AtsAttributeTypes.OperationalImpact;
   }

   protected List<XViewerColumn> createTableColumns() {
      List<XViewerColumn> cols = new ArrayList<>();

      cols.add(new XViewerColumn("col.pr.id", "PR ID", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.state", "State", 100, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.legacy.id", "Legacy ID", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.legacy.pcr.id", "PCR ID(s)", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.priority", "Priority", 40, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.title", "Title", 200, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.description", getDescriptionAttrType(worldSearchItem).getUnqualifiedName(), 200,
         Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.oper.impact", getOperationalImpactAttrType(worldSearchItem).getUnqualifiedName(),
         200, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.work.around", getWorkaroundAttrType(worldSearchItem).getUnqualifiedName(), 200,
         Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.create.date", "Origination Date", 75, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.subsystem", getSubsystemAttrType(worldSearchItem).getUnqualifiedName(), 40, Left,
         true, String, false, ""));
      cols.add(new XViewerColumn("col.cog.priority", "COG Priority", 40, Left, true, String, false, ""));
      return cols;
   }

   private IResultsEditorTab createDetailsHtmlTab() {
      return new ResultsEditorHtmlTab(title, "Details", AHTML.simplePage(getHtmlReport()));
   }

   public String getHtmlReport() {
      String html = worldEditor.getWorldEditorProvider().getWorldEditorHtmlReport();
      if (Strings.isInvalid(html)) {
         XResultData rd = new XResultData();
         rd.log(title + "\n\nCreated: " + DateUtil.getDateNow(DateUtil.MMDDYYHHMM) + "\n\n");
         html = AHTML.textToHtml(rd.toString());
      }
      return AHTML.simplePage(html);
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
