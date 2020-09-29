/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal.users;

import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Ryan D. Brooks
 */
public class BootStrapUser extends User {

   private static BootStrapUser instance;

   private BootStrapUser() {
      super(SystemUser.BootStrap.getId(), "guid", CoreBranches.COMMON);
   }

   public static BootStrapUser getInstance() {
      if (instance == null) {
         instance = new BootStrapUser();
      }
      return instance;
   }

   @Override
   public Long getId() {
      return SystemUser.BootStrap.getId();
   }

   @Override
   public String getEmail() {
      return SystemUser.BootStrap.getEmail();
   }

   @Override
   public String getPhone() {
      return "phone home";
   }

   @Override
   public String getName() {
      return SystemUser.BootStrap.getName();
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