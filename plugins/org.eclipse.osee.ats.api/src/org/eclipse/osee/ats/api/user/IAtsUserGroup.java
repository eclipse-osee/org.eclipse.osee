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

package org.eclipse.osee.ats.api.user;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsUserGroup {

   public List<AtsUser> getUsers();

   public void setUsers(List<? extends AtsUser> users);

   public void addUser(AtsUser user);

   public void removeUser(AtsUser user);
}
