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
package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 */
public enum SystemUser implements IOseeUserInfo {
   OseeSystem("OSEE System", "", "99999999", false),
   Guest("Guest", "", "99999998", false),
   BootStrap("Boot Strap", "bootstrap@osee.org", "bootstrap", true),
   UnAssigned("UnAssigned", "", "99999997", true);
   private final String name;
   private final String email;
   private final String userID;
   private final boolean active;

   SystemUser(String name, String email, String userId, boolean active) {
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUserInfo#isCreationRequired()
    */
   @Override
   public boolean isCreationRequired() {
      return false;
   }
}
