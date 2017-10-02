/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class IntegerAttribute extends CharacterBackedAttribute<Integer> {

   private static final Integer DEFAULT_INTEGER = Integer.MIN_VALUE;

   @Override
   public Integer getValue() {
      return (Integer) getAttributeDataProvider().getValue();
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
      String defaultValue = getAttributeType().getDefaultValue();
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