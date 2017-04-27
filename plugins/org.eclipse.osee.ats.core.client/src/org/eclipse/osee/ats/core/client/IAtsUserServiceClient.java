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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsUserServiceClient extends IAtsUserService {

   User getOseeUser(IAtsUser user) throws OseeCoreException;

   User getOseeUserById(String userId) throws OseeCoreException;

   IAtsUser getUserFromOseeUser(User user) throws OseeCoreException;

   User getCurrentOseeUser() throws OseeCoreException;

   Collection<? extends User> toOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException;

   Collection<IAtsUser> getAtsUsers(Collection<? extends Artifact> artifacts) throws OseeCoreException;

   Collection<User> getOseeUsers(Collection<? extends IAtsUser> users) throws OseeCoreException;

   IAtsUser getUserFromToken(UserToken userToken);

   List<User> getOseeUsersSorted(Active active);

   List<IAtsUser> getSubscribed(IAtsWorkItem workItem) throws OseeCoreException;

   IAtsUser getUserById(long accountId);

}