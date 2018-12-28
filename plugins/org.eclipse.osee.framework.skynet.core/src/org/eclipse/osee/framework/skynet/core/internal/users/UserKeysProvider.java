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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
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

   @Override
   public Iterable<? extends String> getAllKeys() {
      return get();
   }

   @Override
   protected FutureTask<Iterable<? extends String>> createLoaderTask() {
      Callable<Iterable<? extends String>> callable = new Callable<Iterable<? extends String>>() {

         @Override
         public Iterable<? extends String> call() throws Exception {
            Set<String> userIds = new LinkedHashSet<>();
            List<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON);
            for (Artifact artifact : artifacts) {
               User user = (User) artifact;
               userIds.add(user.getUserId());
            }
            return userIds;
         }

      };
      return new FutureTask<>(callable);
   }

}
