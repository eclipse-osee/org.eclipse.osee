/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class LongAttribute extends CharacterBackedAttribute<Long> {

   private static final Long DEFAULT_LONG = Long.MIN_VALUE;

   @Override
   public Long getValue() throws OseeCoreException {
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