/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.demo.util;

import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

/**
 * @author Donald G. Dunne
 */
public enum DemoUsers implements IOseeUserInfo {
   Joe_Smith("Joe Smith", "", "Joe Smith", true),
   Kay_Jones("Guest", "", "99999998", true),
   Jason_Michael("Boot Strap", "bootstrap@osee.org", "bootstrap", true),
   Alex_Kay("UnAssigned", "", "99999997", true);
   private final String name;
   private final String email;
   private final String userID;
   private final boolean active;

   DemoUsers(String name, String email, String userId, boolean active) {
      this.name = name;
      this.email = email;
      this.userID = userId;
      this.active = active;
   }

   public static User getDemoUser(DemoUsers demoUser) throws OseeCoreException {
      return UserManager.getUserByName(demoUser.name());
   }

   /**
    * @return Returns the email.
    */
   public String getEmail() {
      return email;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @return Returns the userID.
    */
   public String getUserID() {
      return userID;
   }

   /**
    * @return Returns the active.
    */
   public boolean isActive() {
      return active;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUserInfo#isCreationRequired()
    */
   @Override
   public boolean isCreationRequired() {
      return false;
   }
}
