/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config.operation;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Program;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Version;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamActionableItem_ActionableItem;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamDefinitionToVersion_Version;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.User;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.rest.internal.config.AtsUserServiceServerImpl;
import org.eclipse.osee.ats.rest.internal.config.UpdateAtsConfiguration;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigOperations {

   private final AtsApi atsApi;
   public static Pattern keyValuePattern = Pattern.compile("^(.*)=(.*)", Pattern.DOTALL);
   private final OrcsApi orcsApi;

   public AtsConfigOperations(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public BranchData createBranch(BranchData bd) {
      atsApi.getBranchService().validate(bd, atsApi);
      if (bd.isValidate() && bd.getResults().isErrors()) {
         return bd;
      }

      atsApi.getBranchService().createBranch(bd);
      return bd;
   }

   public AtsConfigurations getAtsConfigurationsFromDb() {

      boolean debugOn = false; // Set to true to enable debugging; false for commit/production

      // load ats branch configurations
      AtsConfigurations configs = new AtsConfigurations();
      Map<Long, ArtifactReadable> idToArtifact = new HashMap<>();

      ElapsedTime time = new ElapsedTime("Server ACS - getAtsConfigurationsFromDb", debugOn);

      // NOTE: Faster to load individually and allows for easier determination of long loads when debugOn
      for (ArtifactTypeToken artType : Arrays.asList(TeamDefinition, ActionableItem, Version, User, Program)) {

         if (debugOn) {
            System.err.println("\n");
         }

         ElapsedTime cTime = new ElapsedTime("Load " + artType.getName(), debugOn);

         ElapsedTime cTime3 = new ElapsedTime("Search " + artType.getName(), debugOn);
         QueryBuilder queryBuilder = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()) //
            .andIsOfType(artType);
         // Loading all relations from TWS to Version, makes loading Versions take too long
         if (artType.equals(Version)) {
            queryBuilder.setNoLoadRelations();
         } else if (artType.equals(User)) {
            queryBuilder.setNoLoadRelations();
         } else if (artType.equals(Program)) {
            queryBuilder.follow(CoreRelationTypes.SupportingInfo_IsSupportedBy);
         }
         Collection<ArtifactReadable> results = queryBuilder.asArtifacts();
         cTime3.end(Units.MSEC);

         ElapsedTime cTime2 = new ElapsedTime("Process " + artType.getName(), debugOn);
         processConfigQueryResults(configs, idToArtifact, results);
         cTime2.end(Units.MSEC);

         cTime.end(Units.MSEC);

      }
      if (debugOn) {
         System.err.println("\n");
      }

      ElapsedTime time7 = new ElapsedTime("Server ACS - Baseline Branch - query", debugOn);
      for (Branch branch : orcsApi.getQueryFactory().branchQuery().andIsOfType(
         BranchType.BASELINE).getResults().getList()) {
         configs.getBranchIdToBranchToken().put(branch.getId(), BranchToken.create(branch.getId(), branch.getName()));
      }
      time7.end();

      ElapsedTime time2 = new ElapsedTime("Server ACS - setConfigValues", debugOn);
      Map<String, String> configValues = setConfigValues(configs);
      time2.end();

      time2.start("Server ACS - views/cols");
      UpdateAtsConfiguration update = new UpdateAtsConfiguration(atsApi, orcsApi);
      AtsViews views = update.getConfigViews(configValues.get(UpdateAtsConfiguration.VIEWS_KEY));
      // load views
      configs.setViews(views);
      // load color column config
      configs.setColorColumns(update.getColorColumns(configValues.get(UpdateAtsConfiguration.COLOR_COLUMN_KEY)));
      time2.end();

      time.end(Units.SEC);

      return configs;
   }

   private void processConfigQueryResults(AtsConfigurations configs, Map<Long, ArtifactReadable> idToArtifact,
      Collection<ArtifactReadable> results) {
      for (ArtifactReadable art : results) {
         try {
            if (art.isOfType(TeamDefinition)) {
               TeamDefinition teamDef = atsApi.getTeamDefinitionService().createTeamDefinition(art);
               configs.addTeamDef(teamDef);
               handleTeamDef(art, teamDef, idToArtifact, configs);
               if (AtsArtifactToken.TopTeamDefinition.getToken().equals(art)) {
                  configs.setTopTeamDefinition(ArtifactId.create(art));
               }
               ArtifactId program = atsApi.getAttributeResolver().getSoleAttributeValue(art,
                  AtsAttributeTypes.ProgramId, ArtifactId.SENTINEL);
               if (program.isValid()) {
                  configs.getTeamDefToProgram().put(teamDef.getId(), program.getId());
               }
               teamDef.setAtsApi(atsApi);
            } else if (art.isOfType(ActionableItem)) {
               ActionableItem ai = atsApi.getActionableItemService().createActionableItem(art);
               configs.addAi(ai);
               handleAi(art, ai, idToArtifact, configs);
               if (AtsArtifactToken.TopActionableItem.getToken().equals(art.getToken())) {
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
            } else if (art.isOfType(AtsArtifactTypes.Program)) {
               JaxProgram program = JaxProgram.create(art, atsApi);
               configs.getIdToProgram().put(program.getId(), program);
               ArtifactId teamDef = atsApi.getAttributeResolver().getSoleAttributeValue(art,
                  AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
               if (teamDef.isValid()) {
                  configs.getTeamDefToProgram().put(teamDef.getId(), program.getId());
               } else {
                  Collection<ArtifactToken> related =
                     atsApi.getRelationResolver().getRelated(art, CoreRelationTypes.SupportingInfo_IsSupportedBy);
                  if (!related.isEmpty()) {
                     for (ArtifactToken relArt : related) {
                        if (relArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
                           configs.getTeamDefToProgram().put(relArt.getId(), program.getId());
                        }
                     }
                  }
               }

            }
            idToArtifact.put(art.getId(), art);
         } catch (Exception ex) {
            XConsoleLogger.err("Exception " + ex.getLocalizedMessage());
         }
      }
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

   private TeamDefinition handleTeamDef(ArtifactReadable teamDefArt, TeamDefinition teamDef,
      Map<Long, ArtifactReadable> idToArtifact, AtsConfigurations configs) {
      ArtifactReadable parent = teamDefArt.getParent();
      if (parent != null) {
         teamDef.setParentId(parent.getId());
      }
      for (ArtifactReadable child : teamDefArt.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.TeamDefinition)) {
            teamDef.getChildren().add(child.getId());
         }
      }
      for (ArtifactId versionId : teamDefArt.getRelatedIds(TeamDefinitionToVersion_Version)) {
         teamDef.getVersions().add(versionId.getId());
      }
      for (ArtifactId aiId : teamDefArt.getRelatedIds(TeamActionableItem_ActionableItem)) {
         teamDef.getAis().add(aiId.getId());
      }
      return teamDef;
   }

   private Version handleVersion(ArtifactReadable verArt, Version ver, Map<Long, ArtifactReadable> idToArtifact,
      AtsConfigurations configs) {
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleAttributeValue(verArt,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isValid()) {
         ver.setTeamDefId(teamDefId.getId());
      }
      return ver;
   }

   private ActionableItem handleAi(ArtifactReadable aiArt, ActionableItem ai, Map<Long, ArtifactReadable> idToArtifact,
      AtsConfigurations configs) {
      ArtifactReadable parent = aiArt.getParentOrNull();
      if (parent != null) {
         ai.setParentId(parent.getId());
      }
      for (ArtifactReadable child : aiArt.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.ActionableItem)) {
            ai.getChildren().add(child.getId());
         }
      }
      for (ArtifactId teamDefId : aiArt.getRelatedIds(AtsRelationTypes.TeamActionableItem_TeamDefinition)) {
         ai.setTeamDefId(teamDefId.getId());
      }
      return ai;
   }

}
