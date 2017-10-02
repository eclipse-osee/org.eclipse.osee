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

package org.eclipse.osee.framework.skynet.core.internal.users;

import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Ryan D. Brooks
 */
public class BootStrapUser extends User {

   public static BootStrapUser instance;

   private BootStrapUser() {
      super(CoreBranches.COMMON);
   }

   public static BootStrapUser getInstance() {
      if (instance == null) {
         instance = new BootStrapUser();
      }
      return instance;
   }

   @SuppressWarnings("unused")
   private static final long serialVersionUID = 1L;

   @Override
   public String getEmail() {
      return SystemUser.BootStrap.getEmail();
   }

   @Override
   public String getPhone() {
      return "phone home";
   }

   @Override
   public String getUserId() {
      return SystemUser.BootStrap.getUserId();
   }

   @Override
   public boolean isActive() {
      return SystemUser.BootStrap.isActive();
   }

   @Override
   public void setActive(boolean required) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setEmail(String email) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setPhone(String phone) {
      throw new UnsupportedOperationException();
   }

}