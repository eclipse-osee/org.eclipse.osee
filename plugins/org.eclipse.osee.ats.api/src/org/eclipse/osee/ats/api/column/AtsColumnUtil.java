/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.api.column;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

public class AtsColumnUtil {

   private AtsColumnUtil() {
      // Utility class
   }

   public static XViewerAlign getXViewerAlign(ColumnAlign align) {
      if (align == ColumnAlign.Center) {
         return XViewerAlign.Center;
      }
      if (align == ColumnAlign.Right) {
         return XViewerAlign.Right;
      }
      return XViewerAlign.Left;
   }

   public static ColumnAlign getColumnAlign(XViewerAlign xViewerAlign) {
      if (xViewerAlign == XViewerAlign.Center) {
         return ColumnAlign.Center;
      }
      if (xViewerAlign == XViewerAlign.Right) {
         return ColumnAlign.Right;
      }
      return ColumnAlign.Left;
   }

   public static SortDataType getSortDataType(AtsCoreColumnToken colType) {
      return getSortDataType(colType.getColumnType());
   }

   public static SortDataType getSortDataType(String type) {
      SortDataType sortType = SortDataType.String;
      try {
         sortType = SortDataType.valueOf(type);
      } catch (Exception ex) {
         // do nothing
      }
      return sortType;
   }

   public static int getColumnWidth(AttributeTypeToken attrType) {
      int width = 120;
      if (attrType.isEnumerated()) {
         width = 40;
      } else if (attrType.isBoolean()) {
         width = 50;
      } else if (attrType.isInteger()) {
         width = 45;
      } else if (attrType.isDouble()) {
         width = 40;
      } else if (attrType.isDate()) {
         width = 80;
      }
      return width;
   }

   public static ColumnType getColumnType(AttributeTypeToken attributeType) {
      ColumnType columnType = ColumnType.String;
      try {
         if (attributeType.isDate()) {
            columnType = ColumnType.Date;
         } else if (attributeType.isDouble()) {
            columnType = ColumnType.Float;
         } else if (attributeType.isInteger()) {
            columnType = ColumnType.Integer;
         } else if (attributeType.isLong()) {
            columnType = ColumnType.Integer;
         } else if (attributeType.isBoolean()) {
            columnType = ColumnType.Boolean;
         } else if (attributeType.isString()) {
            columnType = ColumnType.Paragraph_Number;
         }
      } catch (Exception ex) {
         //do nothing
      }
      return columnType;
   }

   public static ColumnAlign getColumnAlign(AttributeTypeToken attrType) {
      ColumnAlign columnAlign = ColumnAlign.Left;
      if (attrType.isInteger()) {
         columnAlign = ColumnAlign.Center;
      } else if (attrType.isDouble()) {
         columnAlign = ColumnAlign.Center;
      }
      return columnAlign;
   }

   public static ColumnAlign getColumnAlign(String align) {
      ColumnAlign colAlign = ColumnAlign.Left;
      try {
         colAlign = ColumnAlign.valueOf(align);
      } catch (Exception ex) {
         // do nothing
      }
      return colAlign;
   }

}
