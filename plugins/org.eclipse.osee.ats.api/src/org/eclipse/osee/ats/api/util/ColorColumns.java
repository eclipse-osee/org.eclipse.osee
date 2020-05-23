/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ColorColumns {

   List<ColorColumn> colorColumns = new ArrayList<>();

   public void addColumn(ColorColumn column) {
      colorColumns.add(column);
   }

   public List<ColorColumn> getColorColumns() {
      return colorColumns;
   }

   public void setColorColumns(List<ColorColumn> colorColumns) {
      this.colorColumns = colorColumns;
   }

   public ColorColumn getColumnById(String id) {
      for (ColorColumn column : colorColumns) {
         if (column.getColumnId().equals(id)) {
            return column;
         }
      }
      return null;
   }
}
