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

package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Ryan D. Brooks
 */
@OseeAttribute("IntegerAttribute")
public class IntegerAttribute extends CharacterBackedAttribute<Integer> {
   public static final String NAME = IntegerAttribute.class.getSimpleName();
   private static final Integer DEFAULT_INTEGER = Integer.MIN_VALUE;

   public IntegerAttribute(Long id) {
      super(id);
   }

   @Override
   public Integer convertStringToValue(String value) {
      Integer toReturn = null;
      if (isValidInteger(value)) {
         toReturn = Integer.valueOf(value);
      } else {
         toReturn = getDefaultValue();
      }
      return toReturn;
   }

   public Integer getDefaultValue() {
      Integer toReturn = DEFAULT_INTEGER;
      String defaultValue = getDefaultValueFromMetaData();
      if (isValidInteger(defaultValue)) {
         toReturn = Integer.valueOf(defaultValue);
      }
      return toReturn;
   }

   private boolean isValidInteger(String value) {
      boolean result = false;
      if (Strings.isValid(value)) {
         try {
            Integer.parseInt(value);
            result = true;
         } catch (NumberFormatException ex) {
            // Do Nothing;
         }
      }
      return result;
   }
}