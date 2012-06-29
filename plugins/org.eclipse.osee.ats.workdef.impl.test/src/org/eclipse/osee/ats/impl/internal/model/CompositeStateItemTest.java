/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.impl.internal.model;

import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.impl.internal.model.CompositeLayoutItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link CompositeLayoutItem}
 *
 * @author Donald G. Dunne
 */
public class CompositeStateItemTest {

   @Test
   public void testToString() {
      IAtsCompositeLayoutItem compositeStateItem = new CompositeLayoutItem();
      Assert.assertEquals("Composite 2", compositeStateItem.toString());
   }

   @Test
   public void testConstructors() {
      new CompositeLayoutItem();
   }

   @Test
   public void testGetSetNumColumns() {
      IAtsCompositeLayoutItem compositeStateItem = new CompositeLayoutItem();
      Assert.assertEquals(2, compositeStateItem.getNumColumns());
      compositeStateItem.setNumColumns(3);
      Assert.assertEquals(3, compositeStateItem.getNumColumns());

      compositeStateItem = new CompositeLayoutItem(5);
      Assert.assertEquals(5, compositeStateItem.getNumColumns());
   }

   @Test
   public void testGetStateItems() {
      IAtsCompositeLayoutItem comp = new CompositeLayoutItem();
      Assert.assertEquals(0, comp.getaLayoutItems().size());
   }

}
