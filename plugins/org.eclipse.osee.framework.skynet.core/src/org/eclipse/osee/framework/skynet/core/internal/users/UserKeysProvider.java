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

package org.eclipse.osee.framework.skynet.core.internal.users;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class UserKeysProvider extends LazyObject<Iterable<? extends String>> implements CacheKeysLoader<String> {
   private final UserService userService;

   public UserKeysProvider(UserService userService) {
      this.userService = userService;
   }

   @Override
   public Iterable<? extends String> getAllKeys() {
      return get();
   }

   @Override
   protected FutureTask<Iterable<? extends String>> createLoaderTask() {
      Callable<Iterable<? extends String>> callable = new Callable<Iterable<? extends String>>() {

         @Override
         public Iterable<? extends String> call() throws Exception {
            userService.setUserLoading(true);
            Set<String> userIds = new LinkedHashSet<>();
            List<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON);
            for (Artifact artifact : artifacts) {
               User user = (User) artifact;
               userIds.add(user.getUserId());
            }
            userService.setUserLoading(false);
            return userIds;
         }

      };
      return new FutureTask<>(callable);
   }

}
