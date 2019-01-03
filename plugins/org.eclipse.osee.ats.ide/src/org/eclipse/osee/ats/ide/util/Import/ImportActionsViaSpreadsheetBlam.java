/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.Import;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ImportActionsViaSpreadsheetBlam extends AbstractBlam {

   public static enum ImportOption {
      EMAIL_POCS,
      POPUP_ERROR_REPORT,
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

         try {
            importActions(file, null, ImportOption.POPUP_ERROR_REPORT,
               emailPocs ? ImportOption.EMAIL_POCS : ImportOption.NONE);
         } catch (Exception ex) {
            log(ex);
         }

      } finally {
         monitor.subTask("Done");
      }
   }

   public XResultData importActions(File file, IAtsGoal toGoal, ImportOption... importOption) throws Exception {
      boolean emailPocs = Arrays.asList(importOption).contains(ImportOption.EMAIL_POCS);
      boolean popupReport = Arrays.asList(importOption).contains(ImportOption.POPUP_ERROR_REPORT);
      ExcelAtsActionArtifactExtractor extractor = new ExcelAtsActionArtifactExtractor(emailPocs, toGoal);
      extractor.process(file.toURI());
      XResultData rd = extractor.dataIsValid();
      if (!rd.toString().equals("")) {
         if (popupReport) {
            XResultDataUI.report(rd, "Ats Action Import Errors");
         }
      } else {
         IAtsChangeSet changes = AtsClientService.get().createChangeSet("Import Actions from Spreadsheet");
         extractor.createArtifactsAndNotify(changes);
         changes.execute();
         WorldEditor.open(new WorldEditorSimpleProvider("Imported Action Artifacts", extractor.getActionArts()));
      }
      return rd;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
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
   public Collection<String> getCategories() {
      return Arrays.asList("ATS.ADMIN");
   }

}
