/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
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
 * @author Megumi Telles
 */
public class FindDuplicateArtifactNames extends AbstractBlam {
   private static final String ROOT_ARTIFACTS = "Root Artifacts";

   private XCombo branchViewWidget;
   private XListDropViewer viewerWidget;

   @Override
   public String getName() {
      return "Find Artifacts With Duplicate Names";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      Date date = new Date();
      File file = OseeData.getFile("DUP_NAMES_" + dateFormat.format(date) + ".xml");
      ISheetWriter excelWriter = new ExcelXmlWriter(file);

      List<Artifact> artifacts = variableMap.getArtifacts(ROOT_ARTIFACTS);
      BranchId branch = artifacts.get(0).getBranch();

      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);
      Set<ArtifactId> findExcludedArtifactsByView = ViewIdUtility.findExcludedArtifactsByView(viewId, branch);

      excelWriter.startSheet("Report", 6);
      excelWriter.writeRow("Root Artifact", "Subsystem", "Artifact Name", "Artifact Type", "Art Id", "Guid");
      for (Artifact artifact : artifacts) {
         List<Artifact> children = artifact.getChildren();
         for (Artifact child : children) {
            List<Artifact> artifactListFromName =
               ArtifactQuery.getArtifactListFromName(child.getName(), branch, DeletionFlag.EXCLUDE_DELETED);
            if (artifactListFromName != null) {
               ViewIdUtility.removeExcludedArtifacts(artifactListFromName.iterator(), findExcludedArtifactsByView);
            }
            if (artifactListFromName.size() > 1) {
               for (Artifact art : artifactListFromName) {
                  String subsystem = art.getSoleAttributeValueAsString(CoreAttributeTypes.Subsystem, "");
                  excelWriter.writeRow(artifact.getName(), subsystem, art.getName(), art.getArtifactType().getName(),
                     art.getArtId(), art.getGuid());
               }
            }
         }
      }
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      Program.launch(file.getAbsolutePath());
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(ROOT_ARTIFACTS)) {
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
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"Root Artifacts\" />");
      builder.append(BRANCH_VIEW_WIDGET);
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish.Check");
   }

}
