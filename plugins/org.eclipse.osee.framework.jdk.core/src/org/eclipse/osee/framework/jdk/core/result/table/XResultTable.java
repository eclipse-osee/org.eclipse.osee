/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.result.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class XResultTable {

   public String name = "";
   public List<XResultTableColumn> columns = new ArrayList<XResultTableColumn>();
   public List<XResultTableRow> rows = new ArrayList<>();

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<XResultTableColumn> getColumns() {
      return columns;
   }

   public void setColumns(List<XResultTableColumn> columns) {
      this.columns = columns;
   }

   public Collection<XResultTableRow> getRows() {
      return rows;
   }

   public void setRows(List<XResultTableRow> rows) {
      this.rows = rows;
   }

}
