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
public class MaterialVariant extends OseeEnum {

   private static final Long ENUM_ID = 452019003L;

   public static final MaterialVariant NONE = new MaterialVariant(0L, "NONE", "");
   public static final MaterialVariant OUTLINED = new MaterialVariant(1L, "OUTLINED", "outlined");
   public static final MaterialVariant ROUND = new MaterialVariant(2L, "ROUND", "round");
   public static final MaterialVariant SHARP = new MaterialVariant(3L, "SHARP", "sharp");
   public static final MaterialVariant TWOTONE = new MaterialVariant(4L, "TWOTONE", "two-tone");

   private final String value;

   public MaterialVariant(long id, String name, String value) {
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
   public static MaterialVariant fromJson(String value) {
      if (value == null) {
         return NONE;
      }
      for (OseeEnum e : NONE.values()) {
         MaterialVariant m = (MaterialVariant) e;
         if (m.getValue().equals(value)) {
            return m;
         }
      }
      return (MaterialVariant) NONE.getDefault();
   }

   public static MaterialVariant getById(Long id) {
      return (MaterialVariant) NONE.get(id);
   }

   public static MaterialVariant getByName(String name) {
      return (MaterialVariant) NONE.get(name);
   }
}