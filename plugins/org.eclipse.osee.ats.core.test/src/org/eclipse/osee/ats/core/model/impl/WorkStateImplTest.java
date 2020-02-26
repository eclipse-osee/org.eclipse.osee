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
package org.eclipse.osee.ats.core.model.impl;

import java.util.Arrays;
import org.eclipse.osee.ats.core.users.AbstractUserTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class WorkStateImplTest extends AbstractUserTest {

   @Test
   public void testWorkStateImplStringListOfQextendsAtsUser() {
      WorkStateImpl state = new WorkStateImpl("Endorse", Arrays.asList(joe));
      Assert.assertEquals("Endorse", state.getName());
      Assert.assertEquals(0.0, state.getHoursSpent(), 0);
      Assert.assertSame(0, state.getPercentComplete());
      Assert.assertEquals(1, state.getAssignees().size());
   }

   @Test
   public void getGetSetAssignees() {
      WorkStateImpl state = new WorkStateImpl("Endorse", Arrays.asList(joe));
      Assert.assertEquals(1, state.getAssignees().size());
      Assert.assertEquals(joe, state.getAssignees().iterator().next());
      state.addAssignee(steve);
      Assert.assertEquals(2, state.getAssignees().size());
      Assert.assertTrue(state.getAssignees().contains(joe));
      Assert.assertTrue(state.getAssignees().contains(steve));
      state.addAssignee(steve);
      Assert.assertEquals(2, state.getAssignees().size());
      Assert.assertTrue(state.getAssignees().contains(joe));
      Assert.assertTrue(state.getAssignees().contains(steve));
      state.removeAssignee(joe);
      Assert.assertEquals(1, state.getAssignees().size());
      Assert.assertEquals(steve, state.getAssignees().iterator().next());
      state.setAssignees(Arrays.asList(joe, steve));
      Assert.assertEquals(2, state.getAssignees().size());
      Assert.assertTrue(state.getAssignees().contains(joe));
      Assert.assertTrue(state.getAssignees().contains(steve));
   }

   @Test
   public void testSetName() {
      WorkStateImpl state = new WorkStateImpl("Endorse", Arrays.asList(joe));
      Assert.assertEquals("Endorse", state.getName());
      state.setName("Implement");
      Assert.assertEquals("Implement", state.getName());
   }

   @Test
   public void testGetSetHoursSpent() {
      WorkStateImpl state = new WorkStateImpl("Endorse", Arrays.asList(joe));
      Assert.assertEquals(0.0, state.getHoursSpent(), 0);
      state.setHoursSpent(3);
      Assert.assertEquals(3.0, state.getHoursSpent(), 0);
   }

   @Test
   public void testSetPercentComplete() {
      WorkStateImpl state = new WorkStateImpl("Endorse", Arrays.asList(joe));
      Assert.assertEquals(0, state.getPercentComplete());
      state.setPercentComplete(3);
      Assert.assertEquals(3, state.getPercentComplete());
   }

}
