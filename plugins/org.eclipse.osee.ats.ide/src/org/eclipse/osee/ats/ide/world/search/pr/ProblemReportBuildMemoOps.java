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
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorInput;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItemProvider;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.ExportResultEditorToWorkbook;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class ProblemReportBuildMemoOps {

   public static final String NOTHING_LOADED = "Nothing Loaded; Enter Parameters and Search";
   protected final WorldEditor worldEditor;
   private final String title;
   protected WorldSearchItem worldSearchItem;
   private AttributeTypeToken descriptionAttrType;
   private AttributeTypeToken operationalImpactAttrType;
   private AttributeTypeToken workaroundAttrType;
   private AttributeTypeToken subsystemAttrType;
   private List<XViewerColumn> tableColumns;
   protected final AtsApiIde atsApi;
   protected final List<ProblemReportCollector> collections = new ArrayList<>();
   protected XResultData rd;

   public ProblemReportBuildMemoOps(WorldEditor worldEditor, String title) {
      this.worldEditor = worldEditor;
      this.title = title;
      this.atsApi = AtsApiService.get();
   }

   public void validateParameters() {
      // for sub-class
   }

   public void generateAndOpen() {
      setup();

      rd = worldEditor.getWorldEditorProvider().getWorldEditorHtmlReportRd();

      validateParameters();
      if (rd.isErrors()) {
         XResultDataUI.report(rd, title);
         return;
      }

      List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
      if (loadedArtifacts.isEmpty()) {
         rd.errorf(NOTHING_LOADED);
         XResultDataUI.report(rd, title);
         return;
      }

      validateLoaded(loadedArtifacts);

      createCollections(loadedArtifacts);

      openResultsEditor(0L);
   }

   protected String getExportFileNamePrefix() {
      return getClass().getSimpleName();
   }

   public void generateOpenAndExport() {
      setup();

      rd = worldEditor.getWorldEditorProvider().getWorldEditorHtmlReportRd();

      validateParameters();
      if (rd.isErrors()) {
         XResultDataUI.report(rd, title);
         return;
      }

      Long editorId = Lib.generateId();
      List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
      if (loadedArtifacts.isEmpty()) {
         rd.errorf(NOTHING_LOADED);
         XResultDataUI.report(rd, title);
         return;
      }

      validateLoaded(loadedArtifacts);

      // Open ResultsEditor Automatically
      ResultsEditor resultsEditor = null;

      createCollections(loadedArtifacts);

      openResultsEditor(editorId);

      // Find ResultsEditor just opened
      for (ResultsEditor rEditor : ResultsEditor.getEditors()) {
         if (rEditor.getEditorInput().getEditorId().equals(editorId)) {
            resultsEditor = rEditor;
            break;
         }
      }

      // Run Export
      if (resultsEditor != null) {
         new ExportResultEditorToWorkbook(resultsEditor, getExportFileNamePrefix()).run();

         // Close Editor
         resultsEditor.close(false);
      } else {
         AWorkbench.popup("Can not find opened ResultsEditor");
      }

   }

   public XResultData validateLoaded(XResultData rd) {
      if (rd == null) {
         this.rd = worldEditor.getWorldEditorProvider().getWorldEditorHtmlReportRd();
      } else {
         this.rd = rd;
      }

      List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
      validateLoaded(loadedArtifacts);

      return rd;
   }

   private void validateLoaded(List<Artifact> loadedArtifacts) {
      rd.log("\n\n\nValidate Problem Reports: \n---------------------------------------------");

      // Pre-load BIDs
      RelationManager.getRelatedArtifacts(loadedArtifacts, 1, AtsRelationTypes.ProblemReportToBid_Bid);

      for (Artifact art : loadedArtifacts) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(art);

         // Validate BITs and PR State
         if (teamWf.isCancelled()) {
            for (ArtifactToken bidArt : atsApi.getRelationResolver().getRelated(teamWf,
               AtsRelationTypes.ProblemReportToBid_Bid)) {
               String bidState =
                  atsApi.getAttributeResolver().getSoleAttributeValueAsString(bidArt, AtsAttributeTypes.BitState, "");
               if (!bidState.equals(BuildImpactState.Cancelled.name())) {
                  rd.errorf("PR %s Cancelled but BID state [%s]\n", teamWf.getAtsId(), bidState);
               }
            }
         }

         validateLoaded(teamWf, rd);

      }
      rd.logf("Validated %s PRs\n", loadedArtifacts.size());
   }

   protected void validateLoaded(IAtsTeamWorkflow teamWf, XResultData rd) {
      // for subclass
   }

   protected void createCollections(List<Artifact> loadedArtifacts) {

      ProblemReportCollector workingCol = new ProblemReportCollector();
      workingCol.setName("Working");
      workingCol.setDescription("Workflows are still in an Open/Working state.");
      collections.add(workingCol);

      ProblemReportCollector completedCol = new ProblemReportCollector();
      completedCol.setName("Completed");
      completedCol.setDescription("Workflows are are in a Completed state.");
      collections.add(completedCol);

      ProblemReportCollector cancelledCol = new ProblemReportCollector();
      cancelledCol.setName("Cancelled");
      cancelledCol.setDescription("Workflows are are in a Cancelled state.");
      collections.add(cancelledCol);

      rd.logf("\nPR Tab Calcuations: \n---------------------------------------------\n\n");
      logCollectionTabDetails();

      for (Artifact art : loadedArtifacts) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(art);
         StateType stateType = teamWf.getCurrentStateType();
         if (stateType.equals(StateType.Working)) {
            workingCol.add(art);
         } else if (stateType.equals(StateType.Completed)) {
            completedCol.add(art);
         } else if (stateType.equals(StateType.Cancelled)) {
            cancelledCol.add(art);
         }
      }

   }

   protected void logCollectionTabDetails() {
      rd.log("PR Tab Descriptions: ");
      for (ProblemReportCollector coll : collections) {
         String desc = coll.getDescription();
         if (Strings.isValid(desc)) {
            rd.logf("-  %s - %s\n", coll.getName(), coll.getDescription());
         } else {
            rd.logf("-  %s\n", coll.getName());
         }
      }
      rd.log("");
   }

   private void setup() {
      WorldEditorInput weimp = (WorldEditorInput) worldEditor.getEditorInput();
      WorldEditorParameterSearchItemProvider editorInp =
         (WorldEditorParameterSearchItemProvider) weimp.getIWorldEditorProvider();
      worldSearchItem = editorInp.getWorldSearchItem();

      descriptionAttrType = getDescriptionAttrType(worldSearchItem);
      operationalImpactAttrType = getOperationalImpactAttrType(worldSearchItem);
      workaroundAttrType = getWorkaroundAttrType(worldSearchItem);
      subsystemAttrType = getSubsystemAttrType(worldSearchItem);

      tableColumns = createTableColumns();
   }

   protected void openResultsEditor(final Long editorId) {
      setup();
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
               for (ProblemReportCollector coll : collections) {
                  tabs.add(createWorkflowTab(coll));
               }
            }
            return tabs;
         }

         @Override
         public Long getEditorId() {
            return editorId;
         }
      });
   }

   private IResultsEditorTab createWorkflowTab(ProblemReportCollector coll) {
      List<IResultsXViewerRow> artRows = new ArrayList<>();
      for (Artifact art : coll.getCollection()) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(art);
         addTableRow(atsApi, artRows, art, teamWf);
      }
      return new ResultsEditorTableTab(coll.getName(), tableColumns, artRows);
   }

   private void addTableRow(AtsApi atsApi, List<IResultsXViewerRow> artRows, Artifact art, IAtsTeamWorkflow teamWf) {

      artRows.add(new ResultsXViewerRow(new String[] { //

         teamWf.getAtsId(),
         teamWf.getCurrentStateName(),
         teamWf.getLegacyId(),
         Collections.toString(",", teamWf.getPcrIds()),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""),
         teamWf.getName(),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, descriptionAttrType, ""),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, operationalImpactAttrType, ""),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, workaroundAttrType, ""),
         DateUtil.getMMDDYY(teamWf.getCreatedDate()),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, subsystemAttrType, ""),
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
      cols.add(new XViewerColumn("col.description", descriptionAttrType.getUnqualifiedName(), 200, Left, true, String,
         false, ""));
      cols.add(new XViewerColumn("col.oper.impact", operationalImpactAttrType.getUnqualifiedName(), 200, Left, true,
         String, false, ""));
      cols.add(new XViewerColumn("col.work.around", workaroundAttrType.getUnqualifiedName(), 200, Left, true, String,
         false, ""));
      cols.add(new XViewerColumn("col.create.date", "Origination Date", 75, Left, true, String, false, ""));
      cols.add(
         new XViewerColumn("col.subsystem", subsystemAttrType.getUnqualifiedName(), 40, Left, true, String, false, ""));
      cols.add(new XViewerColumn("col.cog.priority", "COG Priority", 40, Left, true, String, false, ""));
      return cols;
   }

   private IResultsEditorTab createDetailsHtmlTab() {
      String htmlReport = XResultDataUI.getReport(rd, title, Manipulations.HTML_MANIPULATIONS,
         Manipulations.CONVERT_NEWLINES, Manipulations.ERROR_WARNING_HEADER).getManipulatedHtml();
      return new ResultsEditorHtmlTab(title, "Details", htmlReport);
   }

}
