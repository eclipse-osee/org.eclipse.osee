/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.ViewIdUtility;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
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
   private ArtifactTypeToken[] lowerLevelTypes;

   private XCombo branchViewWidget;
   private XListDropViewer lowerLevel;
   private AttributeTypeId safetyAttribute = CoreAttributeTypes.IDAL;
   private static final String LEGACY_DAL = "Use Legacy DAL";

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
      lowerLevelTypes =
         variableMap.getCollection(ArtifactTypeToken.class, "Low Level Requirement Type(s)").stream().toArray(
            ArtifactTypeToken[]::new);

      List<Artifact> arts = variableMap.getArtifacts("Lower Level Requirements");
      BranchId branch = arts.get(0).getBranch();
      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);
      Object isLegacy = variableMap.getValue(LEGACY_DAL);
      if (isLegacy.equals(true)) {
         safetyAttribute = CoreAttributeTypes.LegacyDal;
      }
      excludedArtifactIdMap = ViewIdUtility.findExcludedArtifactsByView(viewId, branch);

      initLowLevelRequirements(arts); // depends on excludedArtifactIdMap being set correctly
      initAllocationComponents(variableMap.getArtifacts("Allocation Components"));

      monitor.subTask("Loading Higher Level Requirements"); // bulk load to improve performance
      monitor.worked(1);
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemRequirementMsWord, branch);
      monitor.worked(30);

      orderSubsystemReqs(ArtifactQuery.getArtifactFromId(CoreArtifactTokens.SubSystemRequirementsFolder, branch));

      generateLowLevelToSubsystemTrace();
      generateSubsystemToLowLevelReqTrace();

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("Subsystem_To_Lower_Level_Trace_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void generateLowLevelToSubsystemTrace() throws IOException {
      excelWriter.startSheet("5-2", 11);

      excelWriter.writeRow("5-2  Requirements Traceability to Subsystem PIDS Requirements");
      excelWriter.writeRow("Lower Level Requirements", null, null, null, "Traceable Subsystem Requirement");
      excelWriter.writeRow("SRS Paragraph #", "SRS Paragraph Title", "Qualification Method", "SRS File",
         "PIDS Paragraph #", "PIDS Paragraph Title", CoreAttributeTypes.Subsystem.getName(),
         "Software Requirement Type", "Partition", "IDAL", "Artifact ID");

      String[] row = new String[11];

      for (Artifact lowLevelReq : lowLevelReqs) {
         row[0] = lowLevelReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, null);
         row[1] = lowLevelReq.getName();
         boolean isRelated = false;

         row[2] = lowLevelReq.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod);
         row[3] = lowLevelReq.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "Unspecified");
         row[7] = lowLevelReq.getArtifactType().getName();
         row[8] = lowLevelReq.getAttributesToStringSorted(CoreAttributeTypes.Partition);
         row[9] = lowLevelReq.getSoleAttributeValue(safetyAttribute, "Not Applicable");
         row[10] = lowLevelReq.getIdString();

         List<Artifact> relatedArtifacts =
            lowLevelReq.getRelatedArtifacts(CoreRelationTypes.RequirementTrace_HigherLevelRequirement);
         if (!relatedArtifacts.isEmpty()) {
            ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), excludedArtifactIdMap);
            isRelated = true;
         }
         for (Artifact subSysReq : relatedArtifacts) {
            row[4] = subSysReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, null);
            row[5] = subSysReq.getName();
            row[6] = subSysReq.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
            excelWriter.writeRow(row);
         }

         if (row[0] != null && !isRelated) { // if this requirement is not traced to any lower level req (i.e. the for loop didn't run)
            row[4] = row[5] = row[6] = null;
            excelWriter.writeRow((Object[]) row);
         }
      }
      excelWriter.endSheet();
   }

   private void generateSubsystemToLowLevelReqTrace() throws IOException {
      excelWriter.startSheet("5-3", 7);

      excelWriter.writeRow("5-3 Traceability of SRS Requirements to Allocated Subsystem PIDS Requirements");
      excelWriter.writeRow(null, "Subsystem Requirement", null, "Traceable Lower Level Requirements", null);
      excelWriter.writeRow("Subsystem", "PIDS Paragraph #", "PIDS Paragraph Title", "SRS Paragraph #",
         "SRS Paragraph Title", "Component", "Software Requirement Type");

      for (Entry<String, List<Artifact>> entry : subsysToSubsysReqsMap.entrySet()) {
         List<Artifact> subsysReqs = entry.getValue();

         String[] row = new String[7];
         row[0] = entry.getKey();
         for (Artifact higherLevelReq : subsysReqs) {

            processSubsystemReq(row, higherLevelReq);
         }
      }
      excelWriter.endSheet();
   }

   private void processSubsystemReq(String[] row, Artifact higherLevelReq) throws IOException {
      List<Artifact> relatedArtifacts =
         higherLevelReq.getRelatedArtifacts(CoreRelationTypes.RequirementTrace_LowerLevelRequirement);
      ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), excludedArtifactIdMap);
      boolean isTraced = !Collections.disjoint(lowLevelReqs, relatedArtifacts);

      List<Artifact> allocatedComponets = higherLevelReq.getRelatedArtifacts(CoreRelationTypes.Allocation_Component);
      boolean allocated = !Collections.disjoint(components, allocatedComponets);

      /*
       * Do not include if a subsystem requirement is not allocated to the any of the relevant components, except when
       * this appears to be a case of missing allocation (i.e. it is still traced to any lower-level requirements).
       */
      if (isTraced || allocated) {
         row[1] = higherLevelReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, null);
         row[2] = higherLevelReq.getName();

         if (allocated) {
            row[5] = org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", allocatedComponets);
         } else {
            row[5] = "Missing allocation";
         }

         if (isTraced) {
            for (Artifact lowerLevelReq : relatedArtifacts) {
               if (lowLevelReqs.contains(lowerLevelReq)) {
                  row[3] = lowerLevelReq.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, null);
                  row[4] = lowerLevelReq.getName();
                  row[6] = lowerLevelReq.getArtifactType().getName();
                  excelWriter.writeRow(row);
               }
            }
         } else {
            row[3] = null;
            row[4] = null;
            excelWriter.writeRow(row);
         }
      }
   }

   private void initLowLevelRequirements(List<Artifact> artifacts) {
      RelationManager.getRelatedArtifacts(artifacts, 999, INCLUDE_DELETED, CoreRelationTypes.DefaultHierarchical_Child);
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(lowerLevelTypes)) {
            lowLevelReqs.add(artifact);
         }
         for (Artifact descendant : artifact.getDescendants()) {
            if (descendant.isOfType(lowerLevelTypes)) {
               lowLevelReqs.add(descendant);
            }
         }
      }
      ViewIdUtility.removeExcludedArtifacts(lowLevelReqs.iterator(), excludedArtifactIdMap);
   }

   private void initAllocationComponents(List<Artifact> artifacts) {
      RelationManager.getRelatedArtifacts(artifacts, 999, INCLUDE_DELETED, CoreRelationTypes.DefaultHierarchical_Child);
      for (Artifact artifact : artifacts) {
         if (!artifact.isOfType(CoreArtifactTypes.Folder)) {
            components.add(artifact);
         }
         for (Artifact descendant : artifact.getDescendants()) {
            if (!descendant.isOfType(CoreArtifactTypes.Folder)) {
               components.add(descendant);
            }
         }
      }
      ViewIdUtility.removeExcludedArtifacts(components.iterator(), excludedArtifactIdMap);
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

   private void orderSubsystemReqs(Artifact subsysTopFolder) {
      for (Artifact subsysFolder : subsysTopFolder.getChildren()) {
         List<Artifact> subsysReqs = subsysFolder.getDescendants();
         ViewIdUtility.removeExcludedArtifacts(subsysReqs.iterator(), excludedArtifactIdMap);

         Iterator<Artifact> iterator = subsysReqs.iterator();
         while (iterator.hasNext()) {
            if (!iterator.next().isOfType(CoreArtifactTypes.AbstractSubsystemRequirement)) {
               iterator.remove();
            }
         }

         subsysToSubsysReqsMap.put(subsysFolder.getName(), subsysReqs);
      }
   }

   private static final String TRACE_HANDLER_CHECKBOX =
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\"/>";

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Lower Level Requirements\" />" + //
         "<XWidget xwidgetType=\"XListDropViewer\" displayName=\"Allocation Components\" />" + //
         "<XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" displayName=\"Low Level Requirement Type(s)\" multiSelect=\"true\" />" + //
         "<XWidget xwidgetType=\"XCombo()\" displayName=\"Branch View\" horizontalLabel=\"true\"/>" + //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + LEGACY_DAL + "\" labelAfter=\"true\" horizontalLabel=\"true\"/>" + //
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
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE);
   }

   @Override
   public String getDescriptionUsage() {
      return "The Low Level Requirement artifacts will be filtered based on the type(s) selected.  The standard is to select \"Direct Software Requirement\".";
   }

}