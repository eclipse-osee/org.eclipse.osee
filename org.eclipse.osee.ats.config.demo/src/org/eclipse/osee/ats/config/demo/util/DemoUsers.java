/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.util;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G. Dunne
 */
public enum DemoUsers {
   Joe_Smith, Kay_Jones, Jason_Michael, Alex_Kay;

   public static User getDemoUser(DemoUsers demoUser) throws OseeCoreException {
      return UserManager.getUserByName(demoUser.name().replaceAll("_", " "));
   }

}
