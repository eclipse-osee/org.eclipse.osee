/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.users;

import java.util.HashMap;
import junit.framework.Assert;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.junit.Test;

/**
 * Test case for {@link SystemUser}<br/>
 * Test case for {@link Guest}<br/>
 * Test case for {@link UnAssigned}<br/>
 * Test case for {@link AbstractAtsUser}
 * 
 * @author Donald G. Dunne
 */
public class SystemUsersTest {

   @Test
   public void testGetDescription() {
      Assert.assertEquals("System User", SystemUser.instance.getDescription());
      Assert.assertEquals("Guest", Guest.instance.getDescription());
      Assert.assertEquals("UnAssigned", UnAssigned.instance.getDescription());
   }

   @Test
   public void testIsActive() {
      SystemUser.instance.isActive();
      Guest.instance.isActive();
      UnAssigned.instance.isActive();
      new TestUser().isActive();
   }

   @Test
   public void testGetName() {
      Assert.assertEquals("OSEE System", SystemUser.instance.getName());
      Assert.assertEquals("Guest", Guest.instance.getName());
      Assert.assertEquals("UnAssigned", UnAssigned.instance.getName());
   }

   @Test
   public void testGetGuid() {
      Assert.assertEquals("AAABDBYPet4AGJyrc9dY1w", SystemUser.instance.getGuid());
      Assert.assertEquals("AAABDi35uzwAxJLISLBZdA", Guest.instance.getGuid());
      Assert.assertEquals("AAABDi1tMx8Al92YWMjeRw", UnAssigned.instance.getGuid());
   }

   @Test
   public void testGetHumanReadableId() {
      Assert.assertEquals("FTNT9", SystemUser.instance.getHumanReadableId());
      Assert.assertEquals("TBRQV", Guest.instance.getHumanReadableId());
      Assert.assertEquals("7G020", UnAssigned.instance.getHumanReadableId());
   }

   @Test
   public void testGetUserId() {
      Assert.assertEquals("99999999", SystemUser.instance.getUserId());
      Assert.assertEquals("99999998", Guest.instance.getUserId());
      Assert.assertEquals("99999997", UnAssigned.instance.getUserId());
   }

   @Test
   public void testSetUserId() {
      TestUser user = new TestUser();
      user.setUserId("asdf");
      Assert.assertEquals("asdf", user.getUserId());
   }

   @Test
   public void testGetEmail() {
      Assert.assertEquals("", SystemUser.instance.getEmail());
      Assert.assertEquals("", Guest.instance.getEmail());
      Assert.assertEquals("", UnAssigned.instance.getEmail());
   }

   @Test
   public void testToString() {
      Assert.assertEquals("User [Guest - 99999998 - ]", Guest.instance.toString());
   }

   @Test
   public void testHashCode() {
      TestUser user = new TestUser();
      user.setUserId(null);
      Assert.assertEquals(user.hashCode(), 31);
      user.setUserId("99999998");
      Assert.assertEquals(user.hashCode(), 31 * 1 + user.getUserId().hashCode());
   }

   @Test
   public void testEqualsObject() {
      TestUser user = new TestUser();
      user.setUserId("99999999");
      Assert.assertTrue(SystemUser.instance.equals(user));
      user.setUserId("234");
      Assert.assertFalse(SystemUser.instance.equals(user));
      Assert.assertFalse(SystemUser.instance.equals("asfd"));
      Assert.assertFalse(SystemUser.instance.equals(null));
      Assert.assertTrue(SystemUser.instance.equals(SystemUser.instance));
      user.setUserId(null);
      Assert.assertFalse(user.equals(SystemUser.instance));

      TestUser user2 = new TestUser();
      user2.setUserId(null);
      Assert.assertTrue(user.equals(user2));
   }

   @org.junit.Test
   public void testHashCorrectness() {
      TestUser user = new TestUser();
      user.setUserId("234");

      TestUser user1 = new TestUser();
      user1.setUserId("234");

      TestUser user2 = new TestUser();
      user2.setUserId(SystemUser.instance.getUserId());

      IAtsUser mapToPi = SystemUser.instance;
      IAtsUser alsoMapToPi = user2;

      IAtsUser mapToE = user1;
      IAtsUser alsoMapToE = user1;

      HashMap<IAtsUser, Double> hash = new HashMap<IAtsUser, Double>();
      hash.put(mapToPi, Math.PI);
      hash.put(mapToE, Math.E);

      Assert.assertTrue(hash.get(mapToPi).equals(Math.PI));
      Assert.assertTrue(hash.get(mapToE).equals(Math.E));
      Assert.assertTrue(hash.get(alsoMapToPi).equals(Math.PI));
      Assert.assertTrue(hash.get(alsoMapToE).equals(Math.E));
      Assert.assertFalse(hash.get(mapToPi).equals(Math.E));
      Assert.assertFalse(hash.get(mapToE).equals(Math.PI));
      Assert.assertFalse(mapToPi.equals(mapToE));
      Assert.assertTrue(mapToPi.equals(mapToPi));
   }

   @Test
   public void testEqualsObjectWithException() {
      TestUser user2 = new TestUser();
      user2.setUserId(null);

      ExceptionUser exceptionUser = new ExceptionUser();
      Assert.assertFalse(user2.equals(exceptionUser));
   }

   @Test
   public void testCompareTo() {
      Assert.assertEquals(-8, Guest.instance.compareTo(SystemUser.instance));
      Assert.assertEquals(1, Guest.instance.compareTo(null));
      Assert.assertEquals(-1, Guest.instance.compareTo("asdf"));

      TestUser user = new TestUser();
      user.setName(null);
      Assert.assertEquals(1, Guest.instance.compareTo(user));
      Assert.assertEquals(-1, user.compareTo(Guest.instance));
      Assert.assertEquals(0, user.compareTo(user));
   }

   private class TestUser extends AbstractAtsUser {

      private String name = "Test User";

      public void setName(String name) {
         this.name = name;
      }

      public TestUser() {
         super("999994");
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public String getHumanReadableId() {
         return "ASDF";
      }

      @Override
      public String getGuid() {
         return "ASE434dfgsdfgs";
      }
   };

   private class ExceptionUser implements IAtsUser {

      @Override
      public String getUserId() throws OseeCoreException {
         throw new OseeStateException("this is the exception under test");
      }

      @Override
      public String getName() {
         return "Exception User";
      }

      @Override
      public String getHumanReadableId() {
         return "ASDF";
      }

      @Override
      public String getGuid() {
         return "ASE434dfgsdfgs";
      }

      @Override
      public String getDescription() {
         return getName();
      }

      @Override
      public int compareTo(Object o) {
         return 0;
      }

      @Override
      public String getEmail() {
         return getName();
      }

      @Override
      public boolean isActive() {
         return true;
      }

      @Override
      public boolean matches(Identity<?>... identities) {
         return false;
      }

      @Override
      public String toStringWithId() {
         return String.format("[%s][%s]", getName(), getHumanReadableId());
      }
   };
}
