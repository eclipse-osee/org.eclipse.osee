/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Configuration;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.AtsConfiguredBranch;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Default;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamActionableItem_ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamDefinitionToVersion_Version;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Users_User;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.core.config.AbstractAtsConfigurationService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * Loads the configurations from the database and provides to both server and clients through endpoint.
 *
 * @author Donald G Dunne
 */
public class AtsConfigurationsService extends AbstractAtsConfigurationService {

   private final OrcsApi orcsApi;

   public AtsConfigurationsService(AtsApi atsApi, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      // Kick off loading user cache; Runs in background thread, so ok to do this on construction
      atsApi.getUserService().setConfigurationService(this);
   }

   /**
    * Not synchronized to improve performance after cache is initially loaded. Depends on synchronization of load() and
    * its repeated check of atsConfigurations == null
    */
   @Override
   public AtsConfigurations getConfigurations() {
      if (atsConfigurations == null) {
         load(false);
      }
      return atsConfigurations;
   }

   @Override
   public AtsConfigurations getConfigurationsWithPend() {
      return load(true);
   }

   private synchronized AtsConfigurations load(boolean reload) {
      // fast design of get() depends on re-checking atsConfigurations == null here
      if (reload || atsConfigurations == null) {
         if (orcsApi.getAdminOps().isDataStoreInitialized()) {
            atsConfigurations = getAtsConfigurationsFromDb();
         } else {
            // just return an empty one if database is being initialized so don't get NPE
            atsConfigurations = new AtsConfigurations();
         }
      }
      return atsConfigurations;
   }

   private AtsConfigurations getAtsConfigurationsFromDb() {
      List<Long> teamDefIds = new LinkedList<>();
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
      for (ArtifactId art : query.andTypeEquals(TeamDefinition).asArtifactIds()) {
         teamDefIds.add(art.getId());
      }

      List<Long> aiIds = new LinkedList<>();
      for (ArtifactId art : query.andTypeEquals(ActionableItem).asArtifactIds()) {
         aiIds.add(art.getId());
      }

      Collection<ArtifactReadable> artifacts =
         Collections.castAll(atsApi.getQueryService().getArtifacts(Configuration));
      // load ats branch configurations
      AtsConfigurations configs = new AtsConfigurations();
      for (ArtifactReadable art : artifacts) {
         AtsConfiguration config = new AtsConfiguration();
         configs.getConfigs().add(config);
         config.setName(art.getName());
         config.setArtifactId(art);
         config.setBranchId(BranchId.valueOf(art.getSoleAttributeValue(AtsConfiguredBranch, "0")));
         config.setIsDefault(art.getSoleAttributeValue(Default, false));
      }
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);
      AtsViews views = update.getConfigViews();
      // load views
      configs.setViews(views);
      // load color column config
      configs.setColorColumns(update.getColorColumns());
      // load valid state names
      configs.setValidStateNames(update.getValidStateNames());
      // load users
      for (IAtsUser user : atsApi.getUserService().getUsersFromDb()) {
         configs.getUsers().add((AtsUser) user);
      }
      // load admins
      ArtifactReadable atsAdminArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(AtsUserGroups.AtsAdmin);
      if (atsAdminArt != null) {
         for (ArtifactReadable member : atsAdminArt.getRelated(Users_User)) {
            configs.getAtsAdmins().add(member);
         }
      }

      Map<Long, ArtifactReadable> idToArtifact = new HashMap<>();

      List<ArtifactReadable> configArts = Collections.castAll(
         atsApi.getQueryService().getArtifacts(atsApi.getAtsBranch(), false, TeamDefinition, Version, ActionableItem));

      // load ats config objects
      for (ArtifactReadable configArtId : configArts) {
         if (configArtId.isOfType(TeamDefinition)) {
            TeamDefinition teamDef = atsApi.getTeamDefinitionService().createTeamDefinition(configArtId);
            configs.addTeamDef(teamDef);
         } else if (configArtId.isOfType(ActionableItem)) {
            ActionableItem ai = atsApi.getActionableItemService().createActionableItem(configArtId);
            configs.addAi(ai);
         } else if (configArtId.isOfType(Version)) {
            Version version = atsApi.getVersionService().createVersion(configArtId);
            configs.addVersion(version);
         }
         idToArtifact.put(configArtId.getId(), configArtId);
      }

      // load team def tree
      addTeamDefinitionChildrenWIthRecurse(
         (ArtifactReadable) atsApi.getQueryService().getArtifact(AtsArtifactToken.TopTeamDefinition.getId()),
         idToArtifact, configs, teamDefIds);
      configs.setTopTeamDefinition(AtsArtifactToken.TopTeamDefinition);

      // load actionable items tree
      addActionableItemChildrenWIthRecurse(
         (ArtifactReadable) atsApi.getQueryService().getArtifact(AtsArtifactToken.TopActionableItem.getId()),
         idToArtifact, configs, aiIds);
      configs.setTopActionableItem(AtsArtifactToken.TopActionableItem);

      return configs;
   }

   private TeamDefinition addTeamDefinitionChildrenWIthRecurse(ArtifactReadable teamDef, Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs, List<Long> teamDefIds) {
      if (teamDef != null && teamDef.isOfType(TeamDefinition)) {
         TeamDefinition jaxTeamDef = configs.getIdToTeamDef().get(teamDef.getId());
         for (ArtifactReadable childArt : teamDef.getChildren()) {
            if (childArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
               if (teamDefIds.contains(childArt.getId())) {
                  TeamDefinition child =
                     addTeamDefinitionChildrenWIthRecurse(childArt, idToArtifact, configs, teamDefIds);
                  if (child != null) {
                     child.setParentId(teamDef.getId());
                     jaxTeamDef.addChild(child);
                  }
               }
            }
         }
         // add team to version ids
         for (Long versionId : atsApi.getRelationResolver().getRelatedIds(teamDef, TeamDefinitionToVersion_Version)) {
            jaxTeamDef.addVersion(versionId);
            Version version = configs.getIdToVersion().get(versionId);
            version.setTeamDefId(teamDef.getId());
         }
         // add team to ai ids
         for (Long aiId : atsApi.getRelationResolver().getRelatedIds(teamDef, TeamActionableItem_ActionableItem)) {
            ActionableItem jai = configs.getIdToAi().get(aiId);
            if (jai != null) {
               jaxTeamDef.addAi(aiId);
               jai.setTeamDefId(teamDef.getId());
            }
         }
         return jaxTeamDef;
      }
      return null;
   }

   private ActionableItem addActionableItemChildrenWIthRecurse(ArtifactReadable aiArt, Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs, List<Long> aiIds) {
      if (aiArt != null && aiArt.isOfType(ActionableItem)) {
         ActionableItem jaxAi = configs.getIdToAi().get(aiArt.getId());
         for (ArtifactReadable childArt : aiArt.getChildren()) {
            if (childArt.isOfType(AtsArtifactTypes.ActionableItem)) {
               if (aiIds.contains(childArt.getId())) {
                  ActionableItem child = addActionableItemChildrenWIthRecurse(childArt, idToArtifact, configs, aiIds);
                  if (child != null) {
                     child.setParentId(aiArt.getId());
                     jaxAi.addChild(child);
                  }
               }
            }
         }
         return jaxAi;
      }
      return null;
   }

   @Override
   public XResultData configAtsDatabase(AtsApi atsApi) {
      if (isAtsBaseCreated()) {
         XResultData results = new XResultData();
         results.error("ATS base config has already been completed");
         return results;
      }
      AtsDatabaseConfig config = new AtsDatabaseConfig(atsApi, orcsApi);
      return config.run();
   }

}