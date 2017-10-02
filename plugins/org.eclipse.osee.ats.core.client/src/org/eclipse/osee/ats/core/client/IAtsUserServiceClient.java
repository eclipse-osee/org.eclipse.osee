/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsUserServiceClient extends IAtsUserService {

   User getOseeUser(IAtsUser user);

   User getOseeUserById(String userId);

   IAtsUser getUserFromOseeUser(User user);

   User getCurrentOseeUser();

   Collection<? extends User> toOseeUsers(Collection<? extends IAtsUser> users);

   Collection<IAtsUser> getAtsUsers(Collection<? extends Artifact> artifacts);

   Collection<User> getOseeUsers(Collection<? extends IAtsUser> users);

   IAtsUser getUserFromToken(UserToken userToken);

   List<User> getOseeUsersSorted(Active active);

   List<IAtsUser> getSubscribed(IAtsWorkItem workItem);

   IAtsUser getUserById(long accountId);

}