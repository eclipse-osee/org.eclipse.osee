/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;

/**
 * @author Roberto E. Escobar
 */
public class BootstrapAccount extends BaseIdentity<String> implements Account, AccountPreferences {

   public BootstrapAccount() {
      super(SystemUser.BootStrap.getGuid());
   }

   @Override
   public long getId() {
      return -1L;
   }

   @Override
   public boolean isActive() {
      return SystemUser.BootStrap.isActive();
   }

   @Override
   public String getName() {
      return SystemUser.BootStrap.getName();
   }

   @Override
   public String getUserName() {
      return SystemUser.BootStrap.getUserId();
   }

   @Override
   public String getEmail() {
      return SystemUser.BootStrap.getEmail();
   }

   @Override
   public AccountPreferences getPreferences() {
      return this;
   }

   @Override
   public Map<String, String> asMap() {
      return Collections.emptyMap();
   }

   @Override
   public Set<String> getKeys() {
      return Collections.emptySet();
   }

   @Override
   public String get(String key) {
      return "";
   }

   @Override
   public boolean getBoolean(String key) {
      return false;
   }

}
