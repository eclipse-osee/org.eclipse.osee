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
package org.eclipse.osee.framework.skynet.core.importing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKey;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class ExcelOseeTypeDataParser implements RowProcessor {
   private enum Table {
      ARTIFACT_TYPE_TABLE, ATTRIBUTE_TYPE_TABLE, ATTRIBUTE_MAP_TABLE, RELATION_TYPE_TABLE, RELATION_SIDE_TABLE
   }

   private static final String description = "Setup artifact, attribute, and relation type data";
   private final ExcelSaxHandler excelHandler;
   private Table currentTable;
   private Iterator<Table> tableIterator;
   private final HashCollection<String, String> superTypeMap; //map each super type to all its direct sub types
   private final ArrayList<ValidityRow> validityArray;
   private final List<AttributeRow> attributeMapRows;
   private boolean done;
   private final boolean debugRows = false;
   private final XMLReader xmlReader;
   private final IOseeDataTypeProcessor dataTypeProcessor;

   public ExcelOseeTypeDataParser(IOseeDataTypeProcessor dataTypeProcessor) throws SAXException, IOException {
      this.dataTypeProcessor = dataTypeProcessor;
      excelHandler = new ExcelSaxHandler(this, true, true);
      superTypeMap = new HashCollection<String, String>();
      validityArray = new ArrayList<ValidityRow>();
      attributeMapRows = new ArrayList<AttributeRow>();

      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(excelHandler);
   }

   public void extractTypesFromSheet(InputStream importFile) throws IOException, SAXException {
      done = false;
      tableIterator = Arrays.asList(Table.values()).iterator();
      xmlReader.parse(new InputSource(importFile));
   }

   private void ensureAllSuperTypesInheritFromArtifact() {
      Collection<String> artifactSubTypes = superTypeMap.getValues("Artifact");
      for (String typeName : superTypeMap.keySet()) {
         if (!typeName.equals("Artifact") && !artifactSubTypes.contains(typeName)) {
            superTypeMap.put("Artifact", typeName);
         }
      }
   }

   public void finish() throws OseeCoreException {
      ensureAllSuperTypesInheritFromArtifact();
      Collection<String> concreteTypes = new ArrayList<String>();
      for (AttributeRow attributeRow : attributeMapRows) {
         concreteTypes.clear();
         dataTypeProcessor.onAttributeValidity(attributeRow.attributeName, attributeRow.artifactSuperTypeName,
               determineConcreteTypes(attributeRow.artifactSuperTypeName, concreteTypes));
      }
      processRelationValidity();
   }

   private void processRelationValidity() throws OseeCoreException {
      Collection<String> concreteTypes = new ArrayList<String>();
      CompositeKeyHashMap<String, String, ObjectPair<Integer, Integer>> keyMap =
            new CompositeKeyHashMap<String, String, ObjectPair<Integer, Integer>>();
      for (ValidityRow row : validityArray) {
         concreteTypes.clear();
         for (String artifactTypeName : determineConcreteTypes(row.artifactSuperTypeName, concreteTypes)) {
            String relationType = row.relationTypeName;
            int sideAMax = row.sideAmax;
            int sideBMax = row.sideBmax;

            ObjectPair<Integer, Integer> sideDefinition = keyMap.get(artifactTypeName, relationType);
            if (sideDefinition == null) {
               sideDefinition = new ObjectPair<Integer, Integer>(sideAMax, sideBMax);
               keyMap.put(artifactTypeName, relationType, sideDefinition);
            } else {
               sideDefinition.object1 = Math.max(sideDefinition.object1, sideAMax);
               sideDefinition.object2 = Math.max(sideDefinition.object2, sideBMax);
            }
         }
      }

      for (Entry<CompositeKey<String, String>, ObjectPair<Integer, Integer>> entry : keyMap.entrySet()) {
         String artifactTypeName = entry.getKey().getKey1();
         String relationTypeName = entry.getKey().getKey2();
         ObjectPair<Integer, Integer> sideDefinition = entry.getValue();
         int sideAMax = sideDefinition.object1;
         int sideBMax = sideDefinition.object2;
         dataTypeProcessor.onRelationValidity(artifactTypeName, relationTypeName, sideAMax, sideBMax);
      }
   }

   public static String getDescription() {
      return description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processHeaderRow(java.lang.String[])
    */
   public void processHeaderRow(String[] headerRow) {
      if (done) return;
      if (tableIterator.hasNext()) {
         currentTable = tableIterator.next();
      } else {
         throw new IllegalArgumentException(
               "Encountered row past end of last expected table: " + Arrays.deepToString(headerRow));
      }
   }

   /**
    * import Artifacts
    * 
    * @param row
    */
   public void processRow(String[] row) {
      if (done) return;
      try {
         switch (currentTable) {
            case ARTIFACT_TYPE_TABLE:
               addArtifactType(row);
               break;
            case ATTRIBUTE_TYPE_TABLE:
               addAttributeType(row);
               break;
            case ATTRIBUTE_MAP_TABLE:
               associateAttribute(row);
               break;
            case RELATION_TYPE_TABLE:
               addRelationType(row);
               break;
            case RELATION_SIDE_TABLE:
               addValidityConstraints(row);
         }
      } catch (Exception ex) {
         OseeLog.log(ExcelOseeTypeDataParser.class, Level.SEVERE, ex);
      }
   }

   private void addValidityConstraints(String[] row) {
      validityArray.add(new ValidityRow(row[0], row[1], getQuantity(row[2]), getQuantity(row[3])));
   }

   private void associateAttribute(String[] row) {
      if (debugRows) System.out.println("   associateAttribute => " + row[0] + "," + row[1]);
      attributeMapRows.add(new AttributeRow(row));
   }

   /**
    * @param row
    * @throws Exception
    */
   private void addAttributeType(String[] row) throws Exception {
      if (debugRows) System.out.println("   addAttributeType => " + row[0] + "," + row[1]);
      String attrBaseType = row[0];
      String attrProviderType = row[1];
      String attributeName = row[2];
      String fileTypeExtension = row[3] != null ? row[3] : "";
      String taggerId = row[4] != null ? row[4] : "";
      String defaultValue = row[5];
      String validityXml = row[6];
      int minOccurrence = getQuantity(row[7]);
      int maxOccurrence = getQuantity(row[8]);
      String tipText = row[9];

      dataTypeProcessor.onAttributeType(attrBaseType, attrProviderType, fileTypeExtension, "", attributeName,
            defaultValue, validityXml, minOccurrence, maxOccurrence, tipText, taggerId);
   }

   /**
    * @param row
    * @throws OseeCoreException
    */
   private void addRelationType(String[] row) throws Exception {
      if (debugRows) System.out.println("   addRelationType => " + row[0] + "," + row[1]);
      String relationTypeName = row[0];
      String sideAName = row[1];
      String abPhrasing = row[2];
      String sideBName = row[3];
      String baPhrasing = row[4];
      String shortName = row[5];
      String ordered = row[6];

      dataTypeProcessor.onRelationType("", relationTypeName, sideAName, sideBName, abPhrasing, baPhrasing, shortName,
            ordered);
   }

   private void addArtifactType(String[] row) throws Exception {
      if (debugRows) System.out.println("  addArtifactType => " + row[0] + "," + row[1]);
      String factoryClassName = row[0];
      String artifactTypeName = row[1];
      String superTypeName = row[2];

      if (!artifactTypeName.equals("Artifact")) {
         superTypeMap.put(superTypeName, artifactTypeName);
      }
      dataTypeProcessor.onArtifactType(factoryClassName, "", artifactTypeName);
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processEmptyRow(java.lang.String[])
    */
   public void processEmptyRow() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processCommentRow(java.lang.String[])
    */
   public void processCommentRow(String[] row) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#reachedEndOfWorksheet()
    */
   public void reachedEndOfWorksheet() {
      done = true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#detectedTotalRowCount(int)
    */
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#foundStartOfWorksheet(java.lang.String)
    */
   public void foundStartOfWorksheet(String sheetName) {
   }

   private int getQuantity(String quantity) {
      if (quantity.equalsIgnoreCase("UNLIMITED")) {
         return Integer.MAX_VALUE;
      }
      return Integer.parseInt(quantity);
   }

   private Collection<String> determineConcreteTypes(String artifactSuperTypeName, Collection<String> concreteTypes) throws OseeCoreException {
      if (dataTypeProcessor.doesArtifactSuperTypeExist(artifactSuperTypeName)) { // artifactSuperTypeName might also be a concrete type
         concreteTypes.add(artifactSuperTypeName);
      }
      Collection<String> subTypeNames = superTypeMap.getValues(artifactSuperTypeName);
      if (subTypeNames != null) {
         for (String subTypeName : subTypeNames) {
            determineConcreteTypes(subTypeName, concreteTypes);
         }
      }
      return concreteTypes;
   }

   private class ValidityRow {
      public String artifactSuperTypeName;
      public String relationTypeName;
      public int sideAmax;
      public int sideBmax;

      public ValidityRow(String artifactSuperTypeName, String relationTypeName, int sideAmax, int sideBmax) {
         this.artifactSuperTypeName = artifactSuperTypeName;
         this.relationTypeName = relationTypeName;
         this.sideAmax = sideAmax;
         this.sideBmax = sideBmax;
      }

      public String toString() {
         return String.format("RelationType:[%s] ArtifactSuperType:[%s] Sides(A,B) - (%s, %s)", relationTypeName,
               artifactSuperTypeName, sideAmax, sideBmax);
      }
   }

   private class AttributeRow {
      private String artifactSuperTypeName;
      private String attributeName;

      public AttributeRow(String[] row) {
         super();
         artifactSuperTypeName = row[0];
         attributeName = row[1];
      }
   }
}
