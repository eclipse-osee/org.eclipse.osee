/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
