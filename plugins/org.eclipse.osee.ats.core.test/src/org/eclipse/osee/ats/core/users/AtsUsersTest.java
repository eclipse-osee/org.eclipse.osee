/*
 * Created on Mar 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.mock.MockAtsUser;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsUsersTest {

   private final MockAtsUser joe = new MockAtsUser("joe");
   private final MockAtsUser steve = new MockAtsUser("steve");
   private final MockAtsUser alice = new MockAtsUser("alice");

   @Test
   public void testConstructor() {
      new AtsUsers();
   }

   @Test
   public void testIsSystemUser() {
      Assert.assertFalse(AtsUsers.isSystemUser(null));
      Assert.assertTrue(AtsUsers.isSystemUser(SystemUser.instance));
      Assert.assertFalse(AtsUsers.isSystemUser(UnAssigned.instance));
   }

   @Test
   public void testIsGuestUser() {
      Assert.assertFalse(AtsUsers.isGuestUser(null));
      Assert.assertTrue(AtsUsers.isGuestUser(Guest.instance));
      Assert.assertFalse(AtsUsers.isGuestUser(UnAssigned.instance));
   }

   @Test
   public void testIsUnAssignedUser() {
      Assert.assertFalse(AtsUsers.isUnAssignedUser(null));
      Assert.assertTrue(AtsUsers.isUnAssignedUser(UnAssigned.instance));
      Assert.assertFalse(AtsUsers.isUnAssignedUser(Guest.instance));
   }

   @Test
   public void testToList() {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      users.add(joe);
      users.add(steve);
      users.add(alice);
      List<IAtsUser> list = AtsUsers.toList(users);
      Assert.assertNotNull(list);
      Assert.assertEquals(list.size(), 3);
      Assert.assertTrue(list.contains(joe));
      Assert.assertTrue(list.contains(alice));
      Assert.assertTrue(list.contains(steve));
   }

   @Test
   public void testGetUser() {
      Assert.assertEquals(SystemUser.instance, AtsUsers.getUser(SystemUser.instance.getUserId()));
      Assert.assertNull(AtsUsers.getUser("2345"));
      Assert.assertNull(AtsUsers.getUser(null));

      Assert.assertEquals(SystemUser.instance, AtsUsers.getUser(SystemUser.instance.getUserId()));
      Assert.assertEquals(Guest.instance, AtsUsers.getUser(Guest.instance.getUserId()));
      Assert.assertEquals(UnAssigned.instance, AtsUsers.getUser(UnAssigned.instance.getUserId()));
   }

   @Test
   public void testGetGuestUser() {
      Assert.assertEquals(Guest.instance, AtsUsers.getGuestUser());
   }

   @Test
   public void testClearCache() throws OseeCoreException {
      AtsUsers.clearCache();
      Assert.assertTrue(AtsUsers.getUsers().isEmpty());

      AtsUsers.addUser(joe);
      Assert.assertFalse(AtsUsers.getUsers().isEmpty());

      AtsUsers.clearCache();
      Assert.assertTrue(AtsUsers.getUsers().isEmpty());
   }

   @Test
   public void testGetUsersByUserIds() throws OseeCoreException {
      AtsUsers.clearCache();
      AtsUsers.addUser(joe);
      AtsUsers.addUser(steve);
      List<String> userIds = new ArrayList<String>();
      userIds.add(joe.getUserId());
      userIds.add(steve.getUserId());
      userIds.add(alice.getUserId());
      Collection<IAtsUser> users = AtsUsers.getUsersByUserIds(userIds);
      Assert.assertNotNull(users);
      Assert.assertEquals(users.size(), 2);
      Assert.assertTrue(users.contains(joe));
      Assert.assertTrue(users.contains(steve));
      AtsUsers.addUser(alice);
      users = AtsUsers.getUsersByUserIds(userIds);
      Assert.assertNotNull(users);
      Assert.assertEquals(users.size(), 3);
      Assert.assertTrue(users.contains(joe));
      Assert.assertTrue(users.contains(steve));
      Assert.assertTrue(users.contains(alice));
   }

   @Test
   public void testGetUsers() throws OseeCoreException {
      AtsUsers.clearCache();
      AtsUsers.addUser(joe);
      AtsUsers.addUser(steve);
      Collection<IAtsUser> users = AtsUsers.getUsers();
      Assert.assertNotNull(users);
      Assert.assertEquals(users.size(), 2);
      Assert.assertTrue(users.contains(joe));
      Assert.assertTrue(users.contains(steve));
   }

   @Test
   public void testGetSystemUser() {
      Assert.assertEquals(SystemUser.instance, AtsUsers.getSystemUser());
   }

   @Test
   public void testGetUnAssigned() {
      Assert.assertEquals(UnAssigned.instance, AtsUsers.getUnAssigned());
   }

   @Test
   public void testGetValidEmailUsers() throws OseeCoreException {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      users.add(joe);
      users.add(steve);
      users.add(alice);
      Assert.assertTrue(AtsUsers.getValidEmailUsers(users).isEmpty());

      joe.setEmail("b@b.com");
      steve.setEmail("asdf");
      alice.setEmail(null);

      Assert.assertEquals(1, AtsUsers.getValidEmailUsers(users).size());
      Assert.assertEquals(joe, AtsUsers.getValidEmailUsers(users).iterator().next());
   }

   @Test
   public void testIsEmailValid() {
      Assert.assertTrue(AtsUsers.isEmailValid("b@b.com"));
      Assert.assertFalse(AtsUsers.isEmailValid("asdf"));
      Assert.assertFalse(AtsUsers.isEmailValid(null));
   }

   @Test
   public void testGetActiveEmailUsers() throws OseeCoreException {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      users.add(joe);
      users.add(steve);
      users.add(alice);
      joe.setEmail("b@b.com");
      joe.setActive(true);
      steve.setEmail("b@b.com");
      steve.setActive(false);
      alice.setEmail("b@b.com");
      alice.setActive(true);

      Collection<IAtsUser> activeEmailUsers = AtsUsers.getActiveEmailUsers(users);
      Assert.assertEquals(2, activeEmailUsers.size());
      Assert.assertTrue(activeEmailUsers.contains(joe));
      Assert.assertTrue(activeEmailUsers.contains(alice));
   }

   @Test
   public void testAddUser() throws OseeCoreException {
      AtsUsers.clearCache();
      Assert.assertTrue(AtsUsers.getUsers().isEmpty());
      AtsUsers.addUser(joe);
      Assert.assertFalse(AtsUsers.getUsers().isEmpty());
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddUser_null() throws OseeCoreException {
      AtsUsers.clearCache();
      Assert.assertTrue(AtsUsers.getUsers().isEmpty());
      AtsUsers.addUser(null);
      Assert.assertTrue(AtsUsers.getUsers().isEmpty());
   }
}
