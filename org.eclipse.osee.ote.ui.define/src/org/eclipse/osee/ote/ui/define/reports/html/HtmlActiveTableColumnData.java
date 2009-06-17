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

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.ui.define.utilities.HtmlStringUtils;

/**
 * @author Roberto E. Escobar
 */
public class HtmlActiveTableColumnData {

   private String name;
   private String type;
   private String dataFormat;
   private boolean sortable;
   private int width;

   public HtmlActiveTableColumnData(String name) {
      this(name, "");
   }

   public HtmlActiveTableColumnData(String name, String type) {
      super();
      this.name = name;
      this.type = type;
      this.dataFormat = "";
      this.sortable = true;
      this.width = name.length();
   }

   public String asMetaData() {
      StringBuilder builder = new StringBuilder();
      builder.append("{");
      builder.append("name: ");
      builder.append(escapeString(getName().toLowerCase()));
      if (Strings.isValid(getType())) {
         builder.append(", type: ");
         builder.append(escapeString(getType()));
      }
      if (Strings.isValid(getDataFormat())) {
         builder.append(", dateFormat: ");
         builder.append(escapeString(getDataFormat()));
      }
      builder.append("}");
      return builder.toString();
   }

   public String asColumnCustomization() {
      StringBuilder builder = new StringBuilder();
      builder.append("{");
      builder.append("header: ");
      builder.append("\"");
      builder.append(HtmlStringUtils.escapeString(getName()));
      builder.append("\"");
      //      builder.append(", width: ");
      //      builder.append(getWidth());
      builder.append(", sortable: ");
      builder.append(isSortable());

      //TODO: Add special renderer calls 

      builder.append(", dataIndex: ");
      builder.append(escapeString(getName().toLowerCase()));
      builder.append("}");
      return builder.toString();
   }

   private String escapeString(String value) {
      return HtmlStringUtils.addSingleQuotes(HtmlStringUtils.escapeString(value));
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getDataFormat() {
      return dataFormat;
   }

   public void setDataFormat(String dataFormat) {
      this.dataFormat = dataFormat;
   }

   public boolean isSortable() {
      return sortable;
   }

   public void setSortable(boolean sortable) {
      this.sortable = sortable;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

}
