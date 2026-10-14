/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ParagraphNumber;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.ide.traceability.ScriptTraceabilityOperation;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXListArtifactDropWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXBranchAndViewSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class SubsystemFullTraceReportBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Subsystem Full Trace Report";
   private static final String BRANCH_AND_VIEW = "Branch and View";
   static final String SUBSYSTEM_REQUIREMENTS = "Subsystem Requirements";
   private static int SOFTWARE_REQUIREMENT_INDEX = 9;
   private static int TEST_INDEX = 13;

   private CharBackedInputStream charBak;
   private ISheetWriter writer;
   private HashCollectionSet<Artifact, String> requirementsToCodeUnits;
   private final ArrayList<String> tests = new ArrayList<>(50);
   private final String SCRIPT_ROOT_DIR = "Script Root Directory";
   private final String USE_TRACE_IN_OSEE = "Use traceability from Subsystem Requirements";
   private final String USE_GIT_CODE_STRUCTURE = "Use Git Code Structure";
   private XXCheckBoxWidget useTraceInOsee;
   private XXTextWidget scriptDir;
   private Collection<String> availableTraceHandlers;
   private XXStringsWidget branchViewWidget;
   private XXBranchAndViewSelWidget branchAndViewWidget;
   private XXListArtifactDropWidget subSystem;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public SubsystemFullTraceReportBlam() {
      super(BLAM_NAME, CoreImage.TRACE);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andCheckBoxWidget(USE_TRACE_IN_OSEE).andDefault(true);
      wb.andCheckBoxWidget(USE_GIT_CODE_STRUCTURE).andDefault(true);
      wb.andTextWidget(SCRIPT_ROOT_DIR).andDefault("C:/UserData/workspaceScripts").andToolTip(
         "Leave blank if test script traceability is not needed.");
      availableTraceHandlers = new LinkedList<>();
      wb.andLabelWidget("Select appropriate script parser (if script traceability needed):");
      Collection<String> traceHandlerNames = TraceUnitExtensionManager.getInstance().getAllTraceHandlerNames();
      for (String handler : traceHandlerNames) {
         wb.andCheckBoxWidget(handler);
         availableTraceHandlers.add(handler);
      }
      wb.andListDropWidget(SUBSYSTEM_REQUIREMENTS).andSelectable(availableTraceHandlers);
      wb.andBranchAndViewSelWidget(BRANCH_AND_VIEW);
      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(widget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);

      if (widget.getLabel().equals(SCRIPT_ROOT_DIR)) {
         scriptDir = (XXTextWidget) widget;
         scriptDir.setEnabled(false);
         scriptDir.getControl().setBackground(Displays.getSystemColor(SWT.COLOR_GRAY));
      }

      if (widget.getLabel().equals(USE_TRACE_IN_OSEE)) {
         useTraceInOsee = (XXCheckBoxWidget) widget;
         useTraceInOsee.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               if (useTraceInOsee.isChecked()) {
                  scriptDir.setEnabled(false);
                  scriptDir.getControl().setBackground(Displays.getSystemColor(SWT.COLOR_GRAY));
               } else {
                  scriptDir.setEnabled(true);
                  scriptDir.getControl().setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
               }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
               //Do Nothing
            }
         });
      }
      if (widget.getLabel().equals(SUBSYSTEM_REQUIREMENTS)) {
         subSystem = (XXListArtifactDropWidget) widget;
         subSystem.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  List<Artifact> arts = subSystem.getSelectedArtifacts();
                  if (arts != null && !arts.isEmpty()) {
                     BranchId branch = arts.iterator().next().getBranch();
                     if (branch != null && branch.isValid()) {
                        branchViews =
                           ViewApplicabilityUtil.getBranchViews(ViewApplicabilityUtil.getParentBranch(branch));
                        branchViewWidget.setSelectable(branchViews.values());
                     }
                  }
               }
            }
         });
      }

      if (widget.getLabel().equals(BRANCH_AND_VIEW)) {
         branchAndViewWidget = (XXBranchAndViewSelWidget) widget;
      }

   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Artifact> artifacts = variableMap.getArtifacts(SUBSYSTEM_REQUIREMENTS);
      if (artifacts.isEmpty()) {
         throw new OseeArgumentException("must specify a set of artifacts");
      }
      BranchId branch = branchAndViewWidget.getBranch();
      BranchViewToken view = branchAndViewWidget.getSelectedBranchView();
      setViewId(view);

      init();
      String scriptDir = variableMap.getString(SCRIPT_ROOT_DIR);
      Boolean checked = variableMap.getBoolean(USE_TRACE_IN_OSEE);
      Boolean isGitCodeStructure = variableMap.getBoolean(USE_GIT_CODE_STRUCTURE);

      Collection<TraceHandler> traceHandlers = new LinkedList<>();
      for (String handler : availableTraceHandlers) {
         if (variableMap.getBoolean(handler)) {
            TraceHandler traceHandler = TraceUnitExtensionManager.getInstance().getTraceHandlerByName(handler);
            traceHandlers.add(traceHandler);
         }
      }

      if (!checked) {
         File dir = new File(scriptDir);
         if (dir.exists()) {
            ScriptTraceabilityOperation traceOperation = new ScriptTraceabilityOperation(dir.getParentFile(), branch,
               false, traceHandlers, isGitCodeStructure, viewId, false);
            Operations.executeWorkAndCheckStatus(traceOperation, monitor);
            requirementsToCodeUnits = traceOperation.getRequirementToCodeUnitsMap();
         }
      }

      writeMainSheet(prepareSubsystemRequirements(artifacts));

      writer.endWorkbook();
      IFile iFile = OseeData.getIFile(String.format("Subsystem_Trace_Report_%s.xml", Lib.getDateTimeString()));
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private void init() throws IOException {
      charBak = new CharBackedInputStream();
      writer = new ExcelXmlWriter(charBak.getWriter());
   }

   private List<Artifact> prepareSubsystemRequirements(List<Artifact> artifacts) {
      List<Artifact> subsystemRequirements = new ArrayList<>(400);
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(CoreArtifactTypes.Folder)) {
            subsystemRequirements.addAll(artifact.getDescendants());
         } else {
            subsystemRequirements.add(artifact);
         }
      }
      return subsystemRequirements;
   }

   private void writeMainSheet(List<Artifact> artifacts) throws IOException {
      writer.startSheet("report", 18);
      writer.writeRow(CoreArtifactTypes.SystemRequirementMsWord.getName(), null, null,
         CoreArtifactTypes.SubsystemRequirementMsWord.getName(), null, null, null, null, null,
         CoreArtifactTypes.SoftwareRequirementMsWord.getName());
      writer.writeRow("Paragraph #", "Requirement Name", "Requirement Text", "Paragraph #", "Requirement Name",
         "Requirement Text", "Subsystem", CoreAttributeTypes.QualificationMethod.getName(), "Test Procedure",
         "Paragraph #", "Requirement Name", "Partitions", CoreAttributeTypes.QualificationMethod.getName(),
         "Test Script/Test Procedure");

      for (Artifact subSystemRequirement : artifacts) {
         processSubSystemRequirement(subSystemRequirement);
      }
      writer.endSheet();
   }

   private void processSubSystemRequirement(Artifact subSystemRequirement) throws IOException {
      boolean topRowForSubsystemReq = true;
      for (Artifact systemRequirement : subSystemRequirement.getRelatedArtifacts(
         CoreRelationTypes.RequirementTrace_HigherLevelRequirement)) {
         writer.writeCell(systemRequirement.getSoleAttributeValue(ParagraphNumber, ""));
         writer.writeCell(systemRequirement.getName());
         writer.writeCell(getRequirementText(systemRequirement));

         if (topRowForSubsystemReq) {
            writer.writeCell(subSystemRequirement.getSoleAttributeValue(ParagraphNumber, ""));
            writer.writeCell(subSystemRequirement.getName());
            writer.writeCell(getRequirementText(subSystemRequirement));
            writer.writeCell(subSystemRequirement.getSoleAttributeValue(CoreAttributeTypes.Subsystem, ""));
            writer.writeCell(subSystemRequirement.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod));
            writer.writeCell(Collections.toString(",",
               subSystemRequirement.getRelatedArtifacts(CoreRelationTypes.Verification_Verifier)));
            topRowForSubsystemReq = false;
         }

         for (Artifact softwareRequirement : subSystemRequirement.getRelatedArtifacts(
            CoreRelationTypes.RequirementTrace_LowerLevelRequirement)) {
            processSoftwareRequirement(softwareRequirement);
         }
         writer.endRow();
      }
   }

   private String getRequirementText(Artifact req) {
      Attribute<?> templateContent = req.getSoleAttribute(WordTemplateContent);
      String ret = templateContent.getDisplayableString();
      return StringUtils.trim(ret);
   }

   private void processSoftwareRequirement(Artifact softwareRequirement) throws IOException {
      writer.writeCell(softwareRequirement.getSoleAttributeValue(ParagraphNumber, ""), SOFTWARE_REQUIREMENT_INDEX);
      writer.writeCell(softwareRequirement.getName());
      writer.writeCell(
         Collections.toString(",", softwareRequirement.getAttributesToStringList(CoreAttributeTypes.Partition)));
      writer.writeCell(softwareRequirement.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod));

      tests.clear();
      for (Artifact testProcedure : softwareRequirement.getRelatedArtifacts(CoreRelationTypes.Validation_Validator)) {
         tests.add(testProcedure.getName());
      }
      Collection<String> testScripts = null;
      if (requirementsToCodeUnits != null) {
         testScripts = requirementsToCodeUnits.getValues(softwareRequirement);
      } else {
         List<Artifact> relatedArtifacts =
            softwareRequirement.getRelatedArtifacts(CoreRelationTypes.Verification_Verifier);
         testScripts = Named.getNames(relatedArtifacts);
      }
      if (testScripts != null) {
         for (String testScript : testScripts) {
            tests.add(new File(testScript).getName());
         }
      }
      writer.writeCell(Collections.toString(", ", tests), TEST_INDEX);
      writer.endRow();
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
      return "Generates subsystem requirement full traceability report";
   }

}
