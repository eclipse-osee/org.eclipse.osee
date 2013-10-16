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
package org.eclipse.osee.define.traceability.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.define.traceability.BranchTraceabilityOperation;
import org.eclipse.osee.define.traceability.RequirementTraceabilityData;
import org.eclipse.osee.define.traceability.ScriptTraceabilityOperation;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.define.traceability.TraceabilityFactory;
import org.eclipse.osee.define.traceability.TraceabilityFactory.OutputFormat;
import org.eclipse.osee.define.traceability.TraceabilityFactory.TraceabilityStyle;
import org.eclipse.osee.define.traceability.TraceabilityProviderOperation;
import org.eclipse.osee.define.traceability.TraceabilityTable;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Roberto E. Escobar
 */
public class PublishStdStpTraceability extends AbstractBlam {

   private static final String scriptDirectory =
      "<XWidget xwidgetType=\"XText\" displayName=\"Script Root Directory\" defaultValue=\"C:/UserData/workspaceScripts\"/>";
   private static final String requirementsBranch =
      "<XWidget xwidgetType=\"XBranchSelectWidget\" " + "displayName=\"Requirements Branch\" defaultValue=\"\" toolTip=\"Select a requirements branch.\" />";
   private static final String testProceduresBranch =
      "<XWidget xwidgetType=\"XBranchSelectWidget\" " + "displayName=\"Test Procedures Branch\" defaultValue=\"\" toolTip=\"Select a test procedures branch.\" />";
   private static final String artifactTypeChooser =
      "<XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" displayName=\"Artifact Type(s) to Trace\" defaultValue=\"" + CoreArtifactTypes.AbstractSoftwareRequirement + "\"/>";
   private static final String searchInheritedTypes =
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Search Inherited Types\" labelAfter=\"true\" horizontalLabel=\"true\" defaultValue=\"true\" />";

   private static final String PULL_ALL_DATA_LABEL =
      "Pull all data from test branch (script directory and requirements branch are not needed)";
   private static final String pullAllDataFromBranch =
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + PULL_ALL_DATA_LABEL + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>";

   private static final String TRACE_HANDLER_CHECKBOX =
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\"/>";
   private Collection<String> availableTraceHandlers;

   @Override
   public String getName() {
      return "Publish STD/STP Traceability";
   }

   @Override
   public String getXWidgetsXml() throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select tables to generate:\"/>");
      for (TraceabilityStyle style : TraceabilityStyle.values()) {
         builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
         builder.append(style.asLabel());
         builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      }
      builder.append(scriptDirectory);

      availableTraceHandlers = new LinkedList<String>();
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select appropriate script parser:\" />");
      Collection<String> traceHandlers = TraceUnitExtensionManager.getInstance().getAllTraceHandlerNames();
      for (String handler : traceHandlers) {
         builder.append(String.format(TRACE_HANDLER_CHECKBOX, handler));
         availableTraceHandlers.add(handler);
      }

      builder.append(requirementsBranch);
      builder.append(pullAllDataFromBranch);
      builder.append(testProceduresBranch);
      builder.append(artifactTypeChooser);
      builder.append(searchInheritedTypes);
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private List<TraceabilityStyle> getStyles(VariableMap variableMap) throws OseeArgumentException {
      List<TraceabilityStyle> styles = new ArrayList<TraceabilityStyle>();
      for (TraceabilityStyle style : TraceabilityStyle.values()) {
         boolean isSelected = variableMap.getBoolean(style.asLabel());
         if (isSelected) {
            styles.add(style);
         }
      }
      return styles;
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Branch requirementsBranch = variableMap.getBranch("Requirements Branch");
      Branch testProcedureBranch = variableMap.getBranch("Test Procedures Branch");
      Collection<? extends IArtifactType> types =
         variableMap.getCollection(ArtifactType.class, "Artifact Type(s) to Trace");
      boolean searchInherited = variableMap.getBoolean("Search Inherited Types");
      File scriptDir = new File(variableMap.getString("Script Root Directory"));
      List<TraceabilityStyle> selectedReports = getStyles(variableMap);

      Collection<TraceHandler> traceHandlers = new LinkedList<TraceHandler>();
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
         if (variableMap.getBoolean(PULL_ALL_DATA_LABEL)) {
            provider = new BranchTraceabilityOperation(testProcedureBranch, types, searchInherited);
         } else {
            provider =
               new ScriptTraceabilityOperation(scriptDir, requirementsBranch, false, types, searchInherited,
                  traceHandlers);
         }
         RequirementTraceabilityData traceabilityData = new RequirementTraceabilityData(testProcedureBranch, provider);
         IStatus status = traceabilityData.initialize(monitor);
         if (status.getSeverity() == IStatus.CANCEL) {
            monitor.setCanceled(true);
         } else if (status.getSeverity() == IStatus.OK) {
            monitor.worked(1);
            int count = 0;
            List<IFile> files = new ArrayList<IFile>();
            for (TraceabilityStyle style : selectedReports) {
               monitor.subTask(String.format("Creating table: [%s] [%s of %s]", style.asLabel(), ++count,
                  selectedReports.size()));

               TraceabilityTable table =
                  TraceabilityFactory.getTraceabilityTable(OutputFormat.EXCEL, style, traceabilityData);
               if (table != null) {
                  table.run(monitor);
               }
               monitor.worked(1);

               if (table != null) {
                  monitor.subTask(String.format("Writing table: [%s] [%s of %s]", style.asLabel(), count,
                     selectedReports.size()));
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

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }

}
