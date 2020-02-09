/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigurations {

   private List<AtsConfiguration> configs = new ArrayList<>();
   private AtsViews views = new AtsViews();
   private ColorColumns colorColumns = new ColorColumns();
   List<AtsUser> users = new ArrayList<>();
   List<ArtifactId> atsAdmins = new ArrayList<>();
   ArtifactId topActionableItem;
   ArtifactId topTeamDefinition;
   private Collection<String> validStateNames = new ArrayList<>();
   private Map<Long, ActionableItem> idToAi = new HashMap<>();
   private Map<Long, TeamDefinition> idToTeamDef = new HashMap<>();
   private Map<Long, Version> idToVersion = new HashMap<>();

   public List<AtsConfiguration> getConfigs() {
      return configs;
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

   public List<AtsUser> getUsers() {
      return users;
   }

   public void setUsers(List<AtsUser> users) {
      this.users = users;
   }

   public Collection<String> getValidStateNames() {
      return validStateNames;
   }

   public void setConfigs(List<AtsConfiguration> configs) {
      this.configs = configs;
   }

   public void setValidStateNames(Collection<String> validStateNames) {
      this.validStateNames = validStateNames;
   }

   public List<ArtifactId> getAtsAdmins() {
      return atsAdmins;
   }

   public void setAtsAdminsStr(String atsAdmins) {
      this.atsAdmins = Collections.fromString(atsAdmins, ArtifactId::valueOf);
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

}
