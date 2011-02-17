/*
 * Created on Feb 2, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.provider;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class UserRefUtil {

   public static Set<String> getUserIds(EList<UserRef> userRefs) {
      Set<String> userIds = new HashSet<String>();
      for (UserRef UserRef : userRefs) {
         if (UserRef instanceof UserByUserId) {
            userIds.add(((UserByUserId) UserRef).getUserId());
         }
      }
      return userIds;
   }

   public static Set<String> getUserNames(EList<UserRef> userRefs) {
      Set<String> userNames = new HashSet<String>();
      for (UserRef UserRef : userRefs) {
         if (UserRef instanceof UserByName) {
            userNames.add(Strings.unquote(((UserByName) UserRef).getUserName()));
         }
      }
      return userNames;
   }

   public static Set<Artifact> getUsers(EList<UserRef> userRefs) throws OseeCoreException {
      Set<Artifact> users = new HashSet<Artifact>();
      if (userRefs != null) {
         for (String userId : getUserIds(userRefs)) {
            User user = UserManager.getUserByUserId(userId);
            users.add(user);
         }
         for (String userName : getUserNames(userRefs)) {
            User user = UserManager.getUserByName(Strings.unquote(userName));
            users.add(user);
         }
      }
      return users;
   }
}
