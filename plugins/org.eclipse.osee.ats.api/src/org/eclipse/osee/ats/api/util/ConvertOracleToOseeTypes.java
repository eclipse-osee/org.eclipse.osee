/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import com.google.common.base.CaseFormat;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.WordUtils;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.HexUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class ConvertOracleToOseeTypes {

   public ConvertOracleToOseeTypes() {
   }

   public void run() {
      try {
         /**
          * Export the trax issues table into issues.txt and change path below before running.
          */
         String folder = "C:/UserData/TraxIssues/";
         String file = Lib.fileToString(new File(folder + "issues.txt"));
         StringBuilder oseeAttrDefFile = new StringBuilder();
         StringBuilder oseeAttrArtFile = new StringBuilder();
         StringBuilder typesFile = new StringBuilder();
         StringBuilder workDefFile = new StringBuilder();
         StringBuilder workDefWidgets = new StringBuilder();
         for (String line : file.split("\r\n")) {
            System.out.println("line: " + line);
            String[] items = line.split(",");
            String origName = items[0];
            String type = items[1];
            String javaFieldName = "issue" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, origName);
            String attrDisplayName = WordUtils.capitalizeFully(origName, new char[] {'_'}).replaceAll("_", " ");
            String attrTypeName = "lba.issue." + attrDisplayName;
            System.out.println(
               "name: [" + origName + "] attrType: [" + attrTypeName + "] java: [" + javaFieldName + "] attrDisplay: [" + attrDisplayName + "]");
            String hexId = HexUtil.toString(Lib.generateArtifactIdAsInt());
            if (type.equals("VARCHAR") || type.equals("CLOB") || type.equals("DATE")) {
               replaceNameAndId(oseeAttrDefFile, attrTypeName, hexId, getVarCharAttrOseeTypes());
               replaceNameAndId(typesFile, attrTypeName, hexId, getAttrJavaType(javaFieldName, attrTypeName));
               replaceNameAndId(oseeAttrArtFile, attrTypeName, hexId, getArtAttrJavaType());

               boolean fillVertically = type.equals("CLOB");
               if (type.equals("CLOB") || type.equals("VARCHAR")) {
                  String workDefStr = getWorkDefStrAttr(attrDisplayName, attrTypeName, "XTextDam", fillVertically);
                  workDefFile.append(workDefStr);
               } else if (type.equals("DATE")) {
                  String workDefStr = getWorkDefStrAttr(attrDisplayName, attrTypeName, "XDateDam", fillVertically);
                  workDefFile.append(workDefStr);
               }
            } else if (type.equals("NUMBER")) {
               replaceNameAndId(oseeAttrDefFile, attrTypeName, hexId, getIntegerAttrOseeTypes());
               replaceNameAndId(typesFile, attrTypeName, hexId, getAttrJavaType(javaFieldName, attrTypeName));
               replaceNameAndId(oseeAttrArtFile, attrTypeName, hexId, getArtAttrJavaType());

               String workDefStr = getWorkDefStrAttr(attrDisplayName, attrTypeName, "XIntegerDam", false);
               workDefFile.append(workDefStr);

            } else {
               throw new OseeArgumentException("Unhandled attribute type");
            }
            workDefWidgets.append("widget \"" + attrDisplayName + "\"\n");
         }
         Lib.writeStringToFile(oseeAttrDefFile.toString(), new File(folder + "out.osee"));
         typesFile.append(oseeAttrArtFile.toString());
         Lib.writeStringToFile(typesFile.toString(), new File(folder + "out.java"));
         workDefFile.append(workDefWidgets.toString());
         Lib.writeStringToFile(workDefFile.toString(), new File(folder + "out.ats"));
      } catch (IOException ex) {
         ex.printStackTrace();
      }

   }

   private String getWorkDefStrAttr(String name, String attrName, String widget, boolean fillVertically) {
      return String.format("   widgetDefinition \"%s\" {\n" + //
         "      attributeName \"%s\"\n" + //
         "      xWidgetName \"%s\"\n" + //
         (fillVertically ? "      option FILL_VERTICALLY\n" : "") + //
         "   }\n\n", name, attrName, widget);
   }

   private String getArtAttrJavaType() {
      return "attribute \"NAME\"\n";
   }

   private String getIntegerAttrOseeTypes() {
      return "attributeType \"NAME\" extends IntegerAttribute {\n" + //
         "   id ID\n" + //
         "   dataProvider DefaultAttributeDataProvider\n" + //
         "   min 0\n" + //
         "   max 1\n" + //
         "   mediaType \"text/plain\"\n" + //
         "}\n\n";
   }

   public String getAttrJavaType(String javaFieldName, String attrTypeName) {
      return String.format("public static final AttributeTypeId %s = AttributeTypeToken.valueOf(IDL, \"%s\");\n",
         javaFieldName, attrTypeName);
   }

   private void replaceNameAndId(StringBuilder sb, String attrTypeName, String hexId, String str) {
      str = str.replaceFirst("NAME", attrTypeName);
      str = str.replaceAll("ID", hexId);
      sb.append(str);
   }

   public static void main(String[] args) {
      ConvertOracleToOseeTypes convert = new ConvertOracleToOseeTypes();
      convert.run();
   }

   public String getVarCharAttrOseeTypes() {
      return "attributeType \"NAME\" extends StringAttribute {\n" + //
         "   id ID\n" + //
         "   dataProvider DefaultAttributeDataProvider\n" + //
         "   min 0\n" + //
         "   max 1\n" + //
         "   mediaType \"text/plain\"\n" + //
         "}\n\n";
   }
}
