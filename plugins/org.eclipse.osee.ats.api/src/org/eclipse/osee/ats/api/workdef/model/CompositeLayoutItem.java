/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;

/**
 * @author Donald G. Dunne
 */
public class CompositeLayoutItem extends LayoutItem implements IAtsCompositeLayoutItem {

   private int numColumns;
   private final List<IAtsLayoutItem> stateItems = new ArrayList<>(5);

   public CompositeLayoutItem() {
      this(2);
   }

   public CompositeLayoutItem(int numColumns) {
      super("Composite");
      this.numColumns = numColumns;
   }

   public CompositeLayoutItem(int numColumns, IAtsLayoutItem... layoutItems) {
      this(numColumns);
      for (IAtsLayoutItem item : layoutItems) {
         stateItems.add(item);
      }
   }

   @Override
   public int getNumColumns() {
      return numColumns;
   }

   @Override
   public void setNumColumns(int numColumns) {
      this.numColumns = numColumns;
   }

   @Override
   public List<IAtsLayoutItem> getaLayoutItems() {
      return stateItems;
   }

   @Override
   public String toString() {
      return "Composite " + numColumns;
   }
}
