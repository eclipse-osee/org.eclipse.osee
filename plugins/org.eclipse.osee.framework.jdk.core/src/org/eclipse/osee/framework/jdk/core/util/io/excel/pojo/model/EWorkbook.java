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

/**
 * Represents an Excel Workbook that contains Worksheets (tabs)
 *
 * @author Donald G. Dunne
 */
public class EWorkbook {

   public List<EWorksheet> sheets = new ArrayList<>();
   private String name;
   private EFile file;

   public EWorkbook(String name, EFile file) {
      this.name = name;
      this.file = file;
   }

   public List<EWorksheet> getSheets() {
      return sheets;
   }

   public void setSheets(List<EWorksheet> sheets) {
      this.sheets = sheets;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public EFile getFile() {
      return file;
   }

   public void setFile(EFile file) {
      this.file = file;
   }

   public void addWorksheet(EWorksheet sheet) {
      sheets.add(sheet);
   }

   @Override
   public String toString() {
      return "EWorkbook [name=" + name + ",\n sheets=" + sheets + "]";
   }

}
