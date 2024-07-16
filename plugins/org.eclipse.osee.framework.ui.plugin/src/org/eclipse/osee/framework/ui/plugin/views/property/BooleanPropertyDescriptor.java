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

package org.eclipse.osee.framework.ui.plugin.views.property;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;

public class BooleanPropertyDescriptor extends ComboBoxPropertyDescriptor {
   public static final Integer falseInteger = Integer.valueOf(0);
   public static final Integer trueInteger = Integer.valueOf(1);
   public static final String[] booleanValues = new String[] {"false", "true"};

   public BooleanPropertyDescriptor(PropertyId propertyId) {
      super(propertyId, propertyId.getDisplayName(), booleanValues);
      setCategory(propertyId.getCategoryName());
   }

   public static Integer fromModel(boolean b) {
      return b ? trueInteger : falseInteger;
   }

   public static boolean toModel(Object integer) {
      return ((Integer) integer).intValue() != 0;
   }

}