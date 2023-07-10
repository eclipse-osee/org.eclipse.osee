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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ImportActionsViaSpreadsheetBlam extends AbstractBlam {

   public static final String NAME = "Import Actions Via Spreadsheet";

   public static enum ImportOption {
      EMAIL_POCS,
      POPUP_ERROR_REPORT,
      PERSIST,
      NONE
   }

   @Override
   public String getName() {
      return NAME;
   }

   protected boolean includeGoalWidget() {
      return true;
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      XWidgetBuilder wb = new XWidgetBuilder();
      wb.andWidget("Excel Spreadsheet saved as xml", "XFileTextWithSelectionDialog").endWidget();
      wb.andXCheckbox("Email POCs");
      wb.andXCheckbox("Persist");
      wb.andWidget("Add to Goal", "XGoalCombo").andHorizLabel().andLabelAfter().endWidget();
      wb.andXButtonPush("Open Excel Import Example Spreadsheet").andDisplayLabel(false).endWidget();
      return wb.getItems();
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
      ImportActionsOperation op = new ImportActionsOperation(file, emailPocs, toGoal, persist, NAME);
      XResultData rd = op.run();
      if (rd.isErrors()) {
         if (popupReport) {
            XResultDataUI.report(rd, getName());
         }
      }
      return rd;
   }

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
