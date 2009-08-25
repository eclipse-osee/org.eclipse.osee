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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
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
   private final boolean isFlattenHierarchyEnabled;

   public ExcelOseeTypeDataParser(IOseeDataTypeProcessor dataTypeProcessor) throws SAXException, IOException {
      this(dataTypeProcessor, true);
   }

   public ExcelOseeTypeDataParser(IOseeDataTypeProcessor dataTypeProcessor, boolean isFlattenHierarchyEnabled) throws SAXException, IOException {
      this.dataTypeProcessor = dataTypeProcessor;
      excelHandler = new ExcelSaxHandler(this, true, true);
      superTypeMap = new HashCollection<String, String>(false, HashSet.class);
      validityArray = new ArrayList<ValidityRow>();
      attributeMapRows = new ArrayList<AttributeRow>();

      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(excelHandler);
      this.isFlattenHierarchyEnabled = isFlattenHierarchyEnabled;
   }

   public void extractTypesFromSheet(InputStream importFile) throws IOException, SAXException {
      done = false;
      tableIterator = Arrays.asList(Table.values()).iterator();
      xmlReader.parse(new InputSource(importFile));
   }

   private void ensureAllSuperTypesInheritFromArtifact() {
      Set<String> superTypes = new HashSet<String>(superTypeMap.keySet());
      for (String typeName : superTypes) {
         if (!typeName.equals("Artifact")) {
            superTypeMap.put("Artifact", typeName);
         }
      }
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

   public void finish() throws OseeCoreException {
      ensureAllSuperTypesInheritFromArtifact();
      if (isFlattenHierarchyEnabled) {
         Collection<String> concreteTypes = new ArrayList<String>();
         for (AttributeRow attributeRow : attributeMapRows) {
            concreteTypes.clear();
            dataTypeProcessor.onAttributeValidity(attributeRow.attributeName, attributeRow.artifactSuperTypeName,
                  determineConcreteTypes(attributeRow.artifactSuperTypeName, concreteTypes));
         }
      } else {
         for (AttributeRow attributeRow : attributeMapRows) {
            Collection<String> childTypes = superTypeMap.getValues(attributeRow.artifactSuperTypeName);
            if (childTypes == null) {
               childTypes = Collections.emptyList();
            }
            dataTypeProcessor.onAttributeValidity(attributeRow.attributeName, attributeRow.artifactSuperTypeName,
                  childTypes);
         }
      }
      processRelationValidity();
   }

   private void processRelationValidity() throws OseeCoreException {
      if (isFlattenHierarchyEnabled) {
         Collection<String> concreteTypes = new ArrayList<String>();
         CompositeKeyHashMap<String, String, Pair<Integer, Integer>> keyMap =
               new CompositeKeyHashMap<String, String, Pair<Integer, Integer>>();
         for (ValidityRow row : validityArray) {
            dataTypeProcessor.onRelationValidity(row.artifactSuperTypeName, row.relationTypeName, row.sideAmax,
                  row.sideBmax);

            concreteTypes.clear();
            for (String artifactTypeName : determineConcreteTypes(row.artifactSuperTypeName, concreteTypes)) {
               String relationType = row.relationTypeName;
               int sideAMax = row.sideAmax;
               int sideBMax = row.sideBmax;

               Pair<Integer, Integer> sideDefinition = keyMap.get(artifactTypeName, relationType);
               if (sideDefinition == null) {
                  sideDefinition = new Pair<Integer, Integer>(sideAMax, sideBMax);
                  keyMap.put(artifactTypeName, relationType, sideDefinition);
               } else {
                  sideDefinition.setFirst(Math.max(sideDefinition.getFirst(), sideAMax));
                  sideDefinition.setSecond(Math.max(sideDefinition.getSecond(), sideBMax));
               }
            }
         }

         for (Entry<Pair<String, String>, Pair<Integer, Integer>> entry : keyMap.entrySet()) {
            String artifactTypeName = entry.getKey().getFirst();
            String relationTypeName = entry.getKey().getSecond();
            Pair<Integer, Integer> sideDefinition = entry.getValue();
            int sideAMax = sideDefinition.getFirst();
            int sideBMax = sideDefinition.getSecond();
            dataTypeProcessor.onRelationValidity(artifactTypeName, relationTypeName, sideAMax, sideBMax);
         }
      } else {
         for (ValidityRow row : validityArray) {
            dataTypeProcessor.onRelationValidity(row.artifactSuperTypeName, row.relationTypeName, row.sideAmax,
                  row.sideBmax);
         }
      }
   }

   public static String getDescription() {
      return description;
   }

   public void processHeaderRow(String[] headerRow) {
      if (done) {
         return;
      }
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
      if (done) {
         return;
      }
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
      if (debugRows) {
         System.out.println("   associateAttribute => " + row[0] + "," + row[1]);
      }
      attributeMapRows.add(new AttributeRow(row));
   }

   /**
    * @param row
    * @throws Exception
    */
   private void addAttributeType(String[] row) throws Exception {
      if (debugRows) {
         System.out.println("   addAttributeType => " + row[0] + "," + row[1]);
      }
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
      if (debugRows) {
         System.out.println("   addRelationType => " + row[0] + "," + row[1]);
      }
      String relationTypeName = row[0];
      String sideAName = row[1];
      String abPhrasing = row[2];
      String sideBName = row[3];
      String baPhrasing = row[4];
      String shortName = row[5];
      String ordered = row[6];
      String defaultOrderTypeGuid = row[7];
      if (defaultOrderTypeGuid == null) {
         if (ordered.equalsIgnoreCase("Yes")) {
            defaultOrderTypeGuid = RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC.getGuid();
         } else {
            defaultOrderTypeGuid = RelationOrderBaseTypes.UNORDERED.getGuid();
         }
      }

      dataTypeProcessor.onRelationType("", relationTypeName, sideAName, sideBName, abPhrasing, baPhrasing, shortName,
            ordered, defaultOrderTypeGuid);
   }

   private void addArtifactType(String[] row) throws Exception {
      if (debugRows) {
         System.out.println("  addArtifactType => " + row[0] + "," + row[1]);
      }
      String artifactTypeName = row[0];
      String superTypeName = row[1];

      if (!artifactTypeName.equals("Artifact")) {
         superTypeMap.put(superTypeName, artifactTypeName);
      }
      dataTypeProcessor.onArtifactType("", artifactTypeName, superTypeName);
   }

   public void processEmptyRow() {
   }

   public void processCommentRow(String[] row) {
   }

   public void reachedEndOfWorksheet() {
      done = true;
   }

   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
   }

   public void foundStartOfWorksheet(String sheetName) {
   }

   private int getQuantity(String quantity) {
      if (quantity.equalsIgnoreCase("UNLIMITED")) {
         return Integer.MAX_VALUE;
      }
      return Integer.parseInt(quantity);
   }

   private static class ValidityRow {
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

      @Override
      public String toString() {
         return String.format("RelationType:[%s] ArtifactSuperType:[%s] Sides(A,B) - (%s, %s)", relationTypeName,
               artifactSuperTypeName, sideAmax, sideBmax);
      }
   }

   private static class AttributeRow {
      private final String artifactSuperTypeName;
      private final String attributeName;

      public AttributeRow(String[] row) {
         super();
         artifactSuperTypeName = row[0];
         attributeName = row[1];
      }
   }
}
