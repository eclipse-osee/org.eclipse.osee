/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
