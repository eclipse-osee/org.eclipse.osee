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
package org.eclipse.osee.framework.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SkynetTypesEnumGenerator implements RowProcessor {
   private static final Pattern nonJavaCharP = Pattern.compile("[^a-zA-Z_0-9]");

   private static final String relationImports =
         "import java.sql.SQLException;\n" + "import org.eclipse.osee.framework.skynet.core.artifact.Branch;\n" + "import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;\n" + "import org.eclipse.osee.framework.skynet.core.relation.RelationLink;\n" + "import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;\n" + "import org.eclipse.osee.framework.skynet.core.relation.RelationSide;\n" + "import org.eclipse.osee.framework.skynet.core.relation.RelationType;\n" + "import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;\n\n";

   private static final String relationEnumCode =
         "   private boolean sideA;\n" + "\n" + "   private String typeName;\n" + "\n" + "   private CLASSNAME_PLACEHOLDER(boolean sideA, String typeName) {\n" + "      this.sideA = sideA;\n" + "      this.typeName = typeName;\n" + "      RelationPersistenceManager.sideHash.put(typeName, sideA, this);\n" + "   }\n" + "		\n" + "   public static IRelationEnumeration getRelationSide(String relationType, String relationSide, Branch branch)  throws SQLException {\n" + "      RelationType desc = RelationTypeManager.getType(relationType);\n" + "      boolean isSideA = (desc.getSideAName().equals(relationSide));\n" + "      return RelationPersistenceManager.sideHash.get(relationType, isSideA);\n" + "   }\n" + "\n" + "   /**\n" + "    * @return Returns the sideName.\n" + "    */\n" + "   public boolean isSideA() {\n" + "      return sideA;\n" + "   }\n" + "\n" + "   public String getSideName(Branch branch)  throws SQLException  {\n" + "      if (isSideA())\n" + "         return getRelationType().getSideAName();\n" + "      else\n" + "        return getRelationType().getSideBName();\n" + "   }\n" + "\n" + "   /**\n" + "    * @return Returns the typeName.\n" + "    */\n" + "   public String getTypeName() {\n" + "      return typeName;\n" + "   }\n" + "\n" + "   public RelationType getRelationType() throws SQLException {\n" + "      return RelationTypeManager.getType(typeName);\n" + "   }\n" + "   \n" + "   public boolean isThisType(RelationLink link) {\n" + "      return link.getRelationType().getTypeName().equals(typeName);\n}\n\n" + "   public RelationSide getSide() { \n" + "      return sideA ? RelationSide.SIDE_A : RelationSide.SIDE_B;\n" + "}\n\n" + "   public String getSideName() throws SQLException {\n" + "      if (isSideA()) {\n" + "         return getRelationType().getSideAName();\n" + "      } else {\n" + "         return getRelationType().getSideBName();\n}\n}\n";

   private enum Table {
      ARTIFACT_TYPE_TABLE, ATTRIBUTE_TYPE_TABLE, ATTRIBUTE_MAP_TABLE, RELATION_TYPE_TABLE, RELATION_SIDE_TABLE
   }

   private static final String description = "Setup artifact, attribute, and relation type data";

   private final ExcelSaxHandler excelHandler;

   private Table currentTable;

   private Iterator<Table> tableIterator;

   private boolean done;

   private final XMLReader xmlReader;

   private TreeSet<String> artifacts;

   private TreeSet<String> attributes;

   private TreeSet<String> relations;

   private String sheetName;

   private File destinationDir;

   private static final String skynetTypeCode =
         "   private String name;\n   private CLASSNAME_PLACEHOLDER(String name){ this.name = name; }\n   public String getName(){ return this.name;}\n";

   /**
    * @throws SAXException
    */
   public SkynetTypesEnumGenerator() throws SAXException {

      excelHandler = new ExcelSaxHandler(this, true, true);

      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(excelHandler);

      artifacts = new TreeSet<String>();
      attributes = new TreeSet<String>();
      relations = new TreeSet<String>();
   }

   public void extractTypesFromSheet(File importFile, File destinationDir) throws IOException, SAXException {
      done = false;
      tableIterator = Arrays.asList(Table.values()).iterator();
      xmlReader.parse(new InputSource(new FileInputStream(importFile)));

      this.destinationDir =
            (destinationDir != null && destinationDir.isDirectory()) ? destinationDir : importFile.getParentFile();
   }

   public void finish() throws IOException {
      BufferedWriter out;
      //relation enum gen
      String relClassName = this.sheetName + "_RELATIONS";
      out = new BufferedWriter(new FileWriter(new File(destinationDir, relClassName + ".java")));
      out.append("\n\n");
      out.append(relationImports);
      out.append("public enum ");
      out.append(relClassName);
      out.append(" implements IRelationEnumeration {\n");
      Iterator<String> it = relations.iterator();
      while (it.hasNext()) {
         out.append("   ");
         out.append(it.next());
         if (it.hasNext()) out.append(",\n");
      }
      out.append(";\n");
      out.append(relationEnumCode.replace("CLASSNAME_PLACEHOLDER", relClassName));
      out.append("}");
      out.close();
      //attribute enum gen
      String attrClassName = this.sheetName + "_ATTRIBUTES";
      out = new BufferedWriter(new FileWriter(new File(destinationDir, attrClassName + ".java")));
      out.append("\n\nimport org.eclipse.osee.framework.skynet.core.ISkynetType;\n\npublic enum ");
      out.append(attrClassName);
      out.append(" implements ISkynetType {\n");
      it = attributes.iterator();
      while (it.hasNext()) {
         out.append("   ");
         out.append(it.next());
         if (it.hasNext()) out.append(",\n");
      }
      out.append(";\n\n");
      out.append(skynetTypeCode.replace("CLASSNAME_PLACEHOLDER", attrClassName));
      out.append("}");
      out.close();
      //artifact enum gen
      String artClassName = this.sheetName + "_ARTIFACTS";
      out = new BufferedWriter(new FileWriter(new File(destinationDir, artClassName + ".java")));
      out.append("\n\nimport org.eclipse.osee.framework.skynet.core.ISkynetType;\n\npublic enum ");
      out.append(artClassName);
      out.append(" implements ISkynetType {\n");
      it = artifacts.iterator();
      while (it.hasNext()) {
         out.append("   ");
         out.append(it.next());
         if (it.hasNext()) out.append(",\n");
      }
      out.append(";\n\n");
      out.append(skynetTypeCode.replace("CLASSNAME_PLACEHOLDER", artClassName));
      out.append("}");
      out.close();
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
            case RELATION_TYPE_TABLE:
               addRelationType(row);
               break;
            default:
               break;
         }
      } catch (Exception ex) {
         OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   /**
    * @param row
    * @throws ClassNotFoundException
    */
   private void addAttributeType(String[] row) throws ClassNotFoundException {
      //      String attrBaseType = row[0];
      //      String attrProviderType = row[1];
      String attributeName = row[2];
      int index = attributeName.lastIndexOf(".");
      if (index != -1 && index + 1 < attributeName.length()) {
         attributeName = attributeName.substring(index + 1, attributeName.length());
      }
      //      String fileTypeExtension = row[3];
      //      String defaultValue = row[4];
      //      String validityXml = row[5];
      //      int minOccurrence = getQuantity(row[6]);
      //      int maxOccurrence = getQuantity(row[7]);
      //      String tipText = row[8];

      //      if (fileTypeExtension == null) {
      //         fileTypeExtension = "";
      //      }

      attributes.add(nonJavaCharP.matcher(attributeName).replaceAll("_").toUpperCase() + "(\"" + attributeName + "\")");
   }

   /**
    * @param row
    */
   private void addRelationType(String[] row) {

      String relationTypeName = row[0];
      String sideAName = row[1];
      // String A2BPhrase = row[2];
      String sideBName = row[3];
      // String B2APhrase = row[4];
      // String shortName = row[5];
      generateRelationSideEnum(relationTypeName, sideAName, sideBName);
   }

   private void generateRelationSideEnum(String relationTypeName, String sideAName, String sideBName) {
      sideAName = nonJavaCharP.matcher(sideAName).replaceAll("_").toUpperCase();
      sideBName = nonJavaCharP.matcher(sideBName).replaceAll("_").toUpperCase();
      String enumPrefix = nonJavaCharP.matcher(relationTypeName).replaceAll("_").toUpperCase();
      relations.add(String.format("%s__%s(true, \"%s\"),\n   %s__%s(false, \"%s\")", enumPrefix, sideAName,
            relationTypeName, enumPrefix, sideBName, relationTypeName));
   }

   /**
    * @param row
    */
   private void addArtifactType(String[] row) {
      // String factoryClassName = row[0];
      String artifactTypeName = row[1];
      // String superTypeName = row[2];

      artifacts.add(nonJavaCharP.matcher(artifactTypeName).replaceAll("_").toUpperCase() + "(\"" + artifactTypeName + "\")");
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
      this.sheetName = nonJavaCharP.matcher(sheetName).replaceAll("_").toUpperCase();
   }
}
