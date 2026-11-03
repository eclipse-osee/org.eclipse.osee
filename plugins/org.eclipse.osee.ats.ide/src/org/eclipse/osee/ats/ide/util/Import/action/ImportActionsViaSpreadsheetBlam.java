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

package org.eclipse.osee.ats.ide.util.Import.action;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonPushWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ImportActionsViaSpreadsheetBlam extends AbstractAtsBlam {

   public static final String BLAM_NAME = "Import Actions Via Spreadsheet";
   static final String EXCEL_SPREADSHEET_SAVED_AS_XML = "Excel Spreadsheet saved as xml";
   static final String EMAIL_POCS = "Email POCs";
   static final String PERSIST = "Persist";
   static final String OPEN_EXCEL_SPREADSHEET = "Open Excel Import Example Spreadsheet";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public ImportActionsViaSpreadsheetBlam() {
      this(BLAM_NAME, CoreImage.IMPORT);
   }

   public ImportActionsViaSpreadsheetBlam(String name) {
      this(name, CoreImage.IMPORT);
   }

   public ImportActionsViaSpreadsheetBlam(String name, OseeImage image) {
      super(name, image);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andButtonPushWidget(OPEN_EXCEL_SPREADSHEET).noLabel();
      wb.andFileSelWidget(EXCEL_SPREADSHEET_SAVED_AS_XML);
      wb.andCheckBoxWidget(EMAIL_POCS);
      wb.andCheckBoxWidget(PERSIST);
      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals(OPEN_EXCEL_SPREADSHEET)) {
         XXButtonPushWidget button = (XXButtonPushWidget) xWidget;
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

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

      try {
         String path = variableMap.getString(EXCEL_SPREADSHEET_SAVED_AS_XML);
         File file = new File(path);
         boolean isValid = Strings.isValid(path) && path.endsWith(".xml") && file.isFile();
         if (!isValid) {
            AWorkbench.popup("File is not a valid xml file");
            return;
         }
         boolean emailPocs = variableMap.getBoolean(EMAIL_POCS);
         boolean persist = variableMap.getBoolean(PERSIST);

         try {
            importActions(file, ImportOption.POPUP_ERROR_REPORT,
               (emailPocs ? ImportOption.EMAIL_POCS : ImportOption.NONE), //
               (persist ? ImportOption.PERSIST : ImportOption.NONE));
         } catch (Exception ex) {
            log(ex);
         }

      } finally {
         monitor.subTask("Done");
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   public static enum ImportOption {
      EMAIL_POCS,
      POPUP_ERROR_REPORT,
      PERSIST,
      NONE
   }

   protected boolean includeGoalWidget() {
      return true;
   }

   public XResultData importActions(File file, IAtsGoal toGoal, ImportOption... importOption) throws Exception {
      boolean emailPocs = Arrays.asList(importOption).contains(ImportOption.EMAIL_POCS);
      boolean persist = Arrays.asList(importOption).contains(ImportOption.PERSIST);
      boolean popupReport = Arrays.asList(importOption).contains(ImportOption.POPUP_ERROR_REPORT);
      ImportActionsOperation op = new ImportActionsOperation(file, emailPocs, toGoal, persist, BLAM_NAME);
      XResultData rd = op.run();
      if (rd.isErrors()) {
         if (popupReport) {
            XResultDataUI.report(rd, getName());
         }
      }
      return rd;
   }

   public XResultData importActions(File file, ImportOption... importOption) throws Exception {
      boolean emailPocs = Arrays.asList(importOption).contains(ImportOption.EMAIL_POCS);
      boolean persist = Arrays.asList(importOption).contains(ImportOption.PERSIST);
      boolean popupReport = Arrays.asList(importOption).contains(ImportOption.POPUP_ERROR_REPORT);
      ImportActionsOperation op = new ImportActionsOperation(file, emailPocs, null, persist, BLAM_NAME);
      XResultData rd = op.run();
      if (rd.isErrors()) {
         if (popupReport) {
            XResultDataUI.report(rd, getName());
         }
      }
      return rd;
   }

   public File getSampleSpreadsheetFile() throws Exception {
      return OseeInf.getResourceAsFile("atsImport/Action_Import.xml", getClass());
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_IMPORT);
   }

   @Override
   public String getDescriptionUsage() {
      return "Import Actions via Excel Spreadsheet.\n Open example spreadsheet, " //
         + "save to desktop and edit as desired. Once completed, select file from " //
         + "this BLAM and Run.  Titles that are the same are placed under the same action." //
         + "  After \"Estimated Hours\", remaining columns will attempt to match " //
         + "column name with valid attribute type name add that to Task.";
   }

}
