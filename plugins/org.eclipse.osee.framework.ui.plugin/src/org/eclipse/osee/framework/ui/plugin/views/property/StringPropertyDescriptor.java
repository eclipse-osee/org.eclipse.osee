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

import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class StringPropertyDescriptor extends TextPropertyDescriptor {

   public StringPropertyDescriptor(PropertyId propertyId) {
      super(propertyId, propertyId.getDisplayName());
      setCategory(propertyId.getCategoryName());
   }

   public static String fromModel(String string) {
      return string != null ? string : "";
   }

   public static String toModel(Object string) {
      return (String) string;
   }

}