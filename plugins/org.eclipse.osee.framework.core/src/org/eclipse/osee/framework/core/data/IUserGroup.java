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
public interface IUserGroup {

   ArtifactToken getArtifact();

   boolean addMember(UserId user);

   boolean isMember(UserId user);

   boolean isCurrentUserMember();

   boolean removeMember(UserId user);

   Collection<UserToken> getMembers();

}