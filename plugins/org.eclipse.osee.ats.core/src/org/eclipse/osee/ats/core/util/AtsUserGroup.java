/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.util;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.IAtsUserGroup;

/**
 * @author Donald G. Dunne
 */
public class AtsUserGroup implements IAtsUserGroup {

   List<IAtsUser> users = new LinkedList<IAtsUser>();

   public AtsUserGroup() {
   }

   @Override
   public List<IAtsUser> getUsers() {
      return users;
   }

   @Override
   public void setUsers(List<? extends IAtsUser> users) {
      this.users.clear();
      for (IAtsUser user : users) {
         this.users.add(user);
      }
   }

   @Override
   public void addUser(IAtsUser user) {
      users.add(user);
   }

   @Override
   public void removeUser(IAtsUser user) {
      users.remove(user);
   }

   @Override
   public String toString() {
      return String.format("%s", users);
   }
}
