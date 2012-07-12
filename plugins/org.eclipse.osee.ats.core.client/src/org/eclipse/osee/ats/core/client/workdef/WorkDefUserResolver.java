/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.workdef;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.IUserResolver;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G. Dunne
 */
public class WorkDefUserResolver implements IUserResolver {

   public WorkDefUserResolver() {
   }

   @Override
   public boolean isUserIdValid(String userId) {
      try {
         return UserManager.getUserByUserId(userId) != null;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public boolean isUserNameValid(String name) {
      try {
         return UserManager.getUserByName(name) != null;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public String getUserIdByName(String name) {
      try {
         User user = UserManager.getUserByName(name);
         if (user != null) {
            return user.getUserId();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

}
