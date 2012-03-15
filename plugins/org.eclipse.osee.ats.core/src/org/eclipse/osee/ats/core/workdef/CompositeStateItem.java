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
package org.eclipse.osee.ats.core.workdef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class CompositeStateItem extends StateItem {

   private int numColumns;
   private final List<StateItem> stateItems = new ArrayList<StateItem>(5);

   public CompositeStateItem() {
      this(2);
   }

   public CompositeStateItem(int numColumns) {
      super("Composite");
      this.numColumns = numColumns;
   }

   public int getNumColumns() {
      return numColumns;
   }

   public void setNumColumns(int numColumns) {
      this.numColumns = numColumns;
   }

   public List<StateItem> getStateItems() {
      return stateItems;
   }

   @Override
   public String toString() {
      return "Composite " + numColumns;
   }
}
