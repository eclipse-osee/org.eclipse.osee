/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.util.internal;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsServerEndpointProvider;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.core.config.AbstractAtsConfigurationService;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Provides client configurations through server endpoint
 *
 * @author Donald G. Dunne
 */
public class AtsConfigurationsService extends AbstractAtsConfigurationService {
   private Supplier<AtsConfigurations> configurationsCache;
   // NOTE: No constructor cause loaded by OSGI before AtsApi

   @Override
   public AtsConfigurations getConfigurations() {
      if (configurationsCache == null || configurationsCache.get().getUsers().isEmpty()) {
         invalidate();
      }
      return configurationsCache.get();
   }

   @Override
   public AtsConfigurations getConfigurationsWithPend() {
      invalidate();
      AtsConfigurations configs = configurationsCache.get();
      return configs;
   }

   private void invalidate() {
      configurationsCache = Suppliers.memoizeWithExpiration(getConfigurationsSupplier(), 30, TimeUnit.MINUTES);
   }

   private Supplier<AtsConfigurations> getConfigurationsSupplier() {
      return new Supplier<AtsConfigurations>() {
         @Override
         public AtsConfigurations get() {
            return loadConfigurations();
         }
      };
   }

   private AtsConfigurations loadConfigurations() {
      AtsApiIde iAtsClient = AtsApiService.get();
      if (iAtsClient != null) {
         IAtsServerEndpointProvider serverEndpoints = iAtsClient.getServerEndpoints();
         AtsConfigEndpointApi configEndpoint = serverEndpoints.getConfigEndpoint();
         AtsConfigurations configs = configEndpoint.get();
         for (Version version : configs.getIdToVersion().values()) {
            version.setAtsApi(AtsApiService.get());
         }
         for (TeamDefinition teamDef : configs.getIdToTeamDef().values()) {
            teamDef.setAtsApi(AtsApiService.get());
         }
         for (ActionableItem ai : configs.getIdToAi().values()) {
            ai.setAtsApi(atsApi);
         }
         for (AtsUser user : configs.getUsers()) {
            user.setAtsApi(AtsApiService.get());
         }
         return configs;
      }
      return new AtsConfigurations();
   }

   @Override
   public XResultData configAtsDatabase(AtsApi atsApi) {
      if (isAtsBaseCreated()) {
         XResultData results = new XResultData();
         results.error("ATS base config has already been completed");
         return results;
      }
      return AtsApiService.get().getServerEndpoints().getConfigEndpoint().atsDbInit();
   }

   @Override
   public boolean isConfigLoaded() {
      return atsConfigurations != null;
   }
}