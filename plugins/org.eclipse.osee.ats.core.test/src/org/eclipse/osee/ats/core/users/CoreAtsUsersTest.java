/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.core.users;

import java.util.HashMap;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link SystemUser}<br/>
 * Test case for {@link Anonymous}<br/>
 * Test case for {@link UnAssigned}<br/>
 * Test case for {@link AtsUser}
 *
 * @author Donald G. Dunne
 */
public class CoreAtsUsersTest {

   @Test
   public void testIsActive() {
      AtsCoreUsers.SYSTEM_USER.isActive();
      AtsCoreUsers.UNASSIGNED_USER.isActive();
      new TestUser().isActive();
   }

   @Test
   public void testGetName() {
      Assert.assertEquals(SystemUser.OseeSystem.getName(), AtsCoreUsers.SYSTEM_USER.getName());
      Assert.assertEquals(SystemUser.UnAssigned.getName(), AtsCoreUsers.UNASSIGNED_USER.getName());
   }

   @Test
   public void testGetId() {
      Assert.assertEquals(SystemUser.OseeSystem.getId(), AtsCoreUsers.SYSTEM_USER.getId());
   }

   @Test
   public void testGetUserId() {
      Assert.assertEquals(SystemUser.OseeSystem.getUserId(), AtsCoreUsers.SYSTEM_USER.getUserId());
      Assert.assertEquals(SystemUser.UnAssigned.getUserId(), AtsCoreUsers.UNASSIGNED_USER.getUserId());
   }

   @Test
   public void testSetUserId() {
      TestUser user = new TestUser();
      user.setUserId("asdf");
      Assert.assertEquals("asdf", user.getUserId());
   }

   @Test
   public void testGetEmail() {
      Assert.assertEquals("", AtsCoreUsers.SYSTEM_USER.getEmail());
      Assert.assertEquals("", AtsCoreUsers.UNASSIGNED_USER.getEmail());
   }

   @Test
   public void testEqualsObject() {
      TestUser user = new TestUser();
      user.setUserId("99999999");
      user.setId(11L);
      Assert.assertTrue(AtsCoreUsers.SYSTEM_USER.equals(user));
      user.setUserId("234");
      user.setId(95645L);
      Assert.assertFalse(AtsCoreUsers.SYSTEM_USER.equals(user));
      Assert.assertFalse(AtsCoreUsers.SYSTEM_USER.equals("asfd"));
      Assert.assertFalse(AtsCoreUsers.SYSTEM_USER.equals(null));
      Assert.assertTrue(AtsCoreUsers.SYSTEM_USER.equals(AtsCoreUsers.SYSTEM_USER));
      user.setUserId(null);
      Assert.assertFalse(user.equals(AtsCoreUsers.SYSTEM_USER));
   }

   @org.junit.Test
   public void testHashCorrectness() {
      TestUser user = new TestUser();
      user.setUserId("234");

      TestUser user1MapToE = new TestUser();
      user1MapToE.setUserId("234");

      TestUser user2MapToPi = new TestUser();
      user2MapToPi.setUserId(SystemUser.OseeSystem.getUserId());

      AtsUser user3MapToPi = AtsCoreUsers.SYSTEM_USER;

      HashMap<AtsUser, Double> hash = new HashMap<>();
      hash.put(user1MapToE, Math.E);
      hash.put(user2MapToPi, Math.E);
      hash.put(user3MapToPi, Math.PI);

      Assert.assertFalse(hash.get(user1MapToE).equals(Math.PI));
      Assert.assertFalse(hash.get(user2MapToPi).equals(Math.PI));
      Assert.assertTrue(hash.get(user3MapToPi).equals(Math.PI));

      Assert.assertTrue(hash.get(user1MapToE).equals(Math.E));
      Assert.assertTrue(hash.get(user2MapToPi).equals(Math.E));
      Assert.assertFalse(hash.get(user3MapToPi).equals(Math.E));

      Assert.assertFalse(user3MapToPi.equals(user1MapToE));
      Assert.assertTrue(user3MapToPi.equals(user3MapToPi));
   }

   @Test
   public void testEqualsObjectWithException() {
      TestUser user2 = new TestUser();
      user2.setUserId(null);
      ExceptionUser exceptionUser = new ExceptionUser();
      Assert.assertFalse(user2.equals(exceptionUser));
   }

   private class TestUser extends AtsUser {
      public TestUser() {
         super(999994L, "Test User", "999994", "", true);
      }
   };

   private class ExceptionUser extends AtsUser {
      public ExceptionUser() {
         super(0L, "Exception User", null, "", true);
      }

      @Override
      public String getUserId() {
         throw new OseeStateException("this is the exception under test");
      }
   };
}
