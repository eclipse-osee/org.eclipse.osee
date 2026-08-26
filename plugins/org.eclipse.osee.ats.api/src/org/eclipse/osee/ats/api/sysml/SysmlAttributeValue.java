/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.api.sysml;

import java.util.List;

/**
 * Represents an attribute value assignment in a SysML V2 part usage instance.
 *
 * @author Donald G. Dunne
 */
public class SysmlAttributeValue {

   private final String name;
   private final Object value;
   private final boolean isEnum;
   private final String enumDefName;

   public SysmlAttributeValue(String name, Object value) {
      this(name, value, false, null);
   }

   public SysmlAttributeValue(String name, Object value, boolean isEnum, String enumDefName) {
      this.name = name;
      this.value = value;
      this.isEnum = isEnum;
      this.enumDefName = enumDefName;
   }

   public String getName() {
      return name;
   }

   public Object getValue() {
      return value;
   }

   public boolean isEnum() {
      return isEnum;
   }

   public String getEnumDefName() {
      return enumDefName;
   }

   public boolean isMultiValued() {
      return value instanceof List;
   }
}
