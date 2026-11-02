/*********************************************************************
 * Copyright (c) 2017 Boeing
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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;
import org.osgi.service.component.annotations.Component;

/**
 * @author Megumi Telles
 */
@Component(service = AbstractBlam.class, immediate = true)
public class FindMissingOrUnspecifiedAttributesBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Find Artifacts with Unspecified Attributes";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public FindMissingOrUnspecifiedAttributesBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget(XWidgetLabels.ATTRIBUTE_TYPE, WidgetId.XXAttributeTypeWidget);
      wb.andWidget(XWidgetLabels.BRANCH, WidgetId.XXBranchSelWidget);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      Date date = new Date();
      BranchId branch = variableMap.getBranch(XWidgetLabels.BRANCH);
      AttributeTypeToken attributeType = variableMap.getAttributeType(XWidgetLabels.ATTRIBUTE_TYPE);

      File file = OseeData.getFile("UNSPECIFIED_" + dateFormat.format(date) + ".xml");
      ISheetWriter excelWriter = new ExcelXmlWriter(file);

      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromAttribute(attributeType, AttributeId.UNSPECIFIED, branch);
      excelWriter.startSheet("Attrs", 6);
      excelWriter.writeRow("Artifact", XWidgetLabels.ARTIFACT_TYPE, "Subsystem", "Guid", XWidgetLabels.ATTRIBUTE_TYPE,
         "Attribute Value");
      for (Artifact art : artifacts) {
         String subsystem = art.getSoleAttributeValueAsString(CoreAttributeTypes.Subsystem, "");
         excelWriter.writeRow(art.getName(), art.getArtifactType(), subsystem, art.getGuid(), attributeType,
            AttributeId.UNSPECIFIED.toString());
      }
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      Program.launch(file.getAbsolutePath());
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   @Override
   public String getDescriptionUsage() {
      return "Searches a branch for artifacts that have an attribute set to the UNSPECIFIED value. Generates an Excel report listing each artifact's name, type, subsystem, and the unspecified attribute.";
   }

}
