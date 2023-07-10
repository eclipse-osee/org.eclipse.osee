/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class ECell {

   private String value = "";
   private ERow row;
   private EHeaderCell col;

   public ECell(ERow row, EHeaderCell col, String value) {
      this.row = row;
      this.col = col;
      this.value = value;
      row.addCell(this);
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public ERow getRow() {
      return row;
   }

   public void setRow(ERow row) {
      this.row = row;
   }

   @Override
   public String toString() {
      return String.format("cell(%s,%s)[%s]", row.getRowNum(), col.getColNum(), value);
   }

   public EHeaderCell getCol() {
      return col;
   }

   public void setCol(EHeaderCell col) {
      this.col = col;
   }

   public Collection<String> getValues(String separator) {
      List<String> values = new ArrayList<>();
      if (Strings.isValid(value)) {
         for (String val : value.split(separator)) {
            values.add(val);
         }
      }
      return values;
   }

}
