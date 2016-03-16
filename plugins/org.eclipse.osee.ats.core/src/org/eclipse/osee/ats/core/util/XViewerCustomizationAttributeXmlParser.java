/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;

/**
 * @author Morgan Cook
 */
public class XViewerCustomizationAttributeXmlParser {

   private static final String ID = "id";
   private static final String NAME = "name";
   private static final String NAMESPACE = "namespace";
   private static final String GUID = "guid";

   private static final Pattern treePropertiesPattern =
      Pattern.compile("<XTreeProperties " + NAME + "=\"(.*?)\" " + NAMESPACE + "=\"(.*?)\" " + GUID + "=\"(.*?)\">",
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern columnPattern =
      Pattern.compile("<xCol>(.*?)</xCol>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern elementsPattern = Pattern.compile("<(" + ID + "|" + NAME + ")>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private XViewerCustomization customization;

   String xml;

   public XViewerCustomizationAttributeXmlParser(String xml) {
      this.xml = xml;
   }

   private void parseXml() {
      customization = new XViewerCustomization();
      parseTreeProperties();

      //Get all columns
      Matcher columnMatcher = columnPattern.matcher(xml);
      Matcher attrMatcher;
      while (columnMatcher.find()) {
         String columnId = null;
         String columnName = null;
         attrMatcher = elementsPattern.matcher(columnMatcher.group(1));
         while (attrMatcher.find()) {
            if (attrMatcher.group(1).equals(ID)) {
               columnId = attrMatcher.group(2);
            } else if (attrMatcher.group(1).equals(NAME)) {
               columnName = attrMatcher.group(2);
            }
         }

         if (columnId != null && columnName != null) {
            AtsAttributeValueColumn column = new AtsAttributeValueColumn();
            column.setName(columnName);
            column.setId(columnId);
            // column.setAtsColumnId(columnId);
            customization.getColumns().add(column);
         }
      }
   }

   private void parseTreeProperties() {
      Matcher propertiesMatcher = treePropertiesPattern.matcher(xml);
      if (propertiesMatcher.find()) {
         customization.setName(propertiesMatcher.group(1));
         customization.setNamespace(propertiesMatcher.group(2));
         customization.setGuid(propertiesMatcher.group(3));
      }
   }

   public XViewerCustomization getCustomization() {
      if (customization == null) {
         parseXml();
      }
      return customization;
   }

}
