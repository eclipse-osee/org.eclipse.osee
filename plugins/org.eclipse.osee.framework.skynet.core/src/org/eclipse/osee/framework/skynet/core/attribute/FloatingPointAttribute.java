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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public class FloatingPointAttribute extends CharacterBackedAttribute<Double> {

   @Override
   public Double getValue() {
      return (Double) getAttributeDataProvider().getValue();
   }

   @Override
   public Double convertStringToValue(String value) {
      if (Strings.isNumeric(value)) {
         return Double.valueOf(value);
      }
      return 0.0;
   }
}