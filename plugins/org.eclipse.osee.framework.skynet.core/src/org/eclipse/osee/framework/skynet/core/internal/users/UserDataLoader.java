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
package org.eclipse.osee.framework.skynet.core.internal.users;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.cache.admin.CacheDataLoader;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.UserInDatabaseMultipleTimes;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class UserDataLoader implements CacheDataLoader<String, User> {

   public UserDataLoader() {
   }

   @Override
   public Map<String, User> load(Iterable<? extends String> keys)  {
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON);
      Map<String, User> result = new HashMap<>();
      for (Artifact artifact : artifacts) {
         User user = (User) artifact;
         result.put(user.getUserId(), user);
      }
      return result;
   }

   @Override
   public User load(String userId)  {
      User user = null;
      try {
         Artifact artifact = ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.User,
            CoreAttributeTypes.UserId, userId, CoreBranches.COMMON);
         user = (User) artifact;
      } catch (ArtifactDoesNotExist ex1) {
         throw new UserNotInDatabase(ex1, "Unable to load user with userId[%s]", userId);
      } catch (MultipleArtifactsExist ex2) {
         throw new UserInDatabaseMultipleTimes(ex2, "Unable to load user with userId[%s] - multiple users found.",
            userId);
      }
      return user;
   }

   @Override
   public User reload(String key, User oldValue)  {
      Collection<? extends Artifact> reloadArtifacts = ArtifactQuery.reloadArtifacts(Collections.singleton(oldValue));
      ArtifactToken artifact = reloadArtifacts.isEmpty() ? null : reloadArtifacts.iterator().next();
      return (User) artifact;
   }

}
