/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.config.tx.AtsConfigTxImpl;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsConfigurationService implements IAtsConfigurationsService {

   protected AtsConfigurations atsConfigurations;
   protected AtsApi atsApi;

   @Override
   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public IAtsConfigTx createConfigTx(String name, IAtsUser asUser) {
      return new AtsConfigTxImpl(name, atsApi, asUser);
   }

   @Override
   public boolean isAtsBaseCreated() {
      return atsApi.getQueryService().getArtifact(AtsArtifactToken.HeadingFolder) != null;
   }

   @Override
   public AtsUser getUserByUserId(String userId) {
      Long artId = getConfigurations().getUserIdToUserArtId().get(userId);
      AtsUser user = getConfigurations().getIdToUser().get(artId);
      return user;
   }

   @Override
   public IAtsUser getUserByName(String name) {
      Long artId = getConfigurations().getUserNameToUserArtId().get(name);
      AtsUser user = getConfigurations().getIdToUser().get(artId);
      return user;
   }

}
