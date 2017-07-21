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
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStringProvider;
import org.eclipse.osee.ats.api.workdef.WorkDefData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsConfigurations implements IAtsWorkDefinitionStringProvider {

   private List<AtsConfiguration> configs = new ArrayList<>();
   private AtsViews views = new AtsViews();
   private ColorColumns colorColumns = new ColorColumns();
   List<AtsUser> users = new ArrayList<>();
   List<ArtifactId> atsAdmins = new ArrayList<>();
   ArtifactId topActionableItem;
   ArtifactId topTeamDefinition;
   List<JaxVersion> versions = new ArrayList<>();
   private Collection<String> validStateNames = new ArrayList<>();
   private List<WorkDefData> workDefinitions = new ArrayList<>();
   private Map<Long, JaxActionableItem> idToAi = new HashMap<>();
   private Map<Long, JaxTeamDefinition> idToTeamDef = new HashMap<>();
   private Map<Long, JaxVersion> idToVersion = new HashMap<>();

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

   public String getAtsAdminsStr() {
      return Collections.toString(",", atsAdmins);
   }

   @JsonIgnore
   public List<ArtifactId> getAtsAdmins() {
      return atsAdmins;
   }

   public void setAtsAdminsStr(String atsAdmins) {
      this.atsAdmins = Collections.fromString(atsAdmins, ArtifactId::valueOf);
   }

   @Override
   public List<WorkDefData> getWorkDefinitionsData() {
      return workDefinitions;
   }

   public void setWorkDefinitions(List<WorkDefData> workDefinitions) {
      this.workDefinitions = workDefinitions;
   }

   public Map<Long, JaxActionableItem> getIdToAi() {
      return idToAi;
   }

   public void setIdToAi(Map<Long, JaxActionableItem> idToAi) {
      this.idToAi = idToAi;
   }

   public Map<Long, JaxTeamDefinition> getIdToTeamDef() {
      return idToTeamDef;
   }

   public void setIdToTeamDef(Map<Long, JaxTeamDefinition> idToTeamDef) {
      this.idToTeamDef = idToTeamDef;
   }

   public Map<Long, JaxVersion> getIdToVersion() {
      return idToVersion;
   }

   public void setIdToVersion(Map<Long, JaxVersion> idToVersion) {
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

   public void addTeamDef(JaxTeamDefinition teamDef) {
      idToTeamDef.put(teamDef.getUuid(), teamDef);
   }

   public void addAi(JaxActionableItem ai) {
      idToAi.put(ai.getUuid(), ai);
   }

   public void addVersion(JaxVersion version) {
      idToVersion.put(version.getUuid(), version);
   }

   public void addWorkDefinition(WorkDefData workDef) {
      workDefinitions.add(workDef);
   }

}
