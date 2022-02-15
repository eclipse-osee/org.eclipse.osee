/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.JaxAgileTeam;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigurations {

   private AtsViews views = new AtsViews();
   private ColorColumns colorColumns = new ColorColumns();
   ArtifactId topActionableItem;
   ArtifactId topTeamDefinition;
   private Collection<String> validStateNames = new ArrayList<>();
   private Map<Long, ActionableItem> idToAi = new HashMap<>();
   private Map<Long, TeamDefinition> idToTeamDef = new HashMap<>();
   private Map<Long, Version> idToVersion = new HashMap<>();
   private Map<Long, AtsUser> idToUser = new HashMap<>();
   private Map<Long, JaxProgram> idToProgram = new HashMap<>();
   private Map<Long, JaxAgileTeam> idToAgileTeam = new HashMap<>();
   private Map<Long, JaxAgileFeatureGroup> idToAgileFeature = new HashMap<>();
   private Map<String, Long> userIdToUserArtId = new HashMap<>();
   private Map<String, Long> userNameToUserArtId = new HashMap<>();
   private Map<Long, Long> teamDefToAgileTeam = new HashMap<>();
   private Map<Long, Long> teamDefToProgram = new HashMap<>();
   private Map<Long, Long> featureToAgileTeam = new HashMap<>();
   private Map<String, String> atsConfig = new HashMap<>();

   public AtsViews getViews() {
      return views;
   }

   public void setViews(AtsViews views) {
      this.views = views;
   }

   public ColorColumns getColorColumns() {
      return colorColumns;
   }

   public void setColorColumns(ColorColumns colorColumns) {
      this.colorColumns = colorColumns;
   }

   @JsonIgnore
   public Collection<AtsUser> getUsers() {
      return idToUser.values();
   }

   public Collection<String> getValidStateNames() {
      return validStateNames;
   }

   public void setValidStateNames(Collection<String> validStateNames) {
      this.validStateNames = validStateNames;
   }

   public Map<Long, ActionableItem> getIdToAi() {
      return idToAi;
   }

   public void setIdToAi(Map<Long, ActionableItem> idToAi) {
      this.idToAi = idToAi;
   }

   public Map<Long, TeamDefinition> getIdToTeamDef() {
      return idToTeamDef;
   }

   public void setIdToTeamDef(Map<Long, TeamDefinition> idToTeamDef) {
      this.idToTeamDef = idToTeamDef;
   }

   public Map<Long, Version> getIdToVersion() {
      return idToVersion;
   }

   public void setIdToVersion(Map<Long, Version> idToVersion) {
      this.idToVersion = idToVersion;
   }

   public ArtifactId getTopActionableItem() {
      return topActionableItem;
   }

   public void setTopActionableItem(ArtifactId topActionableItem) {
      this.topActionableItem = topActionableItem;
   }

   public ArtifactId getTopTeamDefinition() {
      return topTeamDefinition;
   }

   public void setTopTeamDefinition(ArtifactId topTeamDefinition) {
      this.topTeamDefinition = topTeamDefinition;
   }

   public void addTeamDef(TeamDefinition teamDef) {
      idToTeamDef.put(teamDef.getId(), teamDef);
   }

   public void addAi(ActionableItem ai) {
      idToAi.put(ai.getId(), ai);
   }

   public void addVersion(Version version) {
      idToVersion.put(version.getId(), version);
   }

   public TeamDefinition getTeamDef(IAtsTeamDefinition teamDef) {
      if (teamDef instanceof TeamDefinition) {
         return (TeamDefinition) teamDef;
      }
      return idToTeamDef.get(teamDef.getId());
   }

   public Map<Long, AtsUser> getIdToUser() {
      return idToUser;
   }

   public void setIdToUser(Map<Long, AtsUser> idToUser) {
      this.idToUser = idToUser;
   }

   public void addUser(AtsUser user) {
      idToUser.put(user.getId(), user);
      userIdToUserArtId.put(user.getUserId(), user.getArtifactId().getId());
      userNameToUserArtId.put(user.getName(), user.getArtifactId().getId());
   }

   public Map<String, Long> getUserIdToUserArtId() {
      return userIdToUserArtId;
   }

   public void setUserIdToUserArtId(Map<String, Long> userIdToUserArtId) {
      this.userIdToUserArtId = userIdToUserArtId;
   }

   public Map<String, Long> getUserNameToUserArtId() {
      return userNameToUserArtId;
   }

   public void setUserNameToUserArtId(Map<String, Long> userNameToUserArtId) {
      this.userNameToUserArtId = userNameToUserArtId;
   }

   public Map<String, String> getAtsConfig() {
      return atsConfig;
   }

   public void setAtsConfig(Map<String, String> atsConfig) {
      this.atsConfig = atsConfig;
   }

   public void addAtsConfig(String key, String value) {
      this.atsConfig.put(key, value);
   }

   @JsonIgnore
   public String getConfigValue(String key) {
      return this.atsConfig.get(key);
   }

   public Map<Long, Long> getTeamDefToAgileTeam() {
      return teamDefToAgileTeam;
   }

   public void setTeamDefToAgileTeam(Map<Long, Long> teamDefToAgileTeam) {
      this.teamDefToAgileTeam = teamDefToAgileTeam;
   }

   public Map<Long, JaxAgileTeam> getIdToAgileTeam() {
      return idToAgileTeam;
   }

   public void setIdToAgileTeam(Map<Long, JaxAgileTeam> idToAgileTeam) {
      this.idToAgileTeam = idToAgileTeam;
   }

   public Map<Long, JaxAgileFeatureGroup> getIdToAgileFeature() {
      return idToAgileFeature;
   }

   public void setIdToAgileFeature(Map<Long, JaxAgileFeatureGroup> idToAgileFeature) {
      this.idToAgileFeature = idToAgileFeature;
   }

   public Map<Long, Long> getFeatureToAgileTeam() {
      return featureToAgileTeam;
   }

   public void setFeatureToAgileTeam(Map<Long, Long> featureToAgileTeam) {
      this.featureToAgileTeam = featureToAgileTeam;
   }

   public Map<Long, JaxProgram> getIdToProgram() {
      return idToProgram;
   }

   public void setIdToProgram(Map<Long, JaxProgram> idToProgram) {
      this.idToProgram = idToProgram;
   }

   public Map<Long, Long> getTeamDefToProgram() {
      return teamDefToProgram;
   }

   public void setTeamDefToProgram(Map<Long, Long> teamDefToProgram) {
      this.teamDefToProgram = teamDefToProgram;
   }

}
