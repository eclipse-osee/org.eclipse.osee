/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.account.admin;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.BaseId;

/**
 * @author Roberto E. Escobar
 */
public class UserTokenAccount extends BaseId implements Account, AccountPreferences {

   private final UserToken user;

   public UserTokenAccount(UserToken user) {
      super(user);
      this.user = user;
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