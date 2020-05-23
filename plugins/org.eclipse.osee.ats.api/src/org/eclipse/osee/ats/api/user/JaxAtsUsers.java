/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.api.user;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsUsers {

   private List<AtsUser> users = new LinkedList<>();

   public JaxAtsUsers() {
      // for jax-rs
   }

   public List<AtsUser> getUsers() {
      return users;
   }

   public void setUsers(List<AtsUser> users) {
      this.users = users;
   }

}
