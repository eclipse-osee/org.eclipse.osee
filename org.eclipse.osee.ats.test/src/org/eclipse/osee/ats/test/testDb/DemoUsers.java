package org.eclipse.osee.ats.test.testDb;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

public enum DemoUsers {
   Joe_Smith, Kay_Jones, Jason_Michael, Alex_Kay;

   public static User getDemoUser(DemoUsers demoUser) throws OseeCoreException {
      return UserManager.getUserByName(demoUser.name().replaceAll("_", " "));
   }

}