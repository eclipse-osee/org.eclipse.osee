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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class ExportArtifacts extends AbstractBlam {
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;
   private AttributeTypeToken[] attributeColumns;
   private static final int NUM_FIXED_COLUMNS = 3;

   @Override
   public String getName() {
      return "Export Artifacts";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      init();

      List<Artifact> parentArtifacts = variableMap.getArtifacts("artifacts");
      List<Artifact> artifacts;

      if (variableMap.getBoolean("Include Children")) {
         artifacts = new ArrayList<>(400);
         for (Artifact artifact : parentArtifacts) {
            artifacts.add(artifact);
            artifacts.addAll(artifact.getDescendants());
         }
      } else {
         artifacts = parentArtifacts;
      }

      mapAttributeTypeToColumn(artifacts);

      String[] row = new String[attributeColumns.length + NUM_FIXED_COLUMNS];
      excelWriter.startSheet("Artifacts", row.length);
      excelWriter.writeCell("GUID");
      excelWriter.writeCell("Artifact Type");
      excelWriter.writeCell("Name");
      for (AttributeTypeToken attributeType : attributeColumns) {
         excelWriter.writeCell(attributeType.getName());
      }
      excelWriter.endRow();

      for (Artifact artifact : artifacts) {
         Arrays.fill(row, null);
         row[0] = artifact.getGuid();
         row[1] = artifact.getArtifactTypeName();
         row[2] = artifact.getName();
         for (AttributeTypeId attributeType : artifact.getAttributeTypes()) {
            if (attributeType.notEqual(CoreAttributeTypes.Name)) {
               String value = artifact.getAttributesToString(attributeType);
               if (!value.equals("")) {
                  row[NUM_FIXED_COLUMNS + Arrays.binarySearch(attributeColumns, attributeType)] = value;
               }
            }
         }
         excelWriter.writeRow(row);
      }

      excelWriter.endSheet();
      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("artifacts" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void mapAttributeTypeToColumn(List<Artifact> artifacts) {
      HashSet<AttributeTypeId> attributeTypes = new HashSet<>();

      for (Artifact artifact : artifacts) {
         for (AttributeTypeId attributeType : artifact.getAttributeTypes()) {
            attributeTypes.add(attributeType);
         }
      }

      attributeTypes.remove(CoreAttributeTypes.Name);
      attributeColumns = attributeTypes.toArray(new AttributeType[attributeTypes.size()]);
      Arrays.sort(attributeColumns);
   }

   private void init() throws IOException {
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Include Children\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }
}