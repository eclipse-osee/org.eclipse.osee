/*********************************************************************
 * Copyright (c) 2008 Boeing
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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.ViewIdUtility;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXListArtifactDropWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXBranchAndViewSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class PublishSubsystemToDesignTraceabilityBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Publish Subsystem To Design Traceability";
   private static final String BRANCH_AND_VIEW = "Branch and View";
   static final String SUBSYSTEM_ROOT_ARTIFACTS = "Subsystem Root Artifacts";

   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;
   private XXStringsWidget branchViewWidget;
   private XXBranchAndViewSelWidget branchAndViewWidget;
   private XXListArtifactDropWidget viewerWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public PublishSubsystemToDesignTraceabilityBlam() {
      super(BLAM_NAME, CoreImage.PUBLISH);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andListDropWidget(SUBSYSTEM_ROOT_ARTIFACTS);
      wb.andBranchAndViewSelWidget(BRANCH_AND_VIEW);
      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, xModListener, isEditable);
      if (xWidget.getLabel().equals(SUBSYSTEM_ROOT_ARTIFACTS)) {
         viewerWidget = (XXListArtifactDropWidget) xWidget;
         viewerWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  List<Artifact> arts = viewerWidget.getSelectedArtifacts();
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
      } else if (xWidget.getLabel().equals(BRANCH_AND_VIEW)) {
         branchAndViewWidget = (XXBranchAndViewSelWidget) xWidget;
      }
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask(getDescriptionUsage(), 100);

      List<Artifact> subsystems = variableMap.getArtifacts(SUBSYSTEM_ROOT_ARTIFACTS);
      BranchId branch = branchAndViewWidget.getBranch();
      BranchViewToken view = branchAndViewWidget.getSelectedBranchView();
      setViewId(view);

      init();
      Set<ArtifactId> findExcludedArtifactsByView = ViewIdUtility.findExcludedArtifactsByView(viewId, branch);
      ViewIdUtility.removeExcludedArtifacts(subsystems.iterator(), findExcludedArtifactsByView);

      monitor.subTask("Aquiring Design Artifacts"); // bulk load for performance reasons
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemDesignMsWord, branch);
      monitor.worked(10);

      monitor.subTask("Aquiring Subsystem Requirements"); // bulk load for performance reasons
      ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SubsystemRequirementMsWord, branch);
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

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private void init() throws IOException {
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   private void writeSubsystemDesignTraceability(Artifact subsystem) throws IOException {
      excelWriter.startSheet(subsystem.getName(), 200);
      excelWriter.writeRow(subsystem.getName() + " Subsystem To Design Traceability");

      excelWriter.writeRow("Subsystem Requirement", null, "Subsystem Design");
      excelWriter.writeRow(ParagraphNumber.getName(), "Paragraph Title", ParagraphNumber.getName(), "Paragraph Title");

      List<Artifact> descendants = subsystem.getDescendants();
      if (descendants != null) {
         ViewIdUtility.removeExcludedArtifacts(descendants.iterator(), excludedArtifactIdMap);

         for (Artifact subsystemRequirement : descendants) {
            excelWriter.writeCell(subsystemRequirement.getSoleAttributeValue(ParagraphNumber, ""));
            excelWriter.writeCell(subsystemRequirement.getName());

            if (subsystemRequirement.isOfType(CoreArtifactTypes.SubsystemRequirementMsWord)) {
               boolean loopNeverRan = true;
               List<Artifact> relatedArtifacts =
                  subsystemRequirement.getRelatedArtifacts(CoreRelationTypes.Design_Design);
               ViewIdUtility.removeExcludedArtifacts(relatedArtifacts.iterator(), excludedArtifactIdMap);
               for (Artifact subsystemDesign : relatedArtifacts) {
                  if (subsystemDesign.isOfType(CoreArtifactTypes.SubsystemDesignMsWord)) {
                     loopNeverRan = false;
                     excelWriter.writeCell(subsystemDesign.getSoleAttributeValue(ParagraphNumber, ""), 2);
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
      }

      excelWriter.endSheet();
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE);
   }

   @Override
   public String getDescriptionUsage() {
      return "Publish Subsystem To Design Traceability Tables";
   }

}
