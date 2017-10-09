/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.skynet.core.utility.ViewIdUtility;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class SubsystemToLowLevelReqTraceReport extends AbstractBlam {
   private static final String LOW_LEVEL_REQUIREMENTS = "Lower Level Requirements";
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;
   private final HashMap<String, List<Artifact>> subsysToSubsysReqsMap;
   private final List<Artifact> lowLevelReqs;
   private final HashSet<Artifact> components;
   private Collection<ArtifactType> lowerLevelTypes;

   private XCombo branchViewWidget;
   private XListDropViewer lowerLevel;

   @Override
   public String getName() {
      return "Subsystem To Low Level Req Trace Report";
   }

   public SubsystemToLowLevelReqTraceReport() {
      subsysToSubsysReqsMap = new HashMap<>();
      components = new HashSet<>();
      lowLevelReqs = new ArrayList<>(1000);
   }

   private void init() throws IOException {
      subsysToSubsysReqsMap.clear();
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      components.clear();
      lowLevelReqs.clear();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Generate Report", 100);

      init();
      lowerLevelTypes = variableMap.getCollection(ArtifactType.class, "Low Level Requirement Type(s)");

      initLowLevelRequirements(variableMap.getArtifacts("Lower Level Requirements"));
      initAllocationComponents(variableMap.getArtifacts("Allocation Components"));

      BranchId branch = lowLevelReqs.get(0).getBranch();

      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);

      monitor.subTask("Loading Higher Level Requirements"); // bulk load to improve performance
      monitor.worked(1);
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemRequirementMSWord, branch);
      monitor.worked(30);

      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      orderSubsystemReqs(root.getChild(Requirements.SUBSYSTEM_REQUIREMENTS));

      generateLowLevelToSubsystemTrace();
      generateSubsystemToLowLevelReqTrace();

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("Subsystem_To_Lower_Level_Trace_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void generateLowLevelToSubsystemTrace() throws IOException, OseeCoreException {
      excelWriter.startSheet("5.1", 7);

      excelWriter.writeRow("5.1  Lower Level Requirements Traceability to Subsystem Requirements");
      excelWriter.writeRow("Lower Level Requirements", null, null, "Traceable Subsystem Requirement");
      excelWriter.writeRow("Paragraph #", "Paragraph Title", "Qualification Method", "PIDS", "Paragraph #",
         "Paragraph Title", CoreAttributeTypes.Subsystem.getName());

      String[] row = new String[7];

      for (Artifact lowLevelReq : lowLevelReqs) {
         row[0] = correct(lowLevelReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
         row[1] = lowLevelReq.getName();
         boolean isRelated = false;
         if (isLowerLevelRequirement(lowLevelReq)) {
            row[2] = lowLevelReq.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod);

            List<Artifact> relatedArtifacts =
               lowLevelReq.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Higher_Level);
            if (relatedArtifacts != null && !relatedArtifacts.isEmpty()) {
               ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), excludedArtifactIdMap);
               isRelated = true;
            }
            for (Artifact subSysReq : relatedArtifacts) {
               row[3] = getAssociatedSubSystem(subSysReq);
               row[4] = correct(subSysReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
               row[5] = subSysReq.getName();
               row[6] = subSysReq.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
               excelWriter.writeRow(row);
            }
         } else {
            row[2] = lowLevelReq.getArtifactTypeName();
         }

         if (row[0] != null && !isRelated) { // if this requirement is not traced to any lower level req (i.e. the for loop didn't run)
            row[3] = row[4] = row[5] = row[6] = null;
            excelWriter.writeRow(row);
         }
      }

      excelWriter.endSheet();
   }

   private boolean isLowerLevelRequirement(Artifact artifact) {
      for (ArtifactType artifactType : lowerLevelTypes) {
         if (artifact.isOfType(artifactType)) {
            return true;
         }
      }
      return false;
   }

   private void generateSubsystemToLowLevelReqTrace() throws IOException, OseeCoreException {
      excelWriter.startSheet("5.2", 6);

      excelWriter.writeRow("5.2  Subsystem Requirements Allocation Traceability to Lower Level Requirements");
      excelWriter.writeRow();

      int count = 1;
      for (Entry<String, List<Artifact>> entry : subsysToSubsysReqsMap.entrySet()) {
         String subSysName = entry.getKey();
         List<Artifact> subsysReqs = entry.getValue();

         excelWriter.writeRow();
         excelWriter.writeRow();
         excelWriter.writeRow(
            "5.2." + count++ + " " + subSysName + " Requirements Allocation Traceability to Lower Level Requirements");
         excelWriter.writeRow(CoreArtifactTypes.SubsystemRequirementMSWord.getName(), null,
            "Traceable Lower Level Requirements", null);
         excelWriter.writeRow("Paragraph #", "Paragraph Title", "Paragraph #", "Paragraph Title");

         String[] row = new String[4];

         for (Artifact higherLevelReq : subsysReqs) {
            if (isAllocated(higherLevelReq)) {
               row[0] = correct(higherLevelReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
               row[1] = higherLevelReq.getName();
               List<Artifact> relatedArtifacts =
                  higherLevelReq.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Lower_Level);
               boolean isRelated = false;
               if (relatedArtifacts != null && !relatedArtifacts.isEmpty()) {
                  ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), excludedArtifactIdMap);
                  isRelated = true;
               }
               for (Artifact lowerLevelReq : relatedArtifacts) {
                  if (lowLevelReqs.contains(lowerLevelReq)) {
                     row[2] = correct(lowerLevelReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
                     row[3] = lowerLevelReq.getName();
                     excelWriter.writeRow(row);
                  }
               }
               if (row[0] != null && !isRelated) { // if this requirement is not traced to any low level requirement(i.e. the for loop didn't run)
                  row[2] = row[3] = null;
                  excelWriter.writeRow(row);
               }
            }
         }
      }
      excelWriter.endSheet();
   }

   private boolean isAllocated(Artifact higherLevelReq) throws OseeCoreException {
      for (Artifact component : higherLevelReq.getRelatedArtifacts(CoreRelationTypes.Allocation__Component)) {
         if (components.contains(component)) {
            return true;
         }
      }
      return false;
   }

   private void initLowLevelRequirements(List<Artifact> artifacts) throws OseeCoreException {
      RelationManager.getRelatedArtifacts(artifacts, 999, INCLUDE_DELETED,
         CoreRelationTypes.Default_Hierarchical__Child);
      for (Artifact artifact : artifacts) {
         if (!artifact.isOfType(CoreArtifactTypes.Folder, CoreArtifactTypes.ImplementationDetails)) {
            lowLevelReqs.add(artifact);
         }
         for (Artifact descendant : artifact.getDescendants()) {
            if (!descendant.isOfType(CoreArtifactTypes.Folder, CoreArtifactTypes.ImplementationDetails)) {
               lowLevelReqs.add(descendant);
            }
         }
      }
      ViewIdUtility.removeExcludedArtifacts(lowLevelReqs.iterator(), excludedArtifactIdMap);
   }

   private void initAllocationComponents(List<Artifact> artifacts) throws OseeCoreException {
      RelationManager.getRelatedArtifacts(artifacts, 999, INCLUDE_DELETED,
         CoreRelationTypes.Default_Hierarchical__Child);
      for (Artifact artifact : artifacts) {
         if (!artifact.isOfType(CoreArtifactTypes.Folder, CoreArtifactTypes.ImplementationDetails)) {
            components.add(artifact);
         }
         for (Artifact descendant : artifact.getDescendants()) {
            if (!descendant.isOfType(CoreArtifactTypes.Folder, CoreArtifactTypes.ImplementationDetails)) {
               components.add(descendant);
            }
         }
      }
      ViewIdUtility.removeExcludedArtifacts(components.iterator(), excludedArtifactIdMap);
   }

   private String correct(String value) {
      return value.equals("") ? null : value;
   }

   private String getAssociatedSubSystem(Artifact subSysReq) {
      for (Entry<String, List<Artifact>> entry : subsysToSubsysReqsMap.entrySet()) {
         String subSysName = entry.getKey();
         List<Artifact> subsysReqs = entry.getValue();
         if (subsysReqs.contains(subSysReq)) {
            return subSysName;
         }
      }
      return "N/A";
   }

   private void orderSubsystemReqs(Artifact subsysTopFolder) throws OseeCoreException {
      List<Artifact> children = subsysTopFolder.getChildren();
      if (children != null) {
         ViewIdUtility.removeExcludedArtifacts(children.iterator(), excludedArtifactIdMap);
      }
      for (Artifact subsysFolder : subsysTopFolder.getChildren()) {
         String subSysName = subsysFolder.getName();
         List<Artifact> subsysReqs = subsysFolder.getDescendants();
         subsysToSubsysReqsMap.put(subSysName, subsysReqs);
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Lower Level Requirements\" />" + //
         "<XWidget xwidgetType=\"XListDropViewer\" displayName=\"Allocation Components\" />" + //
         "<XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" displayName=\"Low Level Requirement Type(s)\" multiSelect=\"true\" />" + //
         "<XWidget xwidgetType=\"XCombo()\" displayName=\"Branch View\" horizontalLabel=\"true\"/>" + //
         "</xWidgets>";
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(LOW_LEVEL_REQUIREMENTS)) {
         lowerLevel = (XListDropViewer) xWidget;
         lowerLevel.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  List<Artifact> arts = lowerLevel.getArtifacts();
                  if (arts != null && !arts.isEmpty()) {
                     BranchId branch = arts.iterator().next().getBranch();
                     if (branch != null && branch.isValid()) {
                        branchViews =
                           ViewApplicabilityUtil.getBranchViews(ViewApplicabilityUtil.getParentBranch(branch));
                        branchViewWidget.setDataStrings(branchViews.values());
                     }
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
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }

   @Override
   public String getDescriptionUsage() {
      return "The Low Level Requirement artifacts will be filtered based on the type(s) selected.  E.g. if you want all kinds of software requirements included, then select \"Abstract Software Requirement\".";
   }
}