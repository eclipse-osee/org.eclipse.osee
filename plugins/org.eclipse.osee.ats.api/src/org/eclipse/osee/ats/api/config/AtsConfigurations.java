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
import org.eclipse.osee.ats.api.user.JaxAtsUser;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsConfigurations implements IWorkDefinitionStringProvider {

   private List<AtsConfiguration> configs = new ArrayList<>();
   private AtsViews views = new AtsViews();
   private ColorColumns colorColumns = new ColorColumns();
   List<JaxAtsUser> users = new ArrayList<>();
   List<Long> atsAdmins = new ArrayList<>();
   Long topActionableItem;
   Long topTeamDefinition;
   List<JaxVersion> versions = new ArrayList<>();
   private Collection<String> validStateNames = new ArrayList<>();
   private Map<String, String> workDefIdToWorkDef = new HashMap<>();
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

   public List<JaxAtsUser> getUsers() {
      return users;
   }

   public void setUsers(List<JaxAtsUser> users) {
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
   public List<Long> getAtsAdmins() {
      return atsAdmins;
   }

   public void setAtsAdminsStr(String atsAdmins) {
      parseStringOfLongs(this.atsAdmins, atsAdmins);
   }

   private void parseStringOfLongs(List<Long> uuids, String strOfLongs) {
      if (Strings.isValid(strOfLongs)) {
         for (String uuid : strOfLongs.split(",")) {
            uuids.add(Long.valueOf(uuid));
         }
      }
   }

   @Override
   public Map<String, String> getWorkDefIdToWorkDef() {
      return workDefIdToWorkDef;
   }

   public void setWorkDefIdToWorkDef(Map<String, String> workDefIdToWorkDef) {
      this.workDefIdToWorkDef = workDefIdToWorkDef;
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

   public Long getTopActionableItem() {
      return topActionableItem;
   }

   public void setTopActionableItem(Long topActionableItem) {
      this.topActionableItem = topActionableItem;
   }

   public Long getTopTeamDefinition() {
      return topTeamDefinition;
   }

   public void setTopTeamDefinition(Long topTeamDefinition) {
      this.topTeamDefinition = topTeamDefinition;
   }

}
