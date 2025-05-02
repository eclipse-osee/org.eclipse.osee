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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigurations {

   private AtsViews views = new AtsViews();
   private ColorColumns colorColumns = new ColorColumns();
   ArtifactId topActionableItem;
   ArtifactId topTeamDefinition;
   private Map<Long, ActionableItem> idToAi = new HashMap<>();
   private Map<Long, TeamDefinition> idToTeamDef = new HashMap<>();
   private Map<Long, Version> idToVersion = new HashMap<>();
   private Map<Long, AtsUser> idToUser = new HashMap<>(1000);
   private Map<String, AtsUser> loginIdToUser = new HashMap<>(1000);
   private Map<Long, JaxProgram> idToProgram = new HashMap<>();
   private Map<String, Long> userIdToUserArtId = new HashMap<>(1000);
   private Map<String, Long> userNameToUserArtId = new HashMap<>(1000);
   private Map<Long, Long> teamDefToProgram = new HashMap<>();
   private Map<Long, BranchToken> branchIdToBranchToken = new HashMap<>();
   private Map<String, String> atsConfig = new HashMap<>();
   private XResultData results = new XResultData();

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

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
      for (String loginId : user.getLoginIds()) {
         if (Strings.isValid(loginId)) {
            loginIdToUser.put(loginId, user);
         } else {
            results.errorf("Invalid loginId for user %s", user.toStringWithId());
         }
      }
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

   public Map<String, AtsUser> getLoginIdToUser() {
      return loginIdToUser;
   }

   public void setLoginIdToUser(Map<String, AtsUser> loginIdToUser) {
      this.loginIdToUser = loginIdToUser;
   }

   public Map<Long, BranchToken> getBranchIdToBranchToken() {
      return branchIdToBranchToken;
   }

   public void setBranchIdToBranchToken(Map<Long, BranchToken> branchIdToBranchToken) {
      this.branchIdToBranchToken = branchIdToBranchToken;
   }

}
