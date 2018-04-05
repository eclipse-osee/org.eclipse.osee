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
package org.eclipse.osee.ats.core.util;

import java.util.Arrays;
import org.eclipse.osee.ats.core.users.AbstractUserTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsUserGroupTest extends AbstractUserTest {

   @Test
   public void testGetSetAddRemoveUsers() {
      AtsUserGroup group = new AtsUserGroup();
      Assert.assertTrue(group.getUsers().isEmpty());

      group.setUsers(Arrays.asList(joe, steve));
      Assert.assertEquals(2, group.getUsers().size());
      Assert.assertTrue(group.getUsers().contains(joe));
      Assert.assertTrue(group.getUsers().contains(steve));
      Assert.assertFalse(group.getUsers().contains(alice));

      group.removeUser(steve);
      Assert.assertEquals(1, group.getUsers().size());
      Assert.assertTrue(group.getUsers().contains(joe));
      Assert.assertFalse(group.getUsers().contains(steve));
      Assert.assertFalse(group.getUsers().contains(alice));
   }

   @Test
   public void testToString() {
      AtsUserGroup group = new AtsUserGroup();
      group.setUsers(Arrays.asList(joe, steve));
      String toString = group.toString();
      Assert.assertTrue(toString.contains("joe"));
      Assert.assertTrue(toString.contains("steve"));
   }

}
