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
public class OseeUser implements IOseeUser {
   private final String userName;
   private final String userId;
   private final String userEmail;
   private final boolean isActive;

   public OseeUser(String userName, String userId, String userEmail, boolean isActive) {
      this.userName = userName;
      this.userId = userId;
      this.userEmail = userEmail;
      this.isActive = isActive;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#getEmail()
    */
   @Override
   public String getEmail() {
      return userEmail;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#getName()
    */
   @Override
   public String getName() {
      return userName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#getUserID()
    */
   @Override
   public String getUserID() {
      return userId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.data.IOseeUser#isActive()
    */
   @Override
   public boolean isActive() {
      return isActive;
   }
}