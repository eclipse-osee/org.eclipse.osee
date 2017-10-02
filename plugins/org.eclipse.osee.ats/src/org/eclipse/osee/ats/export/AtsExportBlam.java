/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.export.AtsExportAction.ExportOption;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */

public class AtsExportBlam extends AbstractBlam {
   public static final String INVALID_DIRECTORY = "Invalid Directory";
   public static final String MUST_SELECT_EXPORT_LOCATION =
      "Must select \"Export Location\" for \"" + ExportOption.AS_HTML_TO_FILE + "\" or \"" + ExportOption.AS_PDF + "\" options.";
   public static final String MUST_SELECT_MERGE_INTO_SINGLE_FILE_OR_SAVE_INTO_SEPARATE_FILES =
      "Must select \"MERGE_INTO_SINGLE_FILE\" or \"SAVE_INTO_SEPARATE_FILES\"";
   public static final String MUST_SELECT_AT_LEAST_ONE_EXPORT_AS_OPTION = "Must select at least one export AS_ option.";
   public static final String NO_ARTIFACTS_SELECTED = "No Artifacts selected.  Cancel wizard and try again.";
   public static final String ARTIFACTS = "Drag in ATS objects to export";
   public static final String EXPORT_LOCATION = "Export Location";
   private final Collection<AbstractWorkflowArtifact> defaultArtifacts;

   public AtsExportBlam(Collection<AbstractWorkflowArtifact> defaultArtifacts) {
      this.defaultArtifacts = defaultArtifacts;
   }

   @Override
   public String getDescriptionUsage() {
      return "Export ATS objects to PDF or HTML.";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"");
      sb.append(ARTIFACTS);
      sb.append("\" />");

      List<ExportOption> validExportOptions = new ArrayList<>();
      validExportOptions.addAll(Arrays.asList(ExportOption.values()));

      for (ExportOption exportOption : validExportOptions) {
         sb.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
         sb.append(exportOption.name());
         sb.append("\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      }

      sb.append("<XWidget xwidgetType=\"XDirectorySelectionDialog\" displayName=\"");
      sb.append(EXPORT_LOCATION);
      sb.append("\" multiSelect=\"false\" />");

      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }

   public Result isEntryValid(VariableMap variableMap) {

      List<Artifact> artifacts = variableMap.getArtifacts(ARTIFACTS);
      if (artifacts == null || artifacts.isEmpty()) {
         return new Result(NO_ARTIFACTS_SELECTED);
      }

      Collection<ExportOption> exportOtions = getExportOptions(variableMap);
      if (!exportOtions.contains(ExportOption.AS_HTML_TO_FILE) && !exportOtions.contains(
         ExportOption.AS_HTML_TO_RESULT_EDITOR) && !exportOtions.contains(ExportOption.AS_PDF)) {
         return new Result(MUST_SELECT_AT_LEAST_ONE_EXPORT_AS_OPTION);
      }
      if (!exportOtions.contains(ExportOption.MERGE_INTO_SINGLE_FILE) && !exportOtions.contains(
         ExportOption.SAVE_INTO_SEPARATE_FILES)) {
         return new Result(MUST_SELECT_MERGE_INTO_SINGLE_FILE_OR_SAVE_INTO_SEPARATE_FILES);
      }
      if (exportOtions.contains(ExportOption.AS_HTML_TO_FILE) || exportOtions.contains(ExportOption.AS_PDF)) {
         String fileSelection = variableMap.getString(EXPORT_LOCATION);
         if (!Strings.isValid(fileSelection)) {
            return new Result(MUST_SELECT_EXPORT_LOCATION);
         }
         if (!new File(fileSelection).isDirectory()) {
            return new Result(INVALID_DIRECTORY);
         }
      }
      return Result.TrueResult;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable)  {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(ARTIFACTS) && defaultArtifacts != null) {
         XListDropViewer viewer = (XListDropViewer) xWidget;
         viewer.setInput(defaultArtifacts);
      }
   }

   public Collection<ExportOption> getExportOptions(VariableMap variableMap) {
      List<ExportOption> options = new ArrayList<>();
      for (ExportOption exportOption : ExportOption.values()) {
         boolean checked = variableMap.getBoolean(exportOption.name());
         if (checked) {
            options.add(exportOption);
         }
      }
      return options;
   }

   @Override
   public IOperation createOperation(final VariableMap variableMap, OperationLogger logger) throws Exception {

      Result result = isEntryValid(variableMap);
      if (result.isFalse()) {
         return null;
      }

      final Collection<ExportOption> exportOptions = getExportOptions(variableMap);
      IOperation export = new AbstractOperation(getName(), Activator.PLUGIN_ID) {

         @Override
         protected void doWork(IProgressMonitor monitor) throws Exception {
            AtsExportAction exporter = new AtsExportAction();
            exporter.export(variableMap.getArtifacts(ARTIFACTS), exportOptions, variableMap.getString(EXPORT_LOCATION));
         }
      };
      return export;
   }

   public static void openAtsExportBlam(Collection<AbstractWorkflowArtifact> defaultArtifacts) {
      try {
         AtsExportBlam blam = new AtsExportBlam(defaultArtifacts);
         BlamEditor.edit(blam);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}