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

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class IntegerPropertyDescriptor extends TextPropertyDescriptor {

   public IntegerPropertyDescriptor(PropertyId propertyId) {
      super(propertyId, propertyId.getDisplayName());
      setCategory(propertyId.getCategoryName());
   }

   public static String fromModel(int i) {
      return String.valueOf(i);
   }

   public static int toModel(Object string) {
      try {
         return Integer.parseInt((String) string);
      } catch (NumberFormatException ex) {
         OseeLog.log(UiPluginConstants.class, Level.SEVERE, ex);
      }
      return 0;
   }

}