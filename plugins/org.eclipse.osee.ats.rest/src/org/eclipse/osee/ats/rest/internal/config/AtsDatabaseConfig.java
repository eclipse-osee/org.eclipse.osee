/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.core.config.OrganizePrograms;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsDatabaseConfig {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsDatabaseConfig(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData run() {
      OseeInfo.setValue(atsApi.getJdbcService().getClient(), "osee.work.def.as.name", "true");

      XResultData results = createAtsFolders();
      if (results.isErrors()) {
         return results;
      }

      // load top team / ai into cache
      IAtsTeamDefinition topTeam =
         atsApi.getTeamDefinitionService().getTeamDefinitionById(AtsArtifactToken.TopTeamDefinition);
      IAtsActionableItem topAi =
         atsApi.getActionableItemService().getActionableItemById(AtsArtifactToken.TopActionableItem);

      IAtsChangeSet changes = atsApi.createChangeSet("Set Top Team Work Definition");
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(topTeam, AtsWorkDefinitionTokens.WorkDef_Team_Default,
         changes);
      changes.setSoleAttributeValue(topAi, AtsAttributeTypes.Actionable, false);
      changes.execute();

      atsApi.clearCaches();

      createUserGroups(atsApi);

      createUserCreationDisabledConfig();

      return results;
   }

   public static void createUserGroups(AtsApi atsApi) {
      if (atsApi.getQueryService().getArtifact(AtsUserGroups.AtsAdmin) == null) {
         IAtsChangeSet changes = atsApi.createChangeSet("Create Admin groups");

         ArtifactToken userGroup = atsApi.getQueryService().getArtifact(CoreArtifactTokens.UserGroups);

         changes.createArtifact(userGroup, AtsUserGroups.AtsAdmin);
         changes.createArtifact(userGroup, AtsUserGroups.AtsTempAdmin);
         changes.execute();
      }
   }

   private void createUserCreationDisabledConfig() {
      atsApi.setConfigValue(AtsUtil.USER_CREATION_DISABLED,
         AtsArtifactTypes.Action.toStringWithId() + ";" + AtsArtifactTypes.TeamWorkflow.toStringWithId());
   }

   public XResultData createAtsFolders() {

      IAtsChangeSet changes = atsApi.createChangeSet("Create ATS Folders");

      ArtifactToken headingArt = atsApi.getQueryService().getOrCreateArtifact(CoreArtifactTokens.OseeConfiguration,
         AtsArtifactToken.HeadingFolder, changes);
      for (ArtifactToken token : Arrays.asList(AtsArtifactToken.TopActionableItem, AtsArtifactToken.TopTeamDefinition,
         AtsArtifactToken.WorkDefinitionsFolder)) {
         atsApi.getQueryService().getOrCreateArtifact(headingArt, token, changes);
      }

      changes.execute();

      AtsConfigEndpointImpl configEp = new AtsConfigEndpointImpl(atsApi, orcsApi, atsApi.getLogger(), null);
      XResultData results = configEp.createUpdateConfig();
      if (results.isErrors()) {
         return results;
      }

      (new OrganizePrograms(atsApi)).run();

      return results;
   }

}