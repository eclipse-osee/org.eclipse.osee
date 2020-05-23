/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class UserTokens {

   List<UserToken> users = new LinkedList<UserToken>();
   UserToken account;

   public UserToken getAccount() {
      return account;
   }

   public void setAccount(UserToken account) {
      this.account = account;
   }

   public List<UserToken> getUsers() {
      return users;
   }

   public void setUsers(List<UserToken> users) {
      this.users = users;
   }

   public void addUser(UserToken user) {
      users.add(user);
   }
}
