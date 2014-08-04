/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.review.role;

import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.junit.Assert;

/**
 * Test unit for {@link UserRole}
 *
 * @author Donald G. Dunne
 */
public class UserRoleTest {

   @org.junit.Test
   public void testToXmlFromXml() {
      UserRole item = new UserRole(Role.Author, "1233", 23.3, true);

      UserRole fromItem = new UserRole(item.toXml());
      Assert.assertEquals("1233", fromItem.getUserId());
      Assert.assertEquals(item.getRole(), fromItem.getRole());
      Assert.assertEquals(item.getHoursSpent(), fromItem.getHoursSpent());
      Assert.assertEquals(item.isCompleted(), fromItem.isCompleted());
   }
}
