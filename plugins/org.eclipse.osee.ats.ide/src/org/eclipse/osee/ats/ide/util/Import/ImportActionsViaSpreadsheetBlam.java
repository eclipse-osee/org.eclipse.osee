/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.util.Import;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.Import.ExcelAtsActionArtifactExtractor.ActionData;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ImportActionsViaSpreadsheetBlam extends AbstractBlam {

   public static enum ImportOption {
      EMAIL_POCS,
      POPUP_ERROR_REPORT,
      PERSIST,
      NONE
   }

   @Override
   public String getName() {
      return "Import Actions Via Spreadsheet";
   }

   protected boolean includeGoalWidget() {
      return true;
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append(
         "<XWidget xwidgetType=\"XButtonPush\" displayName=\"Open Excel Import Example Spreadsheet\" displayLabel=\"false\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XFileTextWithSelectionDialog\" displayName=\"Excel Spreadsheet saved as xml\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Email POCs\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Persist\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      if (includeGoalWidget()) {
         builder.append(
            "<XWidget xwidgetType=\"XGoalCombo\" displayName=\"Add to Goal\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      }
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         String path = variableMap.getString("Excel Spreadsheet saved as xml");
         File file = new File(path);
         boolean isValid = Strings.isValid(path) && path.endsWith(".xml") && file.isFile();
         if (!isValid) {
            AWorkbench.popup("File is not a valid xml file");
            return;
         }
         boolean emailPocs = variableMap.getBoolean("Email POCs");
         boolean persist = variableMap.getBoolean("Persist");

         try {
            importActions(file, null, ImportOption.POPUP_ERROR_REPORT, //
               (emailPocs ? ImportOption.EMAIL_POCS : ImportOption.NONE), //
               (persist ? ImportOption.PERSIST : ImportOption.NONE));
         } catch (Exception ex) {
            log(ex);
         }

      } finally {
         monitor.subTask("Done");
      }
   }

   public XResultData importActions(File file, IAtsGoal toGoal, ImportOption... importOption) throws Exception {
      boolean emailPocs = Arrays.asList(importOption).contains(ImportOption.EMAIL_POCS);
      boolean persist = Arrays.asList(importOption).contains(ImportOption.PERSIST);
      boolean popupReport = Arrays.asList(importOption).contains(ImportOption.POPUP_ERROR_REPORT);
      ExcelAtsActionArtifactExtractor extractor = new ExcelAtsActionArtifactExtractor(emailPocs, toGoal);
      extractor.process(file.toURI());
      XResultData rd = extractor.dataIsValid();
      if (!rd.toString().equals("")) {
         if (popupReport) {
            XResultDataUI.report(rd, "Ats Action Import Errors");
         }
      } else {
         if (persist) {
            IAtsChangeSet changes = AtsApiService.get().createChangeSet("Import Actions from Spreadsheet");
            extractor.createArtifactsAndNotify(changes);
            changes.execute();
            WorldEditor.open(new WorldEditorSimpleProvider("Imported Action Artifacts", extractor.getActionArts()));
         } else {
            openValidationEditor(extractor.getActionDatas());
         }
      }
      return rd;
   }

   private void openValidationEditor(final List<ActionData> actionDatas) {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return getName();
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               tabs.add(createDataTab());
            }
            return tabs;
         }

         private IResultsEditorTab createDataTab() {
            List<IResultsXViewerRow> rows = new ArrayList<>();
            List<XViewerColumn> columns = new ArrayList<>();
            columns.add(new XViewerColumn(Columns.Title.name(), Columns.Title.name(), 280, XViewerAlign.Left, true,
               SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.Description.name(), Columns.Description.name(), 80, XViewerAlign.Left,
               true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.ActionableItem.name(), Columns.ActionableItem.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.Assignees.name(), Columns.Assignees.name(), 80, XViewerAlign.Left,
               true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.Originator.name(), Columns.Originator.name(), 80, XViewerAlign.Left,
               true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.ChangeType.name(), Columns.ChangeType.name(), 80, XViewerAlign.Left,
               true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.Priority.name(), Columns.Priority.name(), 30, XViewerAlign.Left, true,
               SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.Version.name(), Columns.Version.name(), 80, XViewerAlign.Left, true,
               SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.EstimatedHours.name(), Columns.EstimatedHours.name(), 40,
               XViewerAlign.Left, true, SortDataType.Integer, false, ""));
            columns.add(new XViewerColumn(Columns.AgilePoints.name(), Columns.AgilePoints.name(), 40, XViewerAlign.Left,
               true, SortDataType.Integer, false, ""));
            columns.add(new XViewerColumn(Columns.AgileTeamNam.name(), Columns.AgileTeamNam.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(Columns.AgileSprintName.name(), Columns.AgileSprintName.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));

            for (JaxAttribute attr : actionDatas.iterator().next().attributes) {
               columns.add(new XViewerColumn(attr.getAttrType().getName(), attr.getAttrType().getName(), 80,
                  XViewerAlign.Left, true, SortDataType.String, false, ""));
            }

            for (ActionData aData : actionDatas) {
               ResultsXViewerRow row = new ResultsXViewerRow();
               rows.add(row);
               row.addValue(aData.title);
               row.addValue(aData.desc);
               row.addValue(aData.actionableItems.toString());
               row.addValue(aData.assignees.toString());
               row.addValue(aData.originator.toString());
               row.addValue(aData.changeType.toString());
               row.addValue(aData.priorityStr.toString());
               row.addValue(aData.version.toString());
               if (aData.estimatedHours != null) {
                  row.addValue(aData.estimatedHours.toString());
               } else {
                  row.addValue("");
               }
               row.addValue(aData.agilePoints.toString());
               row.addValue(aData.agileTeamName.toString());
               row.addValue(aData.agileSprintName.toString());
               for (JaxAttribute attr : aData.attributes) {
                  row.addValue(attr.getValues().toString());
               }
            }

            return new ResultsEditorTableTab("Data", columns, rows);
         }
      });

   }
   public static enum Columns {
      Title,
      Description,
      ActionableItem,
      Assignees,
      Originator,
      ChangeType,
      Priority,
      Version,
      AgilePoints,
      EstimatedHours,
      AgileSprintName,
      AgileTeamNam;
   };

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Open Excel Import Example Spreadsheet")) {
         XButtonPush button = (XButtonPush) xWidget;
         button.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  File file = getSampleSpreadsheetFile();
                  Program.launch(file.getCanonicalPath());
               } catch (Exception ex) {
                  log(ex);
               }
            }
         });
      }
   }

   public File getSampleSpreadsheetFile() throws Exception {
      return OseeInf.getResourceAsFile("atsImport/Action_Import.xml", getClass());
   }

   @Override
   public String getDescriptionUsage() {
      return "Import Actions via Excel Spreadsheet.\n Open example spreadsheet, " //
         + "save to desktop and edit as desired. Once completed, select file from " //
         + "this BLAM and Run.  Titles that are the same are placed under the same action." //
         + "  After \"Estimated Hours\", remaining columns will attempt to match " //
         + "column name with valid attribute type name add that to Task.";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_IMPORT);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.IMPORT);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.IMPORT);
   }

}
