/*******************************************************************************
 * Copyright (c) 2021 Boeing.
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
package org.eclipse.osee.framework.core.data.conditions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class ConditionalOperator extends OseeEnum {

   private static final Long ENUM_ID = 4524209798L;

   public static ConditionalOperator NOT_EQUAL = new ConditionalOperator(111L, "Not Equals");
   public static ConditionalOperator EQUAL = new ConditionalOperator(222L, "Equals");
   public static ConditionalOperator NONE = new ConditionalOperator(333L, "NONE");

   public ConditionalOperator(long id, String name) {
      super(ENUM_ID, id, name);
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
}
