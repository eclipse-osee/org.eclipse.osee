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

package org.eclipse.osee.framework.skynet.core;

import java.sql.SQLException;

/**
 * @author Ryan D. Brooks
 */
public class BootStrapUser extends User {

   public static BootStrapUser instance;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param tagId
    * @throws SQLException
    */
   private BootStrapUser() throws SQLException {
      super(null, null, null, null, null);
   }

   /**
    * @return the instance
    */
   public static BootStrapUser getInstance() throws SQLException {
      if (instance == null) instance = new BootStrapUser();
      return instance;
   }

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#getEmail()
    */
   @Override
   public String getEmail() {
      return "bootstrap@osee.org";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#getName()
    */
   @Override
   public String getName() {
      return "Boot Strap";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#getPhone()
    */
   @Override
   public String getPhone() {
      return "phone home";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#getUserId()
    */
   @Override
   public String getUserId() {
      return "bootstrap";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#isActive()
    */
   @Override
   public Boolean isActive() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#isVersionControlled()
    */
   @Override
   public boolean isVersionControlled() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#setActive(boolean)
    */
   @Override
   public void setActive(boolean required) {
      throw new UnsupportedOperationException();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#setEmail(java.lang.String)
    */
   @Override
   public void setEmail(String email) {
      throw new UnsupportedOperationException();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.User#setPhone(java.lang.String)
    */
   @Override
   public void setPhone(String phone) {
      throw new UnsupportedOperationException();
   }

}