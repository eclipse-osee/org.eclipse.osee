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
package org.eclipse.osee.ats.util.Import;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
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

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XButtonPush\" displayName=\"Open Excel Import Example Spreadsheet\" displayLabel=\"false\"/>");
      builder.append("<XWidget xwidgetType=\"XFileTextWithSelectionDialog\" displayName=\"Excel Spreadsheet saved as xml\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Email POCs\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
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
            importActions(file, ImportOption.POPUP_ERROR_REPORT,
               (emailPocs ? ImportOption.EMAIL_POCS : ImportOption.NONE));
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

      } finally {
         monitor.subTask("Done");
      }
   }

   public XResultData importActions(File file, ImportOption... importOption) throws Exception {
      boolean emailPocs = Collections.getAggregate(importOption).contains(ImportOption.EMAIL_POCS);
      boolean popupReport = Collections.getAggregate(importOption).contains(ImportOption.POPUP_ERROR_REPORT);
      ExcelAtsActionArtifactExtractor extractor = new ExcelAtsActionArtifactExtractor(emailPocs);
      extractor.process(file.toURI());
      XResultData rd = extractor.dataIsValid();
      if (!rd.toString().equals("")) {
         if (popupReport) {
            XResultDataUI.report(rd,"Ats Action Import Errors");
         }
      } else {
         SkynetTransaction transaction =
            new SkynetTransaction(AtsUtil.getAtsBranch(), "Import Actions from Spreadsheet");
         extractor.createArtifactsAndNotify(transaction);
         WorldEditor.open(new WorldEditorSimpleProvider("Imported Action Artifacts", extractor.getActionArts()));
         transaction.execute();
      }
      return rd;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
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
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      }
   }

   public File getSampleSpreadsheetFile() throws Exception {
      PluginUtil util = new PluginUtil(AtsPlugin.PLUGIN_ID);
      return util.getPluginFile("support/Action Import.xml");
   }

   @Override
   public String getDescriptionUsage() {
      return "Import Actions via Excel Spreadsheet.\n Open example spreadsheet, save to desktop and edit as desired. Once completed, select file from this BLAM and Run.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS.ADMIN");
   }

}
