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
package org.eclipse.osee.framework.skynet.core;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Roberto E. Escobar
 */
public interface UserAdmin {

   void reset();

   User getCurrentUser() throws OseeCoreException;

   void releaseCurrentUser();

   User getUserByUserId(String userId) throws OseeCoreException;

   List<User> getActiveUsers() throws OseeCoreException;

   List<User> getUsersAll() throws OseeCoreException;

   List<User> getActiveUsersSortedByName() throws OseeCoreException;

   List<User> getUsersAllSortedByName() throws OseeCoreException;

   String[] getUserNames() throws OseeCoreException;

   User getUserByName(String name) throws OseeCoreException;

   User getUser(UserToken user) throws OseeCoreException;

   String getSafeUserNameById(ArtifactId userArtifactId);

   String getUserNameById(ArtifactId userArtifactId) throws OseeCoreException;

   User getUserByArtId(ArtifactId userArtifactId) throws OseeCoreException;

   User createUser(UserToken userToken, String comment) throws OseeCoreException;

   User createUser(UserToken userToken, SkynetTransaction transaction) throws OseeCoreException;

   boolean isDuringCurrentUserCreation();

}