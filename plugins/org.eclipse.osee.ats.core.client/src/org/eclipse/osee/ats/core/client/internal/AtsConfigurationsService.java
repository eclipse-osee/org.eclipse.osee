/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IUserArtLoader;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Provides client configurations through server endpoint
 * 
 * @author Donald G. Dunne
 */
public class AtsConfigurationsService implements IAtsConfigurationsService {

   @Override
   public AtsConfigurations getConfigurations() {
      return configurationsCache.get();
   }

   @Override
   public AtsConfigurations getConfigurationsWithPend() {
      return getConfigurationsSupplier().get();
   }

   private final Supplier<AtsConfigurations> configurationsCache =
      Suppliers.memoizeWithExpiration(getConfigurationsSupplier(), 5, TimeUnit.MINUTES);

   private Supplier<AtsConfigurations> getConfigurationsSupplier() {
      return new Supplier<AtsConfigurations>() {
         @Override
         public AtsConfigurations get() {
            return loadConfigurations();
         }
      };
   }

   private AtsConfigurations loadConfigurations() {
      AtsConfigurations configs = AtsClientService.getConfigEndpoint().get();
      for (IAtsUser user : configs.getUsers()) {
         AtsUser jUser = (AtsUser) user;
         jUser.setUserArtLoader(userLoader);
      }
      return configs;
   }

   /**
    * Lazy Loader for user artifact
    */
   private final UserArtLoader userLoader = new UserArtLoader();
   private class UserArtLoader implements IUserArtLoader {

      @Override
      public ArtifactToken loadUser(IAtsUser user) {
         ArtifactToken userArt = null;
         try {
            userArt = UserManager.getUserByArtId(user.getId());
            if (userArt == null) {
               userArt = ArtifactQuery.getArtifactFromId(user.getId(), AtsClientService.get().getAtsBranch());
            }
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
         user.setStoreObject(userArt);
         return userArt;
      }
   }
}