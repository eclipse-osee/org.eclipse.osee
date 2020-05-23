/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.util;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserGroup;

/**
 * @author Donald G. Dunne
 */
public class AtsUserGroup implements IAtsUserGroup {

   List<AtsUser> users = new LinkedList<>();

   public AtsUserGroup() {
   }

   @Override
   public List<AtsUser> getUsers() {
      return users;
   }

   @Override
   public void setUsers(List<? extends AtsUser> users) {
      this.users.clear();
      for (AtsUser user : users) {
         this.users.add(user);
      }
   }

   @Override
   public void addUser(AtsUser user) {
      users.add(user);
   }

   @Override
   public void removeUser(AtsUser user) {
      users.remove(user);
   }

   @Override
   public String toString() {
      return String.format("%s", users);
   }
}
