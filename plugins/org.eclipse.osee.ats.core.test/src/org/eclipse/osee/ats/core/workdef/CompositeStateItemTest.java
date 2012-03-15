/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link CompositeStateItem}
 *
 * @author Donald G. Dunne
 */
public class CompositeStateItemTest {

   @Test
   public void testToString() {
      CompositeStateItem compositeStateItem = new CompositeStateItem();
      Assert.assertEquals("Composite 2", compositeStateItem.toString());
   }

   @Test
   public void testConstructors() {
      new CompositeStateItem();
   }

   @Test
   public void testGetSetNumColumns() {
      CompositeStateItem compositeStateItem = new CompositeStateItem();
      Assert.assertEquals(2, compositeStateItem.getNumColumns());
      compositeStateItem.setNumColumns(3);
      Assert.assertEquals(3, compositeStateItem.getNumColumns());

      compositeStateItem = new CompositeStateItem(5);
      Assert.assertEquals(5, compositeStateItem.getNumColumns());
   }

   @Test
   public void testGetStateItems() {
      CompositeStateItem comp = new CompositeStateItem();
      Assert.assertEquals(0, comp.getStateItems().size());
   }

}
