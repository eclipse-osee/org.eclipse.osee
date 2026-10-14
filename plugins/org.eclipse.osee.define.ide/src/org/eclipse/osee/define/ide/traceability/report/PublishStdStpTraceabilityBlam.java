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

package org.eclipse.osee.define.ide.traceability.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.define.ide.traceability.BranchTraceabilityOperation;
import org.eclipse.osee.define.ide.traceability.RequirementTraceabilityData;
import org.eclipse.osee.define.ide.traceability.ScriptTraceabilityOperation;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.define.ide.traceability.TraceabilityFactory;
import org.eclipse.osee.define.ide.traceability.TraceabilityFactory.TraceabilityStyle;
import org.eclipse.osee.define.ide.traceability.TraceabilityProviderOperation;
import org.eclipse.osee.define.ide.traceability.TraceabilityTable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;
import org.osgi.service.component.annotations.Component;

/**
 * BLAMTESTED
 *
 * @author Roberto E. Escobar
 */
@Component(service = AbstractBlam.class, immediate = true)
public class PublishStdStpTraceabilityBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Publish STD/STP Traceability";
   static final String SEARCH_INHERITED_TYPES = "Search Inherited Types";
   static final String SCRIPT_ROOT_DIRECTORY = "Script Root Directory";
   static final String SEARCH_GIT_CODE_STRUCTURE = "Search Git Code Structure";
   static final String INCLUDE_IMPD = "Include IMPD";
   private static final String FOR_TRACEABILITY_STORED_IN_TEST_SCRIPTS_SELECT_THE_FOLLOWING =
      "===  For traceability stored in test scripts, select the following  ===";
   static final String PROGRAM_BRANCH = "Program Branch";

   private Collection<String> availableTraceHandlers;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public PublishStdStpTraceabilityBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   public PublishStdStpTraceabilityBlam(String name) {
      this(name, CoreImage.TRACE);
   }

   public PublishStdStpTraceabilityBlam(String name, OseeImage image) {
      super(name, image);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andLabelWidget(FOR_TRACEABILITY_STORED_IN_TEST_SCRIPTS_SELECT_THE_FOLLOWING);
      wb.andBranchAndViewSelWidget(PROGRAM_BRANCH);

      wb.andLabelWidget("Select tables to generate:");
      for (TraceabilityStyle style : TraceabilityStyle.values()) {
         wb.andCheckBoxWidget(style.asLabel());
      }

      wb.andWidget("Artifact Type(s) to Trace", WidgetId.XXArtifactTypeWidget).andDefault(
         CoreArtifactTypes.SoftwareRequirementMsWord);
      wb.andCheckBoxWidget(SEARCH_INHERITED_TYPES);
      wb.andLabelWidget(FOR_TRACEABILITY_STORED_IN_TEST_SCRIPTS_SELECT_THE_FOLLOWING);
      wb.andCheckBoxWidget(SEARCH_GIT_CODE_STRUCTURE);
      wb.andLabelWidget("===  To include IMPD, select the following  ===");
      wb.andCheckBoxWidget(INCLUDE_IMPD);

      wb.andLabelWidget("Select appropriate script parser:");
      Collection<String> traceHandlers = TraceUnitExtensionManager.getInstance().getAllTraceHandlerNames();
      availableTraceHandlers = new LinkedList<>();
      for (String handler : traceHandlers) {
         wb.andCheckBoxWidget(handler);
         availableTraceHandlers.add(handler);
      }

      wb.andTextWidget(SCRIPT_ROOT_DIRECTORY).andDefault("C:/UserData/workspaceScripts");
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      BranchId requirementsBranch = variableMap.getBranch();
      if (requirementsBranch.isInvalid()) {
         log("Select Branch\n");
         return;
      }
      BranchViewToken view = variableMap.getBranchView();
      if (view.isInvalid()) {
         log("Select Branch View\n");
         return;
      }
      setViewId(view);

      Collection<ArtifactTypeToken> types =
         variableMap.getCollection(ArtifactTypeToken.class, "Artifact Type(s) to Trace");
      boolean searchInherited = variableMap.getBoolean(SEARCH_INHERITED_TYPES);
      File scriptDir = new File(variableMap.getString(SCRIPT_ROOT_DIRECTORY));
      List<TraceabilityStyle> selectedReports = getStyles(variableMap);

      Collection<TraceHandler> traceHandlers = new LinkedList<>();
      for (String handler : availableTraceHandlers) {
         if (variableMap.getBoolean(handler)) {
            TraceHandler traceHandler = TraceUnitExtensionManager.getInstance().getTraceHandlerByName(handler);
            traceHandlers.add(traceHandler);
         }
      }

      int totalWork = selectedReports.size() * 2 + 1;

      monitor.beginTask("Generate Traceability Tables", totalWork);

      if (selectedReports.size() > 0) {
         // Load Requirements Data
         TraceabilityProviderOperation provider;
         if (traceHandlers.isEmpty()) {
            provider = new BranchTraceabilityOperation(requirementsBranch, types, searchInherited, viewId);
         } else {
            boolean isGitBased = variableMap.getBoolean(SEARCH_GIT_CODE_STRUCTURE);
            boolean includeImpd = variableMap.getBoolean(INCLUDE_IMPD);
            provider = new ScriptTraceabilityOperation(scriptDir, requirementsBranch, false, types, searchInherited,
               traceHandlers, isGitBased, viewId, includeImpd);
         }
         RequirementTraceabilityData traceabilityData =
            new RequirementTraceabilityData(requirementsBranch, provider, viewId);
         IStatus status = traceabilityData.initialize(monitor);
         if (status.getSeverity() == IStatus.CANCEL) {
            monitor.setCanceled(true);
         } else if (status.getSeverity() == IStatus.OK) {
            monitor.worked(1);
            int count = 0;
            List<IFile> files = new ArrayList<>();
            for (TraceabilityStyle style : selectedReports) {
               monitor.subTask(
                  String.format("Creating table: [%s] [%s of %s]", style.asLabel(), ++count, selectedReports.size()));

               TraceabilityTable table = TraceabilityFactory.getTraceabilityTable(style, traceabilityData);
               if (table != null) {
                  table.run(monitor);
               }
               monitor.worked(1);

               if (table != null) {
                  monitor.subTask(
                     String.format("Writing table: [%s] [%s of %s]", style.asLabel(), count, selectedReports.size()));
                  String fileName = style.toString() + "." + Lib.getDateTimeString() + ".xml";
                  IFile iFile = OseeData.getIFile(fileName);
                  AIFile.writeToFile(iFile, table.toString());
                  files.add(iFile);
               }
               monitor.worked(1);
            }
            for (IFile iFile : files) {
               Program.launch(iFile.getLocation().toOSString());
            }
         } else {
            throw new Exception(status.getMessage(), status.getException());
         }
      }
      monitor.subTask("Done");
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private List<TraceabilityStyle> getStyles(VariableMap variableMap) {
      List<TraceabilityStyle> styles = new ArrayList<>();
      for (TraceabilityStyle style : TraceabilityStyle.values()) {
         boolean isSelected = variableMap.getBoolean(style.asLabel());
         if (isSelected) {
            styles.add(style);
         }
      }
      return styles;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.TRACE);
   }

   @Override
   public String getDescriptionUsage() {
      StringBuilder sb = new StringBuilder();
      sb.append("<form>This BLAM can be ran where test traceability is either stored in OSEE");
      sb.append(" via relations OR by parsing test scripts for embedded tracemarks.<br/>");
      sb.append("<li>If a script parser is not selected, BLAM will assume test traceability is stored in OSEE</li>");
      sb.append("<br/>Click the play button at the top right or in the Execute section.</form>");
      return sb.toString();
   }

}
