/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.WordUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
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
public class SystemSubsystemReport extends AbstractBlam {
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;
   private int subsysDescendantCount;
   private int subsysMarkedCount;
   private int subsysMarkedAndTracedCount;
   private int subsysMarkedAndQualifiedCount;
   private int subsysMarkedAndAllocatedToComponentCount;
   private final HashMap<String, Set<Artifact>> subsysToSubsysReqsMap;
   private final HashMap<String, Set<Artifact>> subsysToSysReqsMap;
   private final LinkedHashSet<Artifact> components;
   private List<Artifact> sysReqs;
   Set<ArtifactId> findExcludedArtifactsByView;

   private XCombo branchViewWidget;
   private XBranchSelectWidget viewerWidget;

   @Override
   public String getName() {
      return "System Subsystem Report";
   }

   private static enum SubsystemCompletness {
      paragraphNumber,
      name,
      subSys,
      qualMethod,
      highLevelTrace,
      allocated
   }
   private static final int COMP_ENUM_COUNT = SubsystemCompletness.values().length;

   public SystemSubsystemReport() {
      subsysToSubsysReqsMap = new HashMap<>();
      subsysToSysReqsMap = new HashMap<>();
      components = new LinkedHashSet<>(250);
      findExcludedArtifactsByView = new HashSet<>();
   }

   private void init() throws IOException {
      subsysDescendantCount = 0;
      subsysMarkedCount = 0;
      subsysMarkedAndTracedCount = 0;
      subsysMarkedAndQualifiedCount = 0;
      subsysMarkedAndAllocatedToComponentCount = 0;
      subsysToSubsysReqsMap.clear();
      subsysToSysReqsMap.clear();
      components.clear();
      findExcludedArtifactsByView.clear();
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Generating Reports", 100);

      BranchId branch = variableMap.getBranch("Branch");

      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);

      init();

      findExcludedArtifactsByView = ViewIdUtility.findExcludedArtifactsByView(viewId, branch);

      monitor.subTask("Aquiring System Components"); // bulk load for performance reasons
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.Component, branch);

      monitor.subTask("Aquiring System Requirements");
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SystemRequirementMSWord, branch);

      monitor.subTask("Aquiring Subsystem Requirements"); // bulk load for performance reasons
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemRequirementMSWord, branch);

      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      Artifact subsysTopFolder = root.getChild(Requirements.SUBSYSTEM_REQUIREMENTS);

      sysReqs = root.getChild(Requirements.SYSTEM_REQUIREMENTS).getDescendants();

      if (sysReqs != null && !sysReqs.isEmpty()) {
         ViewIdUtility.removeExcludedArtifacts(sysReqs.iterator(), findExcludedArtifactsByView);
      }

      monitor.subTask("Generating Metrics");

      generateMetrics(getProductComponent(root), subsysTopFolder);

      monitor.subTask("Generating Per Subsystem Tables");

      generatePerSubsystemTables();

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("System_Subsystem_Report_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void storeInHierarchyOrderBySubsystem(String subSysName, List<Artifact> sysReqByComp) {
      Set<Artifact> orderedSysReqs = new LinkedHashSet<>(sysReqByComp.size());
      for (Artifact sysReq : sysReqs) {
         if (sysReqByComp.contains(sysReq)) {
            orderedSysReqs.add(sysReq);
         }
      }
      subsysToSysReqsMap.put(subSysName, orderedSysReqs);
   }

   private void generateMetrics(Artifact productComponent, Artifact subsysTopFolder) throws IOException, OseeCoreException {
      excelWriter.startSheet("Metrics", 8);

      String[] row = new String[] {
         "Subsystem Name",
         "# of allocated Sys Req",
         "# of Subsys Descendants",
         "# of Subsys Req Marked",
         "# of Subsys Req Traceable to Sys Req",
         "# of Subsys Req with Qual Method Defined",
         "# of Subsys Req allocated to HW/SW Components",
         "Req GUID not allocated"};
      excelWriter.writeRow(row);

      CountingMap<Artifact> allocatedSysReqCounter = new CountingMap<>(sysReqs.size());

      List<Artifact> children = subsysTopFolder.getChildren();
      if (children != null && !children.isEmpty()) {
         ViewIdUtility.removeExcludedArtifacts(children.iterator(), findExcludedArtifactsByView);
      }
      for (Artifact subsysFolder : children) {
         resetCounters();
         String subSysName = subsysFolder.getName();
         row[0] = subSysName;

         Artifact component = productComponent.getChild(subSysName);
         List<Artifact> sysReqByComp = component.getRelatedArtifacts(CoreRelationTypes.Allocation__Requirement);
         if (sysReqByComp != null && !sysReqByComp.isEmpty()) {
            ViewIdUtility.removeExcludedArtifacts(sysReqByComp.iterator(), findExcludedArtifactsByView);
         }
         storeInHierarchyOrderBySubsystem(subSysName, sysReqByComp);
         allocatedSysReqCounter.put(sysReqByComp);
         Set<String> missingAllocationGuids = new LinkedHashSet<>();

         recurseWholeSubsystem(subSysName, subsysFolder, missingAllocationGuids);

         row[1] = String.valueOf(sysReqByComp.size());
         row[2] = String.valueOf(subsysDescendantCount);
         row[3] = String.valueOf(subsysMarkedCount);
         row[4] = String.valueOf(subsysMarkedAndTracedCount);
         row[5] = String.valueOf(subsysMarkedAndQualifiedCount);
         row[6] = String.valueOf(subsysMarkedAndAllocatedToComponentCount);
         row[7] = Collections.toString(", ", missingAllocationGuids);

         excelWriter.writeRow(row);
      }

      int exactlyOnceCount = 0;
      int moreThanOnceCount = 0;
      for (Entry<Artifact, MutableInteger> entry : allocatedSysReqCounter.getCounts()) {
         int count = entry.getValue().getValue();
         if (count == 1) {
            exactlyOnceCount++;
         } else {
            moreThanOnceCount++;
         }
      }

      excelWriter.writeRow();
      excelWriter.writeRow();
      excelWriter.writeRow("Total # of system requirements", String.valueOf(sysReqs.size()));
      excelWriter.writeRow("# of system requirements alloacted exactly once", String.valueOf(exactlyOnceCount));
      excelWriter.writeRow("# of system requirements alloacted more than once", String.valueOf(moreThanOnceCount));
      excelWriter.writeRow("# of system requirements not alloacted",
         String.valueOf(sysReqs.size() - moreThanOnceCount - exactlyOnceCount));

      excelWriter.endSheet();
   }

   private void generatePerSubsystemTables() throws IOException, OseeCoreException {
      for (Entry<String, Set<Artifact>> entry : subsysToSubsysReqsMap.entrySet()) {
         String subSysName = entry.getKey();
         Set<Artifact> subsysReqs = entry.getValue();

         generateSubsystemRaw(subSysName, subsysReqs);

         generateSubsystemComponentAllocation(subSysName, subsysReqs);

         generateComponentAllocation(subSysName, subsysReqs);

         generateSystemToSubsystemTrace(subSysName);

         generateSubsystemToSystemTrace(subSysName);
      }
   }

   private void generateSubsystemRaw(String subSysName, Set<Artifact> subsysReqs) throws IOException, OseeCoreException {
      excelWriter.startSheet(getShortSheetName(subSysName, "Comp"), 6);

      excelWriter.writeRow("Detailed Subsystem Requirement Completeness Report for " + subSysName);
      excelWriter.writeRow("Paragraph #", "Paragraph Title", CoreAttributeTypes.Subsystem.getName(),
         CoreAttributeTypes.QualificationMethod.getName(), "Trace Count", "Allocation Count");

      String[] row = new String[COMP_ENUM_COUNT];
      for (Artifact artifact : subsysReqs) {
         row[SubsystemCompletness.paragraphNumber.ordinal()] =
            artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         row[SubsystemCompletness.name.ordinal()] = artifact.getName();

         row[SubsystemCompletness.subSys.ordinal()] = artifact.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");

         if (artifact.isOfType(CoreArtifactTypes.SubsystemRequirementMSWord)) {
            row[SubsystemCompletness.qualMethod.ordinal()] =
               artifact.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod);
         } else {
            row[SubsystemCompletness.qualMethod.ordinal()] = "N/A: " + artifact.getArtifactTypeName();
         }

         int higherTraceCount = artifact.getRelatedArtifactsCount(CoreRelationTypes.Requirement_Trace__Higher_Level);
         row[SubsystemCompletness.highLevelTrace.ordinal()] = String.valueOf(higherTraceCount);

         int allocationCount = artifact.getRelatedArtifactsCount(CoreRelationTypes.Allocation__Component);
         row[SubsystemCompletness.allocated.ordinal()] = String.valueOf(allocationCount);
         excelWriter.writeRow(row);
      }

      excelWriter.endSheet();
   }

   private void generateSubsystemComponentAllocation(String subSysName, Set<Artifact> subsysReqs) throws IOException, OseeCoreException {
      excelWriter.startSheet(getShortSheetName(subSysName, "5.1"), 3);

      excelWriter.writeRow("Subsystem SSDD section 5.1");
      excelWriter.writeRow("Paragraph #", "Paragraph Title", "Allocated Components");

      String[] row = new String[3];
      for (Artifact artifact : subsysReqs) {
         row[0] = artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         row[1] = artifact.getName();

         if (artifact.isOfType(CoreArtifactTypes.SubsystemRequirementMSWord)) {

            boolean isRelated = false;
            List<Artifact> relatedArtifacts = artifact.getRelatedArtifacts(CoreRelationTypes.Allocation__Component);
            if (relatedArtifacts != null && !relatedArtifacts.isEmpty()) {
               ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), findExcludedArtifactsByView);
               isRelated = true;
            }
            for (Artifact component : relatedArtifacts) {
               components.add(component);
               row[2] = component.getName();
               excelWriter.writeRow(row);
            }

            if (row[0] != null && !isRelated) { // if this requirement has no allocated components (i.e. the for loop didn't run)
               row[2] = null;
               excelWriter.writeRow(row);
            }
         } else {
            row[2] = "N/A: " + artifact.getArtifactTypeName();
            excelWriter.writeRow(row);
         }
      }

      excelWriter.endSheet();
   }

   private void generateComponentAllocation(String subSysName, Set<Artifact> subsysReqs) throws IOException, OseeCoreException {
      excelWriter.startSheet(getShortSheetName(subSysName, "5.2"), 3);
      excelWriter.writeRow("Subsystem SSDD section 5.2");

      String[] row = new String[3];
      for (Artifact component : components) {
         excelWriter.writeRow();
         excelWriter.writeRow();
         excelWriter.writeRow(subSysName + " Subsystem Requirements allocated to the " + component.getName());
         excelWriter.writeRow("PIDS Paragraph #", "PIDS Paragraph Title", "Notes <rationale>");

         List<Artifact> relatedArtifacts = component.getRelatedArtifacts(CoreRelationTypes.Allocation__Requirement);
         if (relatedArtifacts != null && !relatedArtifacts.isEmpty()) {
            ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), findExcludedArtifactsByView);
         }
         for (Artifact subsysReq : relatedArtifacts) {
            if (subsysReqs.contains(subsysReq)) {
               row[0] = subsysReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
               row[1] = subsysReq.getName();
               String rationale = component.getRelationRationale(subsysReq, CoreRelationTypes.Allocation__Requirement);
               row[2] = rationale.equals("") ? null : rationale;
               excelWriter.writeRow(row);
            }
         }
      }

      components.clear();
      excelWriter.endSheet();
   }

   private void recurseWholeSubsystem(String subSysName, Artifact subsysFolder, Set<String> missingAllocationGuids) throws OseeCoreException {
      Set<Artifact> subsysReqs = new LinkedHashSet<>();
      subsysToSubsysReqsMap.put(subSysName, subsysReqs);
      countDescendants(subSysName, subsysReqs, subsysFolder, missingAllocationGuids);
   }

   private void resetCounters() {
      subsysDescendantCount = 0;
      subsysMarkedCount = 0;
      subsysMarkedAndTracedCount = 0;
      subsysMarkedAndQualifiedCount = 0;
      subsysMarkedAndAllocatedToComponentCount = 0;
   }

   private void countDescendants(String subSysName, Set<Artifact> subsysReqs, Artifact artifact, Set<String> missingAllocationGuids) throws OseeCoreException {
      List<Artifact> children = artifact.getChildren();
      if (children != null && !children.isEmpty()) {
         ViewIdUtility.removeExcludedArtifacts(children.iterator(), findExcludedArtifactsByView);
      }
      for (Artifact child : children) {
         if (child.isOfType(CoreArtifactTypes.SubsystemRequirementMSWord)) {
            subsysDescendantCount++;
            String selectedSubSystem = child.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");

            if (selectedSubSystem.equals(subSysName)) {
               subsysMarkedCount++;

               String qualMethod = child.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod);
               if (!qualMethod.equals(AttributeId.UNSPECIFIED)) {
                  subsysMarkedAndQualifiedCount++;
               }

               int higherTraceCount = child.getRelatedArtifactsCount(CoreRelationTypes.Requirement_Trace__Higher_Level);
               if (higherTraceCount > 0) {
                  subsysMarkedAndTracedCount++;
               }

               int allocationCount = child.getRelatedArtifactsCount(CoreRelationTypes.Allocation__Component);
               if (allocationCount > 0) {
                  subsysMarkedAndAllocatedToComponentCount++;
               } else {
                  missingAllocationGuids.add(child.getGuid());
               }
            }
            subsysReqs.add(child);
         }
         countDescendants(subSysName, subsysReqs, child, missingAllocationGuids);
      }
   }

   private void generateSystemToSubsystemTrace(String subSysName) throws IOException, OseeCoreException {
      excelWriter.startSheet(getShortSheetName(subSysName, "System Trace"), 4);

      excelWriter.writeRow(subSysName, "System To Subsystem Trace");
      excelWriter.writeRow(CoreArtifactTypes.SystemRequirementMSWord.getName(), null, "Traceable Subsystem Requirement",
         null);
      excelWriter.writeRow("Paragraph #", "Paragraph Title", "Paragraph #", "Paragraph Title");

      String[] row = new String[4];
      Set<Artifact> orderedSysReqs = subsysToSysReqsMap.get(subSysName);
      Set<Artifact> subsysReqs = subsysToSubsysReqsMap.get(subSysName);

      for (Artifact sysReq : orderedSysReqs) {
         row[0] = sysReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         row[1] = sysReq.getName();

         boolean isRelated = false;
         List<Artifact> relatedArtifacts = sysReq.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Lower_Level);
         if (relatedArtifacts != null && !relatedArtifacts.isEmpty()) {
            ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), findExcludedArtifactsByView);
            isRelated = true;
         }
         for (Artifact subSysReq : relatedArtifacts) {
            if (subsysReqs.contains(subSysReq)) {
               row[2] = subSysReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
               row[3] = subSysReq.getName();
               excelWriter.writeRow(row);
            }
         }
         if (row[0] != null && !isRelated) { // if this requirement is not traced to any subsys req (i.e. the condition in the for loop didn't run)
            row[2] = row[3] = null;
            excelWriter.writeRow(row);
         }
      }

      excelWriter.endSheet();
   }

   private String getShortSheetName(String subSysName, String sufix) {
      String shortenSubSysName = subSysName.contains(" ") ? WordUtils.initials(subSysName) : subSysName;
      return shortenSubSysName + " " + sufix;
   }

   private void generateSubsystemToSystemTrace(String subSysName) throws IOException, OseeCoreException {
      excelWriter.startSheet(getShortSheetName(subSysName, "Subsystem Trace"), 5);

      Set<Artifact> subsysReqs = subsysToSubsysReqsMap.get(subSysName);

      excelWriter.writeRow(subSysName, "Subsystem To System Trace");
      excelWriter.writeRow(CoreArtifactTypes.SubsystemRequirementMSWord.getName(), null, null,
         "Traceable System Requirement", null);
      excelWriter.writeRow("Paragraph #", "Paragraph Title", CoreAttributeTypes.QualificationMethod.getName(),
         "Paragraph #", "Paragraph Title");

      String[] row = new String[5];

      for (Artifact subsysReq : subsysReqs) {
         row[0] = subsysReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         row[1] = subsysReq.getName();
         if (subsysReq.isOfType(CoreArtifactTypes.SubsystemRequirementMSWord)) {
            row[2] = subsysReq.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod);
         } else {
            row[2] = "N/A: " + subsysReq.getArtifactTypeName();
         }

         boolean isRelated = false;
         List<Artifact> relatedArtifacts =
            subsysReq.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Higher_Level);
         if (relatedArtifacts != null && !relatedArtifacts.isEmpty()) {
            ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), findExcludedArtifactsByView);
            isRelated = true;
         }

         for (Artifact subSysReq : relatedArtifacts) {
            row[3] = subSysReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
            row[4] = subSysReq.getName();
            excelWriter.writeRow(row);
         }
         if (row[0] != null && !isRelated) { // if this requirement is not traced to any sys req (i.e. the for loop didn't run)
            row[3] = row[4] = null;
            excelWriter.writeRow(row);
         }
      }

      excelWriter.endSheet();
   }

   private Artifact getProductComponent(Artifact root) throws OseeCoreException {
      for (Artifact artifact : root.getChildren()) {
         if (artifact.isOfType(CoreArtifactTypes.Component)) {
            return artifact;
         }
      }
      throw new OseeStateException("Did not find a child of the hierarchy root that was of type ",
         CoreArtifactTypes.Component);
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates a spreadsheet of traceability and allocation for sys <-> subsys.";
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals("Branch")) {
         viewerWidget = (XBranchSelectWidget) xWidget;
         viewerWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  BranchId branch = viewerWidget.getSelection();
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
   public Collection<String> getCategories() {
      return Arrays.asList("Reports");
   }
}