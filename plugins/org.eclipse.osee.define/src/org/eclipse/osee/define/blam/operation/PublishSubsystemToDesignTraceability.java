/*******************************************************************************
 * Copyright (c) 2004, 2008 Boeing.
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
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
public class PublishSubsystemToDesignTraceability extends AbstractBlam {
   private static final String SUBSYSTEM_ROOT_ARTIFACTS = "Subsystem Root Artifacts";
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;

   private XCombo branchViewWidget;
   private XListDropViewer viewerWidget;

   @Override
   public String getName() {
      return "Publish Subsystem To Design Traceability";
   }

   private void init() throws IOException {
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask(getDescriptionUsage(), 100);

      List<Artifact> subsystems = variableMap.getArtifacts(SUBSYSTEM_ROOT_ARTIFACTS);
      BranchId branch = subsystems.get(0).getBranch();

      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);

      init();
      Set<ArtifactId> findExcludedArtifactsByView = ViewIdUtility.findExcludedArtifactsByView(viewId, branch);
      if (subsystems != null) {
         ViewIdUtility.removeExcludedArtifacts(subsystems.iterator(), findExcludedArtifactsByView);
      }

      monitor.subTask("Aquiring Design Artifacts"); // bulk load for performance reasons
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemDesign, branch);
      monitor.worked(10);

      monitor.subTask("Aquiring Subsystem Requirements"); // bulk load for performance reasons
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemRequirementMSWord, branch);
      monitor.worked(60);

      int workIncrement = 30 / subsystems.size();
      for (Artifact subsystem : subsystems) {
         if (monitor.isCanceled()) {
            return;
         }
         monitor.worked(workIncrement);
         writeSubsystemDesignTraceability(subsystem);
      }

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("SubsystemToDesignTrace_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void writeSubsystemDesignTraceability(Artifact subsystem) throws IOException {
      excelWriter.startSheet(subsystem.getName(), 200);
      excelWriter.writeRow(subsystem.getName() + " Subsystem To Design Traceability");

      excelWriter.writeRow("Subsystem Requirement", null, "Subsystem Design");
      excelWriter.writeRow(CoreAttributeTypes.ParagraphNumber.getName(), "Paragraph Title",
         CoreAttributeTypes.ParagraphNumber.getName(), "Paragraph Title");

      List<Artifact> descendants = subsystem.getDescendants();
      if (descendants != null) {
         ViewIdUtility.removeExcludedArtifacts(descendants.iterator(), excludedArtifactIdMap);
      }
      for (Artifact subsystemRequirement : descendants) {
         excelWriter.writeCell(subsystemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
         excelWriter.writeCell(subsystemRequirement.getName());

         if (subsystemRequirement.isOfType(CoreArtifactTypes.SubsystemRequirementMSWord)) {
            boolean loopNeverRan = true;
            List<Artifact> relatedArtifacts =
               subsystemRequirement.getRelatedArtifacts(CoreRelationTypes.Design__Design);
            if (relatedArtifacts != null) {
               ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), excludedArtifactIdMap);
            }
            for (Artifact subsystemDesign : relatedArtifacts) {
               if (subsystemDesign.isOfType(CoreArtifactTypes.SubsystemDesign)) {
                  loopNeverRan = false;
                  excelWriter.writeCell(subsystemDesign.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""),
                     2);
                  excelWriter.writeCell(subsystemDesign.getName(), 3);
                  excelWriter.endRow();
               }
            }
            if (loopNeverRan) {
               excelWriter.endRow();
            }
         } else {
            excelWriter.writeCell("N/A - " + subsystemRequirement.getArtifactTypeName());
            excelWriter.endRow();
         }
      }

      excelWriter.endSheet();
   }

   @Override
   public String getDescriptionUsage() {
      return "Publish Subsystem To Design Traceability Tables";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"Subsystem Root Artifacts\" />");
      builder.append(BRANCH_VIEW_WIDGET);
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(SUBSYSTEM_ROOT_ARTIFACTS)) {
         viewerWidget = (XListDropViewer) xWidget;
         viewerWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  List<Artifact> arts = viewerWidget.getArtifacts();
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
}