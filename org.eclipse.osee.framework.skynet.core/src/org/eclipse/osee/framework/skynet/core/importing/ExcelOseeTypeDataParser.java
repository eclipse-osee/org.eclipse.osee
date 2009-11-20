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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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
public class ExcelOseeTypeDataParser {

   private final XMLReader xmlReader;
   private final InternalRowProcessor rowProcessor;

   public ExcelOseeTypeDataParser(IOseeDataTypeProcessor dataTypeProcessor) throws SAXException, IOException {
      this(dataTypeProcessor, true);
   }

   public ExcelOseeTypeDataParser(IOseeDataTypeProcessor dataTypeProcessor, boolean isFlattenHierarchyEnabled) throws SAXException, IOException {
      rowProcessor = new InternalRowProcessor(dataTypeProcessor, isFlattenHierarchyEnabled, false);
      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new ExcelSaxHandler(rowProcessor, true, true));
   }

   public void extractTypesFromSheet(String name, InputStream importFile) throws IOException, SAXException {
      rowProcessor.reset();
      try {
         rowProcessor.setResourceName(name);
         xmlReader.parse(new InputSource(importFile));
      } catch (Exception ex) {
         throw new IOException(String.format("File: [%s]", importFile), ex);
      }
   }

   public void finish() throws OseeCoreException {
      rowProcessor.finish();
   }

   private static enum Table {
      ARTIFACT_TYPE_TABLE, ATTRIBUTE_TYPE_TABLE, ATTRIBUTE_MAP_TABLE, RELATION_TYPE_TABLE, RELATION_SIDE_TABLE
   }

   private final static class InternalRowProcessor implements RowProcessor {
      private boolean isDone;
      private Table currentTable;
      private Iterator<Table> tableIterator;
      private String name;

      private final boolean debugRows;
      private final HashCollection<String, String> superTypeMap; //map each super type to all its direct sub types
      private final ArrayList<ValidityRow> validityArray;
      private final List<AttributeRow> attributeMapRows;
      private final boolean isFlattenHierarchyEnabled;
      private final IOseeDataTypeProcessor dataTypeProcessor;

      public InternalRowProcessor(IOseeDataTypeProcessor dataTypeProcessor, boolean isFlattenHierarchyEnabled, boolean debugRows) {
         this.dataTypeProcessor = dataTypeProcessor;
         this.debugRows = debugRows;
         this.isFlattenHierarchyEnabled = isFlattenHierarchyEnabled;
         superTypeMap = new HashCollection<String, String>(false, HashSet.class);
         validityArray = new ArrayList<ValidityRow>();
         attributeMapRows = new ArrayList<AttributeRow>();

      }

      public void reset() {
         tableIterator = Arrays.asList(Table.values()).iterator();
         isDone = false;
      }

      public String getResourceName() {
         return name;
      }

      public void setResourceName(String name) {
         this.name = name;
      }

      public void reachedEndOfWorksheet() {
         isDone = true;
      }

      public void processHeaderRow(String[] headerRow) {
         if (isDone) {
            return;
         }

         if (tableIterator.hasNext()) {
            Table nextTable = tableIterator.next();
            if (Table.ARTIFACT_TYPE_TABLE.equals(currentTable) && Table.ATTRIBUTE_TYPE_TABLE.equals(nextTable)) {
               createArtifactTypeInheritance();
            }
            currentTable = nextTable;
         } else {
            throw new IllegalArgumentException(
                  "Encountered row past end of last expected table: " + Arrays.deepToString(headerRow));
         }
      }

      public void processRow(String[] row) {
         if (isDone) {
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
            OseeLog.log(ExcelOseeTypeDataParser.class, Level.SEVERE, String.format("ResourceName: [%s]",
                  getResourceName()), ex);
         }
      }

      private void createArtifactTypeInheritance() {
         try {
            ensureAllSuperTypesInheritFromArtifact();

            List<String> items = new ArrayList<String>();
            items.addAll(superTypeMap.keySet());
            Collections.sort(items, new Comparator<String>() {

               @Override
               public int compare(String o1, String o2) {
                  Collection<String> child1 = superTypeMap.getValues(o1);
                  Collection<String> child2 = superTypeMap.getValues(o2);
                  int result;
                  if (child1 == null && child2 == null) {
                     result = 0;
                  } else if (child1 == null && child2 != null) {
                     result = 1;
                  } else if (child1 != null && child2 == null) {
                     result = -1;
                  } else {
                     result = child2.size() - child1.size();
                  }
                  return result;
               }
            });

            for (String key : items) {
               dataTypeProcessor.onArtifactType(false, key);
               Collection<String> descendants = superTypeMap.getValues(key);
               if (descendants != null) {
                  for (String descendant : descendants) {
                     dataTypeProcessor.onArtifactType(false, descendant);
                  }
                  dataTypeProcessor.onArtifactTypeInheritance(key, descendants);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(ExcelOseeTypeDataParser.class, Level.SEVERE, String.format("ResourceName: [%s]",
                  getResourceName()), ex);
         }
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
      }

      private void addRelationType(String[] row) throws Exception {
         if (debugRows) {
            System.out.println("   addRelationType => " + row[0] + "," + row[1]);
         }
         String relationTypeName = row[0];
         String sideAName = row[1];
         //         String abPhrasing = row[2];
         String sideBName = row[3];
         //         String baPhrasing = row[4];
         //         String shortName = row[5];
         String ordered = row[6];
         String defaultOrderTypeGuid = row[7];
         if (defaultOrderTypeGuid == null) {
            if (ordered.equalsIgnoreCase("Yes")) {
               defaultOrderTypeGuid = RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC.getGuid();
            } else {
               defaultOrderTypeGuid = RelationOrderBaseTypes.UNORDERED.getGuid();
            }
         }
         dataTypeProcessor.onRelationType(relationTypeName, sideAName, sideBName, "", "", "", ordered,
               defaultOrderTypeGuid);
      }

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

         dataTypeProcessor.onAttributeType(attrBaseType, attrProviderType, fileTypeExtension, attributeName,
               defaultValue, validityXml, minOccurrence, maxOccurrence, tipText, taggerId);
      }

      private void associateAttribute(String[] row) {
         if (debugRows) {
            System.out.println("   associateAttribute => " + row[0] + "," + row[1]);
         }
         attributeMapRows.add(new AttributeRow(row));
      }

      private void addValidityConstraints(String[] row) {
         validityArray.add(new ValidityRow(row[0], row[1], getQuantity(row[2]), getQuantity(row[3])));
      }

      private int getQuantity(String quantity) {
         if (quantity.equalsIgnoreCase("UNLIMITED")) {
            return Integer.MAX_VALUE;
         }
         return Integer.parseInt(quantity);
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
         dataTypeProcessor.onFinish();
      }

      private void processRelationValidity() throws OseeCoreException {
         if (isFlattenHierarchyEnabled) {
            Collection<String> concreteTypes = new ArrayList<String>();
            CompositeKeyHashMap<String, String, Pair<Integer, Integer>> keyMap =
                  new CompositeKeyHashMap<String, String, Pair<Integer, Integer>>();
            for (ValidityRow row : validityArray) {
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

      public void processEmptyRow() {
      }

      public void processCommentRow(String[] row) {
      }

      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      }

      public void foundStartOfWorksheet(String sheetName) {
      }
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
