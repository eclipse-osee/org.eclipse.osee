/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ColorColumn {
   List<ColorColumnValue> values = new ArrayList<>();
   String columnId;

   public void setColor(String value, String fgHexColor, String bgHexColor) {
      ColorColumnValue colValue = new ColorColumnValue();
      colValue.setValue(value);
      colValue.setBgHexColor(bgHexColor);
      colValue.setFgHexColor(fgHexColor);
      values.add(colValue);
   }

   public String getColumnId() {
      return columnId;
   }

   public void setColumnId(String columnId) {
      this.columnId = columnId;
   }

   public String getBackgroundColorHex(String value) {
      for (ColorColumnValue colValue : values) {
         if (colValue.getValue().equals(value)) {
            return colValue.getBgHexColor();
         }
      }
      return null;
   }

   public String getForgroundColorHex(String value) {
      for (ColorColumnValue colValue : values) {
         if (colValue.getValue().equals(value)) {
            return colValue.getFgHexColor();
         }
      }
      return null;
   }

   public List<ColorColumnValue> getValues() {
      return values;
   }

   public void setValues(List<ColorColumnValue> values) {
      this.values = values;
   }

}
