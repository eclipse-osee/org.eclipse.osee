/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.user;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsUsers {

   private List<AtsUser> users = new LinkedList<AtsUser>();

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
