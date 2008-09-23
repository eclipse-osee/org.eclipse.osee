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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class SkynetTypesImporter implements RowProcessor {
   private static final Pattern nonJavaCharP = Pattern.compile("[^a-zA-Z_0-9]");
   private String upCaseEnums = "";
   private String normalEnums = "";

   private enum Table {
      ARTIFACT_TYPE_TABLE, ATTRIBUTE_TYPE_TABLE, ATTRIBUTE_MAP_TABLE, RELATION_TYPE_TABLE, RELATION_SIDE_TABLE
   }

   private static final String description = "Setup artifact, attribute, and relation type data";
   private final ExcelSaxHandler excelHandler;
   private Table currentTable;
   private Iterator<Table> tableIterator;
   private final HashMap<String, ArrayList<String>> superTypeMap;
   private final RelationValidity relationValidity;
   private final List<AttributeMapRow> attributeMapRows;
   private boolean done;
   private final boolean debugRows = false;
   private final XMLReader xmlReader;

   /**
    * @throws SAXException
    * @throws IOException
    * @throws SQLException
    */
   public SkynetTypesImporter(Branch branch) throws SQLException, SAXException, IOException {
      excelHandler = new ExcelSaxHandler(this, true, true);
      superTypeMap = new HashMap<String, ArrayList<String>>();
      relationValidity = new RelationValidity(this, branch);
      attributeMapRows = new ArrayList<AttributeMapRow>();

      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(excelHandler);
   }

   public void extractTypesFromSheet(InputStream importFile) throws IOException, SAXException {
      done = false;
      tableIterator = Arrays.asList(Table.values()).iterator();
      xmlReader.parse(new InputSource(importFile));
   }

   public void finish() throws Exception {
      for (AttributeMapRow attributeRow : attributeMapRows) {
         attributeRow.persist();
      }
      relationValidity.persist();

      String relSideStr = upCaseEnums + "\n\n" + normalEnums;

      try {
         BufferedWriter out;
         out = new BufferedWriter(new FileWriter(OseeData.getFile("RelationSide.java")));
         out.write(relSideStr);
         out.close();
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
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
               relationValidity.addValidityConstraints(row);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
   }

   /**
    * @param row
    * @throws SQLException
    */
   private void associateAttribute(String[] row) throws SQLException {
      if (debugRows) System.out.println("   associateAttribute => " + row[0] + "," + row[1]);
      attributeMapRows.add(new AttributeMapRow(this, row));
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

      AttributeTypeManager.createType(attrBaseType, attrProviderType, fileTypeExtension, "", attributeName,
            defaultValue, validityXml, minOccurrence, maxOccurrence, tipText, taggerId);
   }

   /**
    * @param row
    * @throws OseeCoreException
    */
   private void addRelationType(String[] row) throws OseeCoreException {
      if (debugRows) System.out.println("   addRelationType => " + row[0] + "," + row[1]);
      String relationTypeName = row[0];
      String sideAName = row[1];
      String abPhrasing = row[2];
      String sideBName = row[3];
      String baPhrasing = row[4];
      String shortName = row[5];
      String ordered = row[6];
      generateRelationSideEnum(relationTypeName, sideAName, sideBName);
      generateNormalRelationSideEnum(relationTypeName, sideAName, sideBName);

      RelationTypeManager.createRelationType("", relationTypeName, sideAName, sideBName, abPhrasing, baPhrasing,
            shortName, ordered);
   }

   private void generateRelationSideEnum(String relationTypeName, String sideAName, String sideBName) {
      sideAName = nonJavaCharP.matcher(sideAName).replaceAll("_").toUpperCase();
      sideBName = nonJavaCharP.matcher(sideBName).replaceAll("_").toUpperCase();
      String enumPrefix = nonJavaCharP.matcher(relationTypeName).replaceAll("_").toUpperCase();
      upCaseEnums +=
            String.format("%s__%s(true, \"%s\"), %s__%s(false, \"%s\"), ", enumPrefix, sideAName, relationTypeName,
                  enumPrefix, sideBName, relationTypeName);
   }

   private void generateNormalRelationSideEnum(String relationTypeName, String sideAName, String sideBName) {
      sideAName = nonJavaCharP.matcher(sideAName).replaceAll("");
      sideBName = nonJavaCharP.matcher(sideBName).replaceAll("");
      String enumPrefix = nonJavaCharP.matcher(relationTypeName).replaceAll("");
      normalEnums +=
            String.format("%s_%s(true, \"%s\"), %s_%s(false, \"%s\"),\n", enumPrefix, sideAName, relationTypeName,
                  enumPrefix, sideBName, relationTypeName);
   }

   private void associateWithSuperType(String artifactTypeName, String superTypeName) {
      ArrayList<String> artifactTypeList = superTypeMap.get(superTypeName);
      if (artifactTypeList == null) {
         artifactTypeList = new ArrayList<String>();
         superTypeMap.put(superTypeName, artifactTypeList);
      }
      artifactTypeList.add(artifactTypeName);
      if (!superTypeName.equals("Artifact")) {
         associateWithSuperType(artifactTypeName, "Artifact");
      }
   }

   protected ArrayList<String> determineConcreateTypes(String type) {
      ArrayList<String> artifactTypeList = superTypeMap.get(type);
      if (artifactTypeList == null) {
         artifactTypeList = new ArrayList<String>();
         artifactTypeList.add(type);
      }
      return artifactTypeList;
   }

   /**
    * @param row
    * @throws OseeDataStoreException
    * @throws IllegalStateException
    */
   private void addArtifactType(String[] row) throws SQLException, ClassNotFoundException, IllegalStateException, OseeCoreException {
      if (debugRows) System.out.println("  addArtifactType => " + row[0] + "," + row[1]);
      String factoryClassName = row[0];
      String artifactTypeName = row[1];
      String superTypeName = row[2];

      if (!artifactTypeName.equals("Artifact")) {
         associateWithSuperType(artifactTypeName, superTypeName);
      }
      ArtifactTypeManager.createType(factoryClassName, "", artifactTypeName, artifactTypeName);
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

   public static int getQuantity(String quantity) {
      if (quantity.equalsIgnoreCase("UNLIMITED")) {
         return Integer.MAX_VALUE;
      }
      return Integer.parseInt(quantity);
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
}
