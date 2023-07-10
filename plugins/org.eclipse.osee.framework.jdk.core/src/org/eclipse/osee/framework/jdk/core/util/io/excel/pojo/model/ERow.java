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
 * Represents an Excel row that contains data cells
 *
 * @author Donald G. Dunne
 */
public class ERow {

   protected List<ECell> cells = new ArrayList<>();
   protected int rowNum = 0;
   protected final EWorksheet worksheet;

   public ERow(EWorksheet worksheet, int rowNum) {
      this.worksheet = worksheet;
      this.rowNum = rowNum;
      worksheet.addRow(this);
   }

   public List<ECell> getCells() {
      return cells;
   }

   public void setCells(List<ECell> cells) {
      this.cells = cells;
   }

   public void addCell(ECell cell) {
      cells.add(cell);
   }

   public int getRowNum() {
      return rowNum;
   }

   public void setRowNum(int rowNum) {
      this.rowNum = rowNum;
   }

   public EWorksheet getWorksheet() {
      return worksheet;
   }

   @Override
   public String toString() {
      return "ERow [rowNum=" + rowNum + ", cells=" + cells + "]";
   }

}
