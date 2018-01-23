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
package org.eclipse.osee.define.blam.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.skynet.core.utility.ViewIdUtility;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class PublishSystemLevelSSDD extends AbstractBlam {
   private static final String BRANCH = "Branch";
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;
   private List<Artifact> sysReqs;
   private Artifact[] allSubsystems;
   private final HashCollection<Artifact, Artifact> subsystemToRequirements;

   private XCombo branchViewWidget;
   private XBranchSelectWidget branchWidget;

   @Override
   public String getName() {
      return "Publish System Level SSDD";
   }

   public PublishSystemLevelSSDD() {
      subsystemToRequirements = new HashCollection<>();
   }

   private void init() throws IOException {
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      subsystemToRequirements.clear();
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append(branchXWidgetXml);
      builder.append(BRANCH_VIEW_WIDGET);
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Generating System Level SSDD", 100);

      BranchId branch = variableMap.getBranch(BRANCH);
      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);

      init();
      ViewIdUtility.findExcludedArtifactsByView(viewId, branch);

      monitor.subTask("Aquiring System Components"); // bulk load for performance reasons
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.Component, branch);

      monitor.subTask("Aquiring System Requirements");
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SystemRequirementMSWord, branch);

      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      sysReqs = root.getChild(Requirements.SYSTEM_REQUIREMENTS).getDescendants();

      excludeArtifacts(sysReqs.iterator());
      getSubsystemList();

      monitor.subTask("5.2 System Requirement Allocation To Subsystems");
      writeSystemRequirementAllocationToSubsystems();
      for (Artifact subsystem : allSubsystems) {
         writeSystemRequirementAllocationBySubsystem(subsystem);
      }

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("SystemLevelSSDD_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void getSubsystemList() {
      for (Artifact systemRequirement : sysReqs) {
         List<Artifact> relatedArtifacts =
            systemRequirement.getRelatedArtifacts(CoreRelationTypes.Allocation__Component);
         excludeArtifacts(relatedArtifacts.iterator());
         for (Artifact subsystem : relatedArtifacts) {
            subsystemToRequirements.put(subsystem, systemRequirement);
         }
      }
      Set<Artifact> subsystemsSet = subsystemToRequirements.keySet();
      allSubsystems = new Artifact[subsystemsSet.size()];
      subsystemsSet.toArray(allSubsystems);
      Arrays.sort(allSubsystems);
   }

   private void writeSystemRequirementAllocationToSubsystems() throws IOException {
      excelWriter.startSheet("5.2", 200);

      excelWriter.writeCell(CoreAttributeTypes.ParagraphNumber.getName());
      excelWriter.writeCell(CoreArtifactTypes.Component.getName());
      for (Artifact subsystem : allSubsystems) {
         excelWriter.writeCell(subsystem.getName());
      }
      excelWriter.endRow();

      excelWriter.writeRow("5.2 System Requirement Allocation to Subsystems");
      for (Artifact systemRequirement : sysReqs) {
         writeSystemRequirementAllocation(systemRequirement);
      }
      excelWriter.endSheet();
   }

   private void writeSystemRequirementAllocationBySubsystem(Artifact subsystem) throws IOException {
      excelWriter.startSheet(subsystem.getName(), 200);
      excelWriter.writeRow("System Requirements Allocated to the " + subsystem.getName());
      excelWriter.writeRow(CoreAttributeTypes.ParagraphNumber.getName(),
         CoreArtifactTypes.SystemRequirementMSWord.getName(), "Notes <rationale>");

      for (Artifact systemRequirement : subsystemToRequirements.getValues(subsystem)) {
         List<Artifact> relatedArtifacts =
            systemRequirement.getRelatedArtifacts(CoreRelationTypes.Allocation__Component);

         excludeArtifacts(relatedArtifacts.iterator());

         for (Artifact component : relatedArtifacts) {
            if (component.equals(subsystem)) {
               String rationale =
                  systemRequirement.getRelationRationale(component, CoreRelationTypes.Allocation__Component);
               if (rationale.equals("")) {
                  rationale = null;
               }
               excelWriter.writeRow(systemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""),
                  systemRequirement.getName(), rationale);
            }
         }
      }

      excelWriter.endSheet();
   }

   private void writeSystemRequirementAllocation(Artifact systemRequirement) throws IOException {
      excelWriter.writeCell(systemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
      excelWriter.writeCell(systemRequirement.getName());

      List<Artifact> relatedArtifacts = systemRequirement.getRelatedArtifacts(CoreRelationTypes.Allocation__Component);
      excludeArtifacts(relatedArtifacts.iterator());

      List<Artifact> allocatedSubsystems = new ArrayList<Artifact>(relatedArtifacts);
      Collections.sort(allocatedSubsystems);
      for (Artifact allocatedSubsystem : allocatedSubsystems) {
         excelWriter.writeCell("X", Arrays.binarySearch(allSubsystems, allocatedSubsystem) + 2);
      }
      excelWriter.endRow();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(BRANCH)) {
         branchWidget = (XBranchSelectWidget) xWidget;
         branchWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  BranchId branch = branchWidget.getSelection();
                  if (branch != null && branch.isValid()) {
                     branchViews = ViewApplicabilityUtil.getBranchViews(ViewApplicabilityUtil.getParentBranch(branch));
                     branchViewWidget.setDataStrings(branchViews.values());
                  }
               }
            }
         });
      } else if (xWidget.getLabel().equals(BRANCH_VIEW)) {
         branchViewWidget = (XCombo) xWidget;
         branchViewWidget.setEditable(false);
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "Generate Traceability tables for the System Level SSDD";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }
}