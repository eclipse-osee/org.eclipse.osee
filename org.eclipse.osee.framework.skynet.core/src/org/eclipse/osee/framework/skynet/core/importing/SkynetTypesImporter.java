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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class SkynetTypesImporter implements RowProcessor {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SkynetTypesImporter.class);

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

   private Branch branch;

   private static final ConfigurationPersistenceManager configurationManager =
         ConfigurationPersistenceManager.getInstance();

   /**
    * @throws SAXException
    * @throws IOException
    * @throws SQLException
    */
   public SkynetTypesImporter(Branch branch) throws SQLException, SAXException, IOException {

      this.branch = branch;
      excelHandler = new ExcelSaxHandler(this, true, true);
      superTypeMap = new HashMap<String, ArrayList<String>>();
      relationValidity = new RelationValidity(this, branch);
      attributeMapRows = new ArrayList<AttributeMapRow>();

      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(excelHandler);

      configurationManager.startBatch(branch);
   }

   public void extractTypesFromSheet(InputStream importFile) throws IOException, SAXException {
      done = false;
      tableIterator = Arrays.asList(Table.values()).iterator();
      xmlReader.parse(new InputSource(importFile));
   }

   public void finish() throws SQLException, CoreException, IOException {
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

         configurationManager.executeBatch();
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
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
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (ClassNotFoundException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   /**
    * @param row
    * @throws SQLException
    */
   private void associateAttribute(String[] row) throws SQLException {
      if (debugRows) System.out.println("   associateAttribute => " + row[0] + "," + row[1]);
      attributeMapRows.add(new AttributeMapRow(this, row, branch));
   }

   /**
    * @param row
    * @throws ClassNotFoundException
    * @throws SQLException
    */
   private void addAttributeType(String[] row) throws ClassNotFoundException, SQLException {
      if (debugRows) System.out.println("   addAttributeType => " + row[0] + "," + row[1]);
      String attrBaseType = row[0];
      String attributeName = row[1];
      String defaultValue = row[2];
      String validityXml = row[3];
      int minOccurrence = getQuantity(row[4]);
      int maxOccurrence = getQuantity(row[5]);
      String tipText = row[6];

      String basePackageName = Attribute.class.getPackage().getName();
      Class<? extends Attribute> baseAttributeClass =
            Class.forName(basePackageName + "." + attrBaseType, true, Attribute.class.getClassLoader()).asSubclass(
                  Attribute.class);

      configurationManager.makePersistent(baseAttributeClass, attributeName, defaultValue, validityXml, minOccurrence,
            maxOccurrence, tipText);
   }

   /**
    * @param row
    */
   private void addRelationType(String[] row) {
      if (debugRows) System.out.println("   addRelationType => " + row[0] + "," + row[1]);
      String relationTypeName = row[0];
      String sideAName = row[1];
      String A2BPhrase = row[2];
      String sideBName = row[3];
      String B2APhrase = row[4];
      String shortName = row[5];
      generateRelationSideEnum(relationTypeName, sideAName, sideBName);
      generateNormalRelationSideEnum(relationTypeName, sideAName, sideBName);

      configurationManager.persistRelationLinkType(relationTypeName, sideAName, sideBName, A2BPhrase, B2APhrase,
            shortName, branch);
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
    */
   private void addArtifactType(String[] row) throws SQLException, ClassNotFoundException {
      if (debugRows) System.out.println("  addArtifactType => " + row[0] + "," + row[1]);
      String factoryClassName = row[0];
      String artifactTypeName = row[1];
      String superTypeName = row[2];

      associateWithSuperType(artifactTypeName, superTypeName);

      if (configurationManager.getArtifactSubtypeDescriptor(artifactTypeName, branch) == null) {
         configurationManager.makeSubtypePersistent(factoryClassName, artifactTypeName, branch);

         addValidityAlreadyInDb(artifactTypeName, superTypeName);

         // Hack for the sake that the current inheritance is only ever 2 levels deep
         // i.e. assume that the superType (since it is not "Artifact") must itself inherit from
         // "Artifact"
         if (!superTypeName.equals("Artifact")) {
            addValidityAlreadyInDb(artifactTypeName, "Artifact");
         }
      }
   }

   private void addValidityAlreadyInDb(String artifactTypeName, String superTypeName) throws SQLException {
      if (superTypeName.equals("Artifact")) {
         superTypeName = "Root Artifact"; // this is a concrete type that should be on every branch
      }
      ArtifactSubtypeDescriptor superArtifactType =
            configurationManager.getArtifactSubtypeDescriptor(superTypeName, branch);
      ArtifactSubtypeDescriptor artifactType =
            configurationManager.getArtifactSubtypeDescriptor(artifactTypeName, branch);

      if (superArtifactType != null) {

         Collection<DynamicAttributeDescriptor> parentAttributes =
               configurationManager.getAttributeTypesFromArtifactType(superArtifactType);
         Iterator<DynamicAttributeDescriptor> it = parentAttributes.iterator();
         while (it.hasNext()) {
            configurationManager.persistAttributeValidity(it.next(), artifactType);
         }

         Collection<IRelationLinkDescriptor> links =
               RelationPersistenceManager.getInstance().getIRelationLinkDescriptors(superArtifactType);
         Iterator<IRelationLinkDescriptor> linksIt = links.iterator();
         while (linksIt.hasNext()) {
            IRelationLinkDescriptor desc = linksIt.next();
            int sideAmax = desc.getRestrictionSizeFor(superArtifactType.getArtTypeId(), true);
            int sideBmax = desc.getRestrictionSizeFor(superArtifactType.getArtTypeId(), false);
            configurationManager.persistRelationLinkValidity(artifactType, desc, sideAmax, sideBmax);
         }
      }
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
