/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
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
