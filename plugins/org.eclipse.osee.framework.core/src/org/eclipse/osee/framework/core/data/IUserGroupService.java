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

import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public interface IUserGroupService {

   IUserGroup getUserGroup(IUserGroupArtifactToken userGroup);

   Collection<IUserGroupArtifactToken> getMyUserGroups();

   boolean isInUserGroup(IUserGroupArtifactToken... userGroups);

   /**
    * Checks for existence of user group, then if member
    */
   public boolean isUserMember(IUserGroupArtifactToken amsUsers, Long id);

}
