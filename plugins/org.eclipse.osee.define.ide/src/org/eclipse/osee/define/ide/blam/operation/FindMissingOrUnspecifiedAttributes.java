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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
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
      AttributeTypeToken attributeType = variableMap.getAttributeType("Attribute Type");

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
   public Collection<String> getCategoriesStr() {
      return Arrays.asList("Define.Publish.Check");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}
