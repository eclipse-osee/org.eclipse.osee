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
package org.eclipse.osee.framework.skynet.core.user;

import org.eclipse.osee.framework.jdk.core.util.OseeUser;
import org.eclipse.osee.framework.ui.plugin.security.GuestAuthentication;

/**
 * @author Ryan D. Brooks
 */
public enum UserEnum implements OseeUser {
   NoOne("No One", "", "99999999", false),
   Guest(GuestAuthentication.DEFAULT_USER_NAME, "", GuestAuthentication.DEFAULT_USER_ID, false),
   UnAssigned("UnAssigned", "", "99999997", true);
   private final String name;
   private final String email;
   private final String userID;
   private final boolean active;

   UserEnum(String name, String email, String userId, boolean active) {
      this.name = name;
      this.email = email;
      this.userID = userId;
      this.active = active;
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
}
