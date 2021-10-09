/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class CompositeLayoutItem extends LayoutItem {

   private int numColumns;
   private final List<LayoutItem> childrenLayoutItems = new ArrayList<>(5);

   public CompositeLayoutItem() {
      this(2);
   }

   public CompositeLayoutItem(int numColumns) {
      super("Composite");
      this.numColumns = numColumns;
   }

   public CompositeLayoutItem(int numColumns, LayoutItem... layoutItems) {
      this(numColumns);
      for (LayoutItem item : layoutItems) {
         childrenLayoutItems.add(item);
      }
   }

   public int getNumColumns() {
      return numColumns;
   }

   public void setNumColumns(int numColumns) {
      this.numColumns = numColumns;
   }

   public List<LayoutItem> getaLayoutItems() {
      return childrenLayoutItems;
   }

   @Override
   public String toString() {
      return "Composite " + numColumns;
   }

   public boolean isGroupComposite() {
      return false;
   }

}
