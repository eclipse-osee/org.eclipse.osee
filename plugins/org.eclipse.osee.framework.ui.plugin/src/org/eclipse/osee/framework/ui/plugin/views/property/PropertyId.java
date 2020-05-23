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

/**
 * A PropertyId combines the category name and display name for a IPropertyDescriptor in a single object. The same set
 * of diaplayNames may therefore appear more than once in a property list as is necessary to display both ends of an
 * reference
 */
public class PropertyId {
   private final String categoryName;
   private final String displayName;

   public PropertyId(String categoryName, String displayName) {
      this.categoryName = categoryName;
      this.displayName = displayName;
   }

   public String getCategoryName() {
      return categoryName;
   }

   public String getDisplayName() {
      return displayName;
   }

   @Override
   public String toString() {
      return categoryName + " : " + displayName;
   }

}