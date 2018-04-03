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
package org.eclipse.osee.ote.ui.define.reports.html;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.ui.define.internal.Activator;
import org.eclipse.osee.ote.ui.define.utilities.HtmlStringUtils;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class HtmlActiveTable {
   private static final String TEMPLATE_PATH = "templates/HtmlActiveTableTemplate";

   private static final String ELEMENT_NAME_TAG = "##ELEMENT_NAME##";
   private static final String TABLE_HEIGHT = "##HEIGHT##";
   private static final String TABLE_WIDTH = "##WIDTH##";
   private static final String TABLE_TITLE = "##TABLE_TITLE##";

   private static final String TABLE_DATA_TAG = "##TABLE_DATA##";
   private static final String CUSTOM_FUNCTIONS_TAG = "##CUSTOM_FUNCTIONS##";
   private static final String COLUMN_METADATA_TAG = "##COLUMN_DATA##";
   private static final String COLUMN_CUSTOMIZATIONS_TAG = "##COLUMN_CUSTOMIZATIONS_DATA##";
   private static final String AUTO_EXPAND_ON_COLUMN = "##AUTO_EXPAND_COLUMN##";

   private String elementName;
   private String tableHeight;
   private String tableWidth;
   private String tableTitle;
   private final List<List<String>> rowData;
   private final List<HtmlActiveTableColumnData> columnList;

   public HtmlActiveTable() {
      super();
      this.rowData = new ArrayList<>();
      this.columnList = new ArrayList<>();
      this.elementName = "ID";
      this.tableHeight = "";
      this.tableWidth = "";
      this.tableTitle = this.getClass().getName();
   }

   public void setElementName(String elementName) {
      this.elementName = elementName;
   }

   public void setTableHeight(String tableHeight) {
      this.tableHeight = tableHeight;
   }

   public void setTableWidth(String tableWidth) {
      this.tableWidth = tableWidth;
   }

   public void setTableTitle(String tableTitle) {
      this.tableTitle = tableTitle;
   }

   private URL getTemplate() throws IOException {
      URL url = null;
      Bundle bundle = Activator.getInstance().getBundle();
      if (bundle != null) {
         url = bundle.getEntry(TEMPLATE_PATH);
         url = FileLocator.resolve(url);
      }
      return url;
   }

   public void addColumn(HtmlActiveTableColumnData columnData) {
      this.columnList.add(columnData);
   }

   private String getColumnDataStoreInfo() {
      StringBuilder builder = new StringBuilder();
      int size = columnList.size();
      for (int index = 0; index < size; index++) {
         HtmlActiveTableColumnData data = columnList.get(index);
         builder.append(data.asMetaData());
         if (index + 1 < size) {
            builder.append(",\n");
         }
      }
      return builder.toString();
   }

   private String getColumnCustomizations() {
      StringBuilder builder = new StringBuilder();
      int size = columnList.size();
      for (int index = 0; index < size; index++) {
         HtmlActiveTableColumnData data = columnList.get(index);
         String row = data.asColumnCustomization();

         if (index == 0) {
            row = row.substring(1, row.length());
            builder.append("{ id:");
            String name = data.getName().toLowerCase();
            builder.append(HtmlStringUtils.addSingleQuotes(HtmlStringUtils.escapeString(name)));
            builder.append(", ");
         }
         builder.append(row);
         if (index + 1 < size) {
            builder.append(",\n");
         }
      }
      return builder.toString();
   }

   public String generate() throws IOException {
      URL url = getTemplate();
      String template = Lib.inputStreamToString(url.openStream());

      try {
         template = template.replace(ELEMENT_NAME_TAG, elementName);
         String widthEntry = "autoWidth: true";
         if (Strings.isValid(tableWidth)) {
            widthEntry = "width: " + tableWidth;
         }
         template = template.replace(TABLE_WIDTH, widthEntry);
         String heightEntry = "autoHeight: true";
         if (Strings.isValid(tableHeight)) {
            heightEntry = "height: " + tableHeight;
         }
         template = template.replace(TABLE_HEIGHT, heightEntry);
         template = template.replace(TABLE_TITLE, tableTitle);
         template = template.replace(COLUMN_METADATA_TAG, getColumnDataStoreInfo());
         template = template.replace(TABLE_DATA_TAG, generateTableData());
         template = template.replace(CUSTOM_FUNCTIONS_TAG, getCustomFunction());
         template = template.replace(COLUMN_CUSTOMIZATIONS_TAG, getColumnCustomizations());
         template = template.replace(AUTO_EXPAND_ON_COLUMN, getAutoExpandColumn());
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return template;
   }

   private String getAutoExpandColumn() {
      StringBuilder builder = new StringBuilder();
      if (columnList.size() > 0) {
         builder.append("autoExpandColumn: ");
         HtmlActiveTableColumnData data = columnList.get(0);
         String name = data.getName().toLowerCase();
         builder.append(HtmlStringUtils.addSingleQuotes(HtmlStringUtils.escapeString(name)));
         builder.append(", autoExpandMin: ");
         builder.append(data.getWidth());
         builder.append(", autoExpandMax: ");
         builder.append(data.getWidth() * 10);
      }
      return builder.toString();
   }

   private String generateTableData() {
      StringBuilder builder = new StringBuilder();
      for (int index = 0; index < rowData.size(); index++) {
         builder.append(processRow(rowData.get(index)));
         if (index + 1 < rowData.size()) {
            builder.append(", ");
         }
      }
      return builder.toString();
   }

   private String processRow(List<String> values) {
      StringBuilder builder = new StringBuilder();
      builder.append("[ ");
      for (int index = 0; index < values.size(); index++) {
         String rawData = values.get(index);
         String data = HtmlStringUtils.escapeString(rawData != null ? rawData : "");
         HtmlActiveTableColumnData metaData = columnList.get(index);
         if (Strings.isValid(metaData.getType()) != true || metaData.getType().equalsIgnoreCase("string")) {
            data = HtmlStringUtils.addSingleQuotes(data);
         }
         builder.append(data);
         if (index + 1 < values.size()) {
            builder.append(",");
         }
      }
      builder.append(" ]\n");
      return builder.toString();
   }

   public void addDataRow(String... values) {
      this.rowData.add(Arrays.asList(values));
   }

   public String getElementName() {
      return elementName;
   }

   private String getCustomFunction() {
      StringBuilder builder = new StringBuilder();
      // TODO: Create Custom Colors
      //   function change(val){
      //      if(val > 0){
      //          return '<span style="color:green;">' + val + '</span>';
      //      }else if(val < 0){
      //          return '<span style="color:red;">' + val + '</span>';
      //      }
      //      return val;
      //  }
      return builder.toString();
   }

}
