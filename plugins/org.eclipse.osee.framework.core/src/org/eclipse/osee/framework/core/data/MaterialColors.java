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
 * @author Ryan T. Baldwin
 * @author Jaden W. Puckett
 */
public class MaterialColors extends OseeEnum {

   private static final Long ENUM_ID = 452019001L;

   public static final MaterialColors NONE = new MaterialColors(0L, "NONE", "");
   public static final MaterialColors BLUE = new MaterialColors(1L, "BLUE", "primary");
   public static final MaterialColors GREEN = new MaterialColors(2L, "GREEN", "success");
   public static final MaterialColors RED = new MaterialColors(3L, "RED", "warning");
   public static final MaterialColors YELLOW = new MaterialColors(4L, "YELLOW", "accent");

   private final String value;

   public MaterialColors(long id, String name, String value) {
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
   public static MaterialColors fromJson(String value) {
      if (value == null) {
         return NONE;
      }
      for (OseeEnum e : NONE.values()) {
         MaterialColors mc = (MaterialColors) e;
         if (mc.getValue().equals(value)) {
            return mc;
         }
      }
      return (MaterialColors) NONE.getDefault();
   }

   public static MaterialColors getById(Long id) {
      return (MaterialColors) NONE.get(id);
   }

   public static MaterialColors getByName(String name) {
      return (MaterialColors) NONE.get(name);
   }
}