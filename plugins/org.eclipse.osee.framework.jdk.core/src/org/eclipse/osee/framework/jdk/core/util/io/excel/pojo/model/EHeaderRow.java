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
 * Represents an Excel header row that contains a header name
 *
 * @author Donald G. Dunne
 */
public class EHeaderRow {

   protected List<EHeaderCell> hcells = new ArrayList<>();
   protected final EWorksheet worksheet;

   public EHeaderRow(EWorksheet worksheet) {
      this.worksheet = worksheet;
      worksheet.setHeader(this);
   }

   public void addCell(EHeaderCell cell) {
      hcells.add(cell);
   }

   public EWorksheet getWorksheet() {
      return worksheet;
   }

   public List<EHeaderCell> getHcells() {
      return hcells;
   }

   public void setHcells(List<EHeaderCell> hcells) {
      this.hcells = hcells;
   }

   @Override
   public String toString() {
      return "EHeaderRow [hcells=" + hcells + "]";
   }

}
