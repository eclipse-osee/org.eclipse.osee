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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.UuidIdentity;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link SystemUser}<br/>
 * Test case for {@link Guest}<br/>
 * Test case for {@link UnAssigned}<br/>
 * Test case for {@link AbstractAtsUser}
 * 
 * @author Donald G. Dunne
 */
public class CoreAtsUsersTest {

   @Test
   public void testGetDescription() {
      Assert.assertEquals("System User", AtsCoreUsers.SYSTEM_USER.getDescription());
      Assert.assertEquals("Guest", AtsCoreUsers.GUEST_USER.getDescription());
      Assert.assertEquals("UnAssigned", AtsCoreUsers.UNASSIGNED_USER.getDescription());
   }

   @Test
   public void testIsActive() {
      AtsCoreUsers.SYSTEM_USER.isActive();
      AtsCoreUsers.GUEST_USER.isActive();
      AtsCoreUsers.UNASSIGNED_USER.isActive();
      new TestUser().isActive();
   }

   @Test
   public void testGetName() {
      Assert.assertEquals("OSEE System", AtsCoreUsers.SYSTEM_USER.getName());
      Assert.assertEquals("Guest", AtsCoreUsers.GUEST_USER.getName());
      Assert.assertEquals("UnAssigned", AtsCoreUsers.UNASSIGNED_USER.getName());
   }

   @Test
   public void testGetUuid() {
      Assert.assertEquals(11L, AtsCoreUsers.SYSTEM_USER.getUuid());
      Assert.assertEquals(1896L, AtsCoreUsers.GUEST_USER.getUuid());
   }

   @Test
   public void testGetUserId() {
      Assert.assertEquals("99999999", AtsCoreUsers.SYSTEM_USER.getUserId());
      Assert.assertEquals("99999998", AtsCoreUsers.GUEST_USER.getUserId());
      Assert.assertEquals("99999997", AtsCoreUsers.UNASSIGNED_USER.getUserId());
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
      Assert.assertEquals("", AtsCoreUsers.GUEST_USER.getEmail());
      Assert.assertEquals("", AtsCoreUsers.UNASSIGNED_USER.getEmail());
   }

   @Test
   public void testToString() {
      Assert.assertEquals("User [Guest - 99999998 - ]", AtsCoreUsers.GUEST_USER.toString());
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
      Assert.assertTrue(AtsCoreUsers.SYSTEM_USER.equals(user));
      user.setUserId("234");
      Assert.assertFalse(AtsCoreUsers.SYSTEM_USER.equals(user));
      Assert.assertFalse(AtsCoreUsers.SYSTEM_USER.equals("asfd"));
      Assert.assertFalse(AtsCoreUsers.SYSTEM_USER.equals(null));
      Assert.assertTrue(AtsCoreUsers.SYSTEM_USER.equals(AtsCoreUsers.SYSTEM_USER));
      user.setUserId(null);
      Assert.assertFalse(user.equals(AtsCoreUsers.SYSTEM_USER));

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
      user2.setUserId(AtsCoreUsers.SYSTEM_USER.getUserId());

      IAtsUser mapToPi = AtsCoreUsers.SYSTEM_USER;
      IAtsUser alsoMapToPi = user2;

      IAtsUser mapToE = user1;
      IAtsUser alsoMapToE = user1;

      HashMap<IAtsUser, Double> hash = new HashMap<>();
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
      Assert.assertEquals(-8, AtsCoreUsers.GUEST_USER.compareTo(AtsCoreUsers.SYSTEM_USER));
      Assert.assertEquals(1, AtsCoreUsers.GUEST_USER.compareTo(null));
      Assert.assertEquals(-1, AtsCoreUsers.GUEST_USER.compareTo("asdf"));

      TestUser user = new TestUser();
      user.setName(null);
      Assert.assertEquals(1, AtsCoreUsers.GUEST_USER.compareTo(user));
      Assert.assertEquals(-1, user.compareTo(AtsCoreUsers.GUEST_USER));
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
      public void setStoreObject(ArtifactId artifact) {
         // do nothing
      }

      @Override
      public long getUuid() {
         return 999994;
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
      public boolean matches(UuidIdentity... identities) {
         for (UuidIdentity identity : identities) {
            if (equals(identity)) {
               return true;
            }
         }
         return false;
      }

      @Override
      public String toStringWithId() {
         return String.format("[%s][%d]", getName(), getUuid());
      }

      @Override
      public ArtifactId getStoreObject() {
         return null;
      }

      @Override
      public void setStoreObject(ArtifactId artifact) {
         // do nothing
      }

      @Override
      public long getUuid() {
         return 0;
      }
   };
}
