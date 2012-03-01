/*
 * Created on Mar 1, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import junit.framework.Assert;
import org.eclipse.osee.ats.core.model.IAtsChildren;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.DemoUsers;

/**
 * @author Donald G. Dunne
 */
public class AtsUserTest {

   @org.junit.Test
   public void testGetUserId() throws OseeCoreException {
      User user = UserManager.getUser();
      AtsUser atsUser = new AtsUser(user);
      Assert.assertEquals(user.getUserId(), atsUser.getUserId());
   }

   @org.junit.Test
   public void testGetName() throws OseeCoreException {
      User user = UserManager.getUser();
      AtsUser atsUser = new AtsUser(user);
      Assert.assertEquals(user.getName(), atsUser.getName());
   }

   @org.junit.Test
   public void testGetEmail() throws OseeCoreException {
      User user = UserManager.getUser();
      AtsUser atsUser = new AtsUser(user);
      Assert.assertEquals(user.getEmail(), atsUser.getEmail());
   }

   @org.junit.Test
   public void testEquals() throws OseeCoreException {
      User user = UserManager.getUser();
      AtsUser atsUser = new AtsUser(user);
      Assert.assertEquals(atsUser, user);

      IAtsUser atsUser2 = getAtsUser(user.getName(), user.getUserId());
      Assert.assertEquals(atsUser, atsUser2);
   }

   @org.junit.Test
   public void testRemove() throws OseeCoreException {
      Collection<IAtsUser> assignees = new HashSet<IAtsUser>();
      assignees.add(AtsUsers.getUserFromToken(DemoUsers.Alex_Kay));
      assignees.add(AtsUsers.getUserFromToken(DemoUsers.Joe_Smith));
      Assert.assertTrue(Collections.isEqual(assignees,
         Arrays.asList(AtsUsers.getUserFromToken(DemoUsers.Alex_Kay), AtsUsers.getUserFromToken(DemoUsers.Joe_Smith))));

      assignees.remove(AtsUsers.getUser());
      Assert.assertTrue(Collections.isEqual(assignees, Arrays.asList(AtsUsers.getUserFromToken(DemoUsers.Alex_Kay))));
   }

   public IAtsUser getAtsUser(final String name, final String userId) {
      return new IAtsUser() {

         @Override
         public int compareTo(Object o) {
            return 0;
         }

         @Override
         public String getName() {
            return name;
         }

         @Override
         public Integer getIdInt() {
            return null;
         }

         @Override
         public String getHumanReadableId() {
            return null;
         }

         @Override
         public String getGuid() {
            return null;
         }

         @Override
         public String getDescription() {
            return null;
         }

         @Override
         public IAtsChildren getAtsChildren() {
            return null;
         }

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public String getUserId() {
            return userId;
         }

         @Override
         public String getEmail() {
            return null;
         }
      };
   }

}
