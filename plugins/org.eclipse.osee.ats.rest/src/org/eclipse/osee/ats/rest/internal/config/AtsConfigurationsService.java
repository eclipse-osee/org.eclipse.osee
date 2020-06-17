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
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamActionableItem_ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamDefinitionToVersion_Version;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.core.config.AbstractAtsConfigurationService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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

   @SuppressWarnings("unlikely-arg-type")
   private AtsConfigurations getAtsConfigurationsFromDb() {

      // load ats branch configurations
      AtsConfigurations configs = new AtsConfigurations();
      Map<Long, ArtifactReadable> idToArtifact = new HashMap<>();

      ElapsedTime time = new ElapsedTime("Server ACS - getAtsConfigurationsFromDb.loadTeamDefsOld", false);
      time.off(); // Turn on to debug (change above to false so doesn't log begin)
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
      for (ArtifactReadable art : query.andTypeEquals(TeamDefinition, ActionableItem, Version,
         CoreArtifactTypes.User).getResults()) {
         if (art.isOfType(TeamDefinition)) {
            TeamDefinition teamDef = atsApi.getTeamDefinitionService().createTeamDefinition(art);
            configs.addTeamDef(teamDef);
            handleTeamDef(art, teamDef, idToArtifact, configs);
            if (AtsArtifactToken.TopTeamDefinition.equals(art)) {
               configs.setTopTeamDefinition(ArtifactId.valueOf(art.getId()));
            }
            teamDef.setAtsApi(atsApi);
            addExtraAttributes(teamDef, atsApi);
         } else if (art.isOfType(ActionableItem)) {
            ActionableItem ai = atsApi.getActionableItemService().createActionableItem(art);
            configs.addAi(ai);
            handleAi(art, ai, idToArtifact, configs);
            if (AtsArtifactToken.TopActionableItem.equals(art)) {
               configs.setTopActionableItem(ArtifactId.valueOf(art.getId()));
            }
            ai.setAtsApi(atsApi);
            addExtraAttributes(ai, atsApi);
         } else if (art.isOfType(Version)) {
            Version version = atsApi.getVersionService().createVersion(art);
            configs.addVersion(version);
            handleVersion(art, version, idToArtifact, configs);
            version.setAtsApi(atsApi);
            addExtraAttributes(version, atsApi);
         } else if (art.isOfType(CoreArtifactTypes.User)) {
            AtsUser user = AtsUserServiceServerImpl.valueOf(art);
            configs.addUser(user);
            user.setAtsApi(atsApi);
            addExtraAttributes(user, atsApi);
         }
         idToArtifact.put(art.getId(), art);
      }
      time.end();

      time.start("Server ACS - getAtsConfigurationsFromDb.views/cols/states");
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);
      AtsViews views = update.getConfigViews();
      // load views
      configs.setViews(views);
      // load color column config
      configs.setColorColumns(update.getColorColumns());
      // load valid state names
      configs.setValidStateNames(update.getValidStateNames());
      time.end();

      return configs;
   }

   /**
    * Add WorkType and StaticIds as appropriate
    */
   private void addExtraAttributes(IAtsObject atsObject, AtsApi atsApi) {
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         for (IAtsActionableItem ai : atsApi.getActionableItemService().getActionableItems(workItem)) {
            Collection<String> workTypeStrs =
               atsApi.getAttributeResolver().getAttributeValues(ai, AtsAttributeTypes.WorkType);
            for (String workTypeStr : workTypeStrs) {
               try {
                  WorkType workType = WorkType.valueOfOrNone(workTypeStr);
                  if (workType != WorkType.None) {
                     ai.getWorkTypes().add(workType);
                  }
               } catch (Exception ex) {
                  // do nothing
               }
            }
         }
      } else {
         Collection<String> workTypeStrs =
            atsApi.getAttributeResolver().getAttributeValues(atsObject, AtsAttributeTypes.WorkType);
         for (String workTypeStr : workTypeStrs) {
            try {
               WorkType workType = WorkType.valueOfOrNone(workTypeStr);
               if (workType != WorkType.None) {
                  atsObject.getWorkTypes().add(workType);
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      }
      atsObject.getTags().addAll(
         atsApi.getAttributeResolver().getAttributeValues(atsObject, CoreAttributeTypes.StaticId));
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
      AtsDatabaseConfig config = new AtsDatabaseConfig(atsApi);
      return config.run();
   }

   @Override
   public AtsUser getUserByLoginId(String loginId) {
      AtsUser user = null;
      // Don't use cache if not loaded
      if (isConfigLoaded()) {
         for (AtsUser usr : atsApi.getUserService().getUsers()) {
            if (usr.getLoginIds().contains(loginId)) {
               user = usr;
               break;
            }
         }
      }
      if (user == null) {
         ArtifactToken userArt = atsApi.getQueryService().getArtifactFromAttribute(CoreAttributeTypes.LoginId,
            System.getProperty("user.name"), atsApi.getAtsBranch());
         if (userArt.isValid()) {
            user = atsApi.getUserService().getUserById(userArt);
            // Don't use cache if not loaded
            if (isConfigLoaded()) {
               atsApi.getUserService().addUser(user);
            }
         }
      }
      return user;
   }

}
