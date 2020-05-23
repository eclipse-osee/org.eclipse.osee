/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class LongAttribute extends CharacterBackedAttribute<Long> {

   private static final Long DEFAULT_LONG = Long.MIN_VALUE;

   @Override
   public Long getValue() {
      return (Long) getAttributeDataProvider().getValue();
   }

   @Override
   public Long convertStringToValue(String value) {
      Long toReturn = null;
      if (isValidLong(value)) {
         toReturn = Long.valueOf(value);
      } else {
         toReturn = getDefaultValue();
      }
      return toReturn;
   }

   public Long getDefaultValue() {
      Long toReturn = DEFAULT_LONG;
      String defaultValue = getAttributeType().getDefaultValue();
      if (isValidLong(defaultValue)) {
         toReturn = Long.valueOf(defaultValue);
      }
      return toReturn;
   }

   private boolean isValidLong(String value) {
      boolean result = false;
      if (Strings.isValid(value)) {
         try {
            Long.getLong(value);
            result = true;
         } catch (NumberFormatException ex) {
            // Do Nothing;
         }
      }
      return result;
   }
}