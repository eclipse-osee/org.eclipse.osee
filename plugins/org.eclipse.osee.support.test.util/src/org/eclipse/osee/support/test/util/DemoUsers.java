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
package org.eclipse.osee.support.test.util;

import org.eclipse.osee.framework.core.data.IOseeUserInfo;

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
   Joe_Smith("Joe Smith", "Joe Smith", "Joe Smith", true),
   Kay_Jones("Kay Jones", "Kay Jones", "Kay Jones", true),
   Jason_Michael("Jason Michael", "Jason Michael", "Jason Michael", true),
   Alex_Kay("Alex Kay", "Alex Kay", "Alex Kay", true),
   Inactive_Steve("Inactive Steve", "Inactive Steve", "Inactive Steve", false);
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

   /**
    * @return Returns the email.
    */
   @Override
   public String getEmail() {
      return email;
   }

   /**
    * @return Returns the name.
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * @return Returns the userID.
    */
   @Override
   public String getUserID() {
      return userID;
   }

   /**
    * @return Returns the active.
    */
   @Override
   public boolean isActive() {
      return active;
   }

   @Override
   public boolean isCreationRequired() {
      return false;
   }
}
