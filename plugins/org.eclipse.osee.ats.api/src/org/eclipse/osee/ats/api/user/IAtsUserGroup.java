/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.user;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsUserGroup {

   public List<IAtsUser> getUsers();

   public void setUsers(List<? extends IAtsUser> users);

   public void addUser(IAtsUser user);

   public void removeUser(IAtsUser user);
}
