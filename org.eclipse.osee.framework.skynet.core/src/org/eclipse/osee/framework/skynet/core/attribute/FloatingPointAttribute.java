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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class FloatingPointAttribute extends CharacterBackedAttribute<Double> {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public Double getValue() throws OseeCoreException {
      String doubleString = getAttributeDataProvider().getValueAsString();
      return Strings.isValid(doubleString) ? Double.valueOf(doubleString) : null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#subClassSetValue(java.lang.Object)
    */
   @Override
   public boolean subClassSetValue(Double value) throws OseeCoreException {
      return getAttributeDataProvider().setValue(String.valueOf(value));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#convertStringToValue(java.lang.String)
    */
   @Override
   protected Double convertStringToValue(String value) {
      if (value == null || value.equals("")) {
         return null;
      }
      return new Double(value);
   }
}