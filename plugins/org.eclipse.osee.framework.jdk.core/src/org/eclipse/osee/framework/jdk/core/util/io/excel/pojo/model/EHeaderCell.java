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

import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * Represents an Excel header row cell that contains a header name
 *
 * @author Donald G. Dunne
 */
public class EHeaderCell {

   private EHeaderRow headerRow;
   private String name;
   private int colNum;
   private Object storeType;

   public EHeaderCell(EHeaderRow headerRow, String name, int colNum) {
      this.headerRow = headerRow;
      this.name = name;
      this.colNum = colNum;
      headerRow.addCell(this);
   }

   public EHeaderRow getHeaderRow() {
      return headerRow;
   }

   public void setHeaderRow(EHeaderRow headerRow) {
      this.headerRow = headerRow;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getColNum() {
      return colNum;
   }

   public void setColNum(int colNum) {
      this.colNum = colNum;
   }

   @Override
   public String toString() {
      return "EHeaderCell [colNum=" + colNum + ", name=" + name + "]";
   }

   public boolean is(NamedId namedId) {
      return namedId.getName().equals(getName());
   }

   public Object getStoreType() {
      return storeType;
   }

   public void setStoreType(Object storeType) {
      this.storeType = storeType;
   }

}
