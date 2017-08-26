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
package org.eclipse.osee.account.admin;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;

/**
 * @author Roberto E. Escobar
 */
public class UserTokenAccount extends BaseIdentity<String> implements Account, AccountPreferences {
   public static final UserTokenAccount Anonymous = new UserTokenAccount(SystemUser.Anonymous);

   private final UserToken user;

   public UserTokenAccount(UserToken user) {
      super(user.getGuid());
      this.user = user;
   }

   @Override
   public Long getId() {
      return user.getId();
   }

   @Override
   public boolean isActive() {
      return user.isActive();
   }

   @Override
   public String getName() {
      return user.getName();
   }

   @Override
   public String getUserName() {
      return user.getUserId();
   }

   @Override
   public String getEmail() {
      return user.getEmail();
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

   @Override
   public AccountWebPreferences getWebPreferences() {
      return null;
   }
}