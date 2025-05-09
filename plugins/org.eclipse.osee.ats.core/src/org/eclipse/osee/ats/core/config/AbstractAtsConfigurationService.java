/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.config;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.config.tx.AtsConfigTxImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.OseeTypeEnumArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsConfigurationService implements IAtsConfigurationsService {

   protected AtsConfigurations atsConfigurations;
   protected AtsApi atsApi;
   public static Pattern keyValuePattern = Pattern.compile("^(.*)=(.*)", Pattern.DOTALL);

   @Override
   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public IAtsConfigTx createConfigTx(String name) {
      return new AtsConfigTxImpl(name, atsApi);
   }

   @Override
   public boolean isAtsBaseCreated() {
      return atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsTopFolder) != null;
   }

   @Override
   public AtsUser getUserByUserId(String userId) {
      Long artId = getConfigurations().getUserIdToUserArtId().get(userId);
      AtsUser user = getConfigurations().getIdToUser().get(artId);
      return user;
   }

   @Override
   public AtsUser getUserByName(String name) {
      Long artId = getConfigurations().getUserNameToUserArtId().get(name);
      AtsUser user = getConfigurations().getIdToUser().get(artId);
      return user;
   }

   @Override
   public AtsUser getUser(ArtifactId userArt) {
      AtsUser user = getConfigurations().getIdToUser().get(userArt.getId());
      return user;
   }

   @Override
   public AtsUser getUserByLoginId(String loginId) {
      AtsUser user = getConfigurations().getLoginIdToUser().get(loginId);
      return user;
   }

   @Override
   public void createOseeTypeArtifacts(OseeTypeEnumArtifactToken... oseeTypeTokens) {
      IAtsChangeSet changes = atsApi.createChangeSet("Configure Enumerated Artifacts", COMMON);
      ArtifactToken folder = atsApi.getQueryService().getArtifact(CoreArtifactTokens.EnumeratedArtifactsFolder);
      if (folder == null) {
         folder =
            changes.createArtifact(CoreArtifactTokens.OseeConfiguration, CoreArtifactTokens.EnumeratedArtifactsFolder);
      }
      for (OseeTypeEnumArtifactToken enumArtToken : oseeTypeTokens) {
         ArtifactToken enumArtifact = changes.createArtifact(folder, enumArtToken);
         for (String value : enumArtToken.getValues()) {
            changes.addAttributes(enumArtifact, CoreAttributeTypes.IdValue, value);
         }
      }
      changes.execute();
   }

}
