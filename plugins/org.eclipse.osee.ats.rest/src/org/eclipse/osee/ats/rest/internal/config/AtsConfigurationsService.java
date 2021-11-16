/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileFeatureGroup;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.AgileTeam;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamActionableItem_ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamDefinitionToVersion_Version;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.core.agile.AgileFactory;
import org.eclipse.osee.ats.core.config.AbstractAtsConfigurationService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
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
   Pattern keyValuePattern = Pattern.compile("^(.*)=(.*)", Pattern.DOTALL);

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
   public boolean isConfigLoaded() {
      return atsConfigurations != null;
   }

   @Override
   public AtsConfigurations getConfigurationsWithPend() {
      AtsConfigurations configs = load(true);
      return configs;
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

      // load ats branch configurations
      AtsConfigurations configs = new AtsConfigurations();
      Map<Long, ArtifactReadable> idToArtifact = new HashMap<>();

      boolean debugOn = false; // Set to true to enable debugging
      ElapsedTime time = new ElapsedTime("Server ACS - getAtsConfigurationsFromDb", debugOn);
      if (!debugOn) {
         time.off(); // Turn on to debug (change above to false so doesn't log begin)
      }

      ElapsedTime time2 = new ElapsedTime("Server ACS - query", debugOn);
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
      ResultSet<ArtifactReadable> results =
         query.andTypeEquals(TeamDefinition, ActionableItem, Version, User, AgileTeam, AgileFeatureGroup).getResults();
      processConfigQueryResults(configs, idToArtifact, time2, results);
      time2.end();

      time2.start("Server ACS - setConfigValues");
      Map<String, String> configValues = setConfigValues(configs);
      time2.end();

      time2.start("Server ACS - views/cols/states");
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);
      AtsViews views = update.getConfigViews(configValues.get(UpdateAtsConfiguration.VIEWS_KEY));
      // load views
      configs.setViews(views);
      // load color column config
      configs.setColorColumns(update.getColorColumns(configValues.get(UpdateAtsConfiguration.COLOR_COLUMN_KEY)));
      // load valid state names
      configs.setValidStateNames(atsApi.getWorkDefinitionService().getAllValidStateNamesFromConfig());
      time2.end();

      time.end();

      return configs;
   }

   private void processConfigQueryResults(AtsConfigurations configs, Map<Long, ArtifactReadable> idToArtifact, ElapsedTime time2, ResultSet<ArtifactReadable> results) {
      time2.start("Server ACS - process configs");
      for (ArtifactReadable art : results) {
         try {
            if (art.isOfType(TeamDefinition)) {
               TeamDefinition teamDef = atsApi.getTeamDefinitionService().createTeamDefinition(art);
               configs.addTeamDef(teamDef);
               handleTeamDef(art, teamDef, idToArtifact, configs);
               if (AtsArtifactToken.TopTeamDefinition.equals(art)) {
                  configs.setTopTeamDefinition(ArtifactId.create(art));
               }
               teamDef.setAtsApi(atsApi);
            } else if (art.isOfType(ActionableItem)) {
               ActionableItem ai = atsApi.getActionableItemService().createActionableItem(art);
               configs.addAi(ai);
               handleAi(art, ai, idToArtifact, configs);
               if (AtsArtifactToken.TopActionableItem.equals(art)) {
                  configs.setTopActionableItem(ArtifactId.create(art));
               }
               ai.setAtsApi(atsApi);
            } else if (art.isOfType(Version)) {
               Version version = atsApi.getVersionService().createVersion(art);
               configs.addVersion(version);
               handleVersion(art, version, idToArtifact, configs);
               version.setAtsApi(atsApi);
            } else if (art.isOfType(CoreArtifactTypes.User)) {
               AtsUser user = AtsUserServiceServerImpl.valueOf(art);
               configs.addUser(user);
               user.setAtsApi(atsApi);
               user.getTags().addAll(
                  atsApi.getAttributeResolver().getAttributeValues(art, CoreAttributeTypes.StaticId));
            } else if (art.isOfType(AtsArtifactTypes.AgileTeam)) {
               JaxAgileTeam agileTeam = AgileFactory.createJaxTeam(atsApi.getAgileService().getAgileTeam(art));
               configs.getIdToAgileTeam().put(agileTeam.getId(), agileTeam);
               Collection<ArtifactToken> atsTeams =
                  atsApi.getRelationResolver().getRelated(art, AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam);
               for (ArtifactToken teamDef : atsTeams) {
                  configs.getTeamDefToAgileTeam().put(teamDef.getId(), agileTeam.getId());
               }
            } else if (art.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
               JaxAgileFeatureGroup feature =
                  AgileFactory.createJaxAgileFeatureGroup(atsApi.getAgileService().getAgileFeatureGroup(art));
               configs.getIdToAgileFeature().put(feature.getId(), feature);
               Collection<ArtifactToken> agileTeams =
                  atsApi.getRelationResolver().getRelated(art, AtsRelationTypes.AgileTeamToFeatureGroup_AgileTeam);
               if (!agileTeams.isEmpty()) {
                  ArtifactToken agileTeam = agileTeams.iterator().next();
                  configs.getFeatureToAgileTeam().put(feature.getId(), agileTeam.getId());
               }
            }
            idToArtifact.put(art.getId(), art);
         } catch (Exception ex) {
            XConsoleLogger.err("Exception " + ex.getLocalizedMessage());
         }
      }
      time2.end();
   }

   private Map<String, String> setConfigValues(AtsConfigurations configs) {
      ArtifactToken atsConfig = atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsConfig);
      if (atsConfig != null) {
         for (String keyValue : atsApi.getAttributeResolver().getAttributesToStringList(atsConfig,
            CoreAttributeTypes.GeneralStringData)) {
            Matcher m = keyValuePattern.matcher(keyValue);
            if (m.find()) {
               String key = m.group(1);
               String value = m.group(2);
               configs.addAtsConfig(key, value);
            }
         }
      }
      return configs.getAtsConfig();
   }

   private TeamDefinition handleTeamDef(ArtifactReadable teamDefArt, TeamDefinition teamDef, Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs) {
      ArtifactReadable parent = teamDefArt.getParent();
      if (parent != null) {
         teamDef.setParentId(parent.getId());
      }
      for (ArtifactReadable child : teamDefArt.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.TeamDefinition)) {
            teamDef.getChildren().add(child.getId());
         }
      }
      for (Long versionId : teamDefArt.getRelatedIds(TeamDefinitionToVersion_Version)) {
         teamDef.getVersions().add(versionId);
      }
      for (Long aiId : teamDefArt.getRelatedIds(TeamActionableItem_ActionableItem)) {
         teamDef.getAis().add(aiId);
      }
      teamDef.setHasWorkPackages(
         teamDefArt.getRelatedIds(AtsRelationTypes.TeamDefinitionToWorkPackage_WorkPackage).size() > 0);
      return teamDef;
   }

   private Version handleVersion(ArtifactReadable verArt, Version ver, Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs) {
      for (Long teamDefId : verArt.getRelatedIds(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition)) {
         ver.setTeamDefId(teamDefId);
      }
      return ver;
   }

   private ActionableItem handleAi(ArtifactReadable aiArt, ActionableItem ai, Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs) {
      ArtifactReadable parent = aiArt.getParent();
      if (parent != null) {
         ai.setParentId(parent.getId());
      }
      for (ArtifactReadable child : aiArt.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.ActionableItem)) {
            ai.getChildren().add(child.getId());
         }
      }
      for (Long teamDefId : aiArt.getRelatedIds(AtsRelationTypes.TeamActionableItem_TeamDefinition)) {
         ai.setTeamDefId(teamDefId);
      }
      return ai;
   }

   @Override
   public XResultData configAtsDatabase(AtsApi atsApi) {
      if (isAtsBaseCreated()) {
         XResultData results = new XResultData();
         results.error("ATS base config has already been completed");
         return results;
      }
      AtsDbConfigBase config = new AtsDbConfigBase(atsApi, orcsApi);
      return config.run();
   }
}