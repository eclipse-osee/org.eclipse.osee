/*******************************************************************************
 * Copyright (c) 2025 Boeing.
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
package org.eclipse.osee.ats.api.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class AttrValueType extends OseeEnum {

   private static final Long ENUM_ID = 8824387197L;

   public static AttrValueType Value = new AttrValueType(111L, "Value");
   public static AttrValueType AttrExists = new AttrValueType(222L, "AttrExists");
   public static AttrValueType AttrNotExists = new AttrValueType(333L, "AttrNotExists");
   public static AttrValueType AttrExistsValue = new AttrValueType(444L, "AttrExistsValue");
   public static AttrValueType AttrNotExistsValue = new AttrValueType(555L, "AttrNotExistsValue");

   public AttrValueType(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return Value;
   }

}
