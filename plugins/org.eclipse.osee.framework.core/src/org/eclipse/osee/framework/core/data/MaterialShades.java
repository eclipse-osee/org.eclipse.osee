/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * Possible shades for Angular Material colors. Smaller numbers are lighter shades, larger numbers are darker shades.
 * NONE should only be used when the color is set to NONE
 *
 * @author Ryan T. Baldwin
 * @author Jaden W. Puckett
 */
public class MaterialShades extends OseeEnum {

   private static final Long ENUM_ID = 452019002L;

   public static final MaterialShades NONE = new MaterialShades(0L, "NONE", "");
   public static final MaterialShades S50 = new MaterialShades(1L, "S50", "50");
   public static final MaterialShades S100 = new MaterialShades(2L, "S100", "100");
   public static final MaterialShades S200 = new MaterialShades(3L, "S200", "200");
   public static final MaterialShades S300 = new MaterialShades(4L, "S300", "300");
   public static final MaterialShades S400 = new MaterialShades(5L, "S400", "400");
   public static final MaterialShades S500 = new MaterialShades(6L, "S500", "500");
   public static final MaterialShades S600 = new MaterialShades(7L, "S600", "600");
   public static final MaterialShades S700 = new MaterialShades(8L, "S700", "700");
   public static final MaterialShades S800 = new MaterialShades(9L, "S800", "800");
   public static final MaterialShades S900 = new MaterialShades(10L, "S900", "900");

   private final String value;

   public MaterialShades(long id, String name, String value) {
      super(ENUM_ID, id, name);
      this.value = value;
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return NONE;
   }

   public String getValue() {
      return value;
   }

   @JsonValue
   public String toJson() {
      return value;
   }

   @JsonCreator
   public static MaterialShades fromJson(String value) {
      if (value == null) {
         return NONE;
      }
      for (OseeEnum e : NONE.values()) {
         MaterialShades m = (MaterialShades) e;
         if (m.getValue().equals(value)) {
            return m;
         }
      }
      return (MaterialShades) NONE.getDefault();
   }

   public static MaterialShades getById(Long id) {
      return (MaterialShades) NONE.get(id);
   }

   public static MaterialShades getByName(String name) {
      return (MaterialShades) NONE.get(name);
   }
}
