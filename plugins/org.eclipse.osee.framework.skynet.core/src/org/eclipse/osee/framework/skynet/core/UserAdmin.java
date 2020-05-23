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

package org.eclipse.osee.framework.skynet.core;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Roberto E. Escobar
 */
public interface UserAdmin {

   void reset();

   User getCurrentUser();

   void releaseCurrentUser();

   User getUserByUserId(String userId);

   List<User> getActiveUsers();

   List<User> getUsersAll();

   List<User> getActiveUsersSortedByName();

   List<User> getUsersAllSortedByName();

   String[] getUserNames();

   User getUserByName(String name);

   User getUser(UserToken user);

   String getSafeUserNameById(ArtifactId userArtifactId);

   String getUserNameById(ArtifactId userArtifactId);

   User getUserByArtId(ArtifactId userArtifactId);

   User createUser(UserToken userToken, String comment);

   User createUser(UserToken userToken, SkynetTransaction transaction);

   boolean isDuringCurrentUserCreation();

}