/*
 * Created on Dec 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.ArrayList;
import java.util.List;

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

}
