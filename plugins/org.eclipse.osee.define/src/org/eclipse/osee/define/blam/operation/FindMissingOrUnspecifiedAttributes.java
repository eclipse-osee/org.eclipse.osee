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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Megumi Telles
 */
public class FindMissingOrUnspecifiedAttributes extends AbstractBlam {

   @Override
   public String getName() {
      return "Find Artifacts with Unspecified Attributes";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      Date date = new Date();
      File file = OseeData.getFile("UNSPECIFIED_" + dateFormat.format(date) + ".xml");
      ISheetWriter excelWriter = new ExcelXmlWriter(file);

      BranchId branch = variableMap.getBranch("Branch");
      AttributeType attributeType = variableMap.getAttributeType("Attribute Type");

      // Unspecified
      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromAttribute(attributeType, AttributeId.UNSPECIFIED, branch);
      excelWriter.startSheet("Attrs", 6);
      excelWriter.writeRow("Artifact", "Artifact Type", "Subsystem", "Guid", "Attribute Type", "Attribute Value");
      for (Artifact art : artifacts) {
         String subsystem = art.getSoleAttributeValueAsString(CoreAttributeTypes.Subsystem, "");
         excelWriter.writeRow(art.getName(), art.getArtifactType(), subsystem, art.getGuid(), attributeType,
            AttributeId.UNSPECIFIED.toString());
      }
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      Program.launch(file.getAbsolutePath());
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\" displayName=\"Attribute Type\" />" + //
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish.Check");
   }

}
