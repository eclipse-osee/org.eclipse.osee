/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model;

import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class TableData {

   private final String title;
   private final String[] columns;
   private final List<String[]> rows;

   public TableData(String title, String[] columns, List<String[]> rows) {
      this.title = title;
      this.columns = columns;
      this.rows = rows;
   }

   public String getTitle() {
      return title;
   }

   public String[] getColumns() {
      return columns;
   }

   public List<String[]> getRows() {
      return rows;
   }

}
