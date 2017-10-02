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
public class FloatingPointAttribute extends CharacterBackedAttribute<Double> {

   private static final Double DEFAULT_DOUBLE = Double.MIN_VALUE;

   @Override
   public Double getValue() {
      return (Double) getAttributeDataProvider().getValue();
   }

   @Override
   public Double convertStringToValue(String value) {
      Double toReturn = null;
      if (isValidDouble(value)) {
         toReturn = Double.valueOf(value);
      } else {
         toReturn = getDefaultValue();
      }
      return toReturn;
   }

   public Double getDefaultValue() {
      Double toReturn = DEFAULT_DOUBLE;
      String defaultValue = getAttributeType().getDefaultValue();
      if (isValidDouble(defaultValue)) {
         toReturn = Double.valueOf(defaultValue);
      }
      return toReturn;
   }

   private boolean isValidDouble(String value) {
      boolean result = false;
      if (Strings.isValid(value)) {
         try {
            Double.parseDouble(value);
            result = true;
         } catch (NumberFormatException ex) {
            // Do Nothing;
         }
      }
      return result;
   }
}