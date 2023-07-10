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
import java.util.List;

/**
 * Represents an Excel Worksheet that contains a header row and data rows
 *
 * @author Donald G. Dunne
 */
public class EWorksheet {

   private EHeaderRow header;
   private List<ERow> rows = new ArrayList<>();
   private EWorkbook workbook;
   private final String name;

   public EWorksheet(EWorkbook workbook, String name) {
      this.workbook = workbook;
      this.name = name;
      this.header = new EHeaderRow(this);
      workbook.addWorksheet(this);
   }

   public EHeaderRow getHeader() {
      return header;
   }

   public void setHeader(EHeaderRow header) {
      this.header = header;
   }

   public List<ERow> getRows() {
      return rows;
   }

   public void setRows(List<ERow> rows) {
      this.rows = rows;
   }

   public EWorkbook getWorkbook() {
      return workbook;
   }

   public void setWorkbook(EWorkbook workbook) {
      this.workbook = workbook;
   }

   public String getName() {
      return name;
   }

   public void addRow(ERow eRow) {
      rows.add(eRow);
   }

   @Override
   public String toString() {
      return "EWorksheet [header=" + header + ",\n rows=" + rows + ",\n name=" + name + "]";
   }

}
