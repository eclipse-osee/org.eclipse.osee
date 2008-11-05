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
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class ExportArtifacts extends AbstractBlam {
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter; //attributeType, columnIndex
   private AttributeType[] attributeColumns;
   private AttributeType nameAttributeType;
   private static final int NUM_FIXED_COLUMNS = 4;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      init();

      List<Artifact> parentArtifacts = variableMap.getArtifacts("artifacts");
      List<Artifact> artifacts;

      if (variableMap.getBoolean("Include Children")) {
         artifacts = new ArrayList<Artifact>(400);
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
      excelWriter.writeCell("HRID");
      excelWriter.writeCell("Artifact Type");
      excelWriter.writeCell("Name");
      for (AttributeType attributeType : attributeColumns) {
         excelWriter.writeCell(attributeType.getName());
      }
      excelWriter.endRow();

      for (Artifact artifact : artifacts) {
         Arrays.fill(row, null);
         row[0] = artifact.getGuid();
         row[1] = artifact.getHumanReadableId();
         row[2] = artifact.getArtifactTypeName();
         row[3] = artifact.getDescriptiveName();
         for (AttributeType attributeType : artifact.getAttributeTypes()) {
            if (!attributeType.equals(nameAttributeType)) {
               String value = artifact.getAttributesToString(attributeType.getName());
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

   private void mapAttributeTypeToColumn(List<Artifact> artifacts) throws OseeCoreException {
      HashSet<AttributeType> attributeTypes = new HashSet<AttributeType>();

      for (Artifact artifact : artifacts) {
         for (AttributeType attributeType : artifact.getAttributeTypes()) {
            attributeTypes.add(attributeType);
         }
      }

      attributeTypes.remove(nameAttributeType);
      attributeColumns = attributeTypes.toArray(new AttributeType[attributeTypes.size()]);
      Arrays.sort(attributeColumns);
   }

   private void init() throws IOException, OseeDataStoreException, OseeTypeDoesNotExist {
      nameAttributeType = AttributeTypeManager.getType("Name");
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Include Children\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }
}