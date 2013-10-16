/*
 * Created on Apr 9, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface IAtsUserAdmin {

   IAtsUser getCurrentUser() throws OseeCoreException;

   Collection<IAtsUser> getUsers() throws OseeCoreException;

   IAtsUser getUserById(String userId) throws OseeCoreException;

   IAtsUser getUserByName(String name) throws OseeCoreException;

   Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds) throws OseeCoreException;

   ////////////////////////////

   IAtsUser getUserFromToken(IUserToken token) throws OseeCoreException;

   User getOseeUser(IAtsUser user) throws OseeCoreException;

   User getOseeUserById(String userId) throws OseeCoreException;

   IAtsUser getUserFromOseeUser(User user) throws OseeCoreException;

   User getCurrentOseeUser() throws OseeCoreException;

   Collection<? extends User> toOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException;

   Collection<IAtsUser> getUsers(List<? extends Artifact> artifacts) throws OseeCoreException;

   Collection<User> getOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException;

   Collection<IAtsUser> getAtsUsers(Collection<User> users) throws OseeCoreException;

}