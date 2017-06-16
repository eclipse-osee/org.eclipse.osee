/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.copy;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ConfigData {

   String searchStr;
   String replaceStr;
   IAtsTeamDefinition teamDef;
   IAtsActionableItem actionableItem;
   boolean retainTeamLeads;
   boolean persistChanges;
   Long newProgramUuid;

   public void validateData(XResultData resultData) {
      if (teamDef == null) {
         resultData.error("Must Select Team Definition");
      }
      if (actionableItem == null) {
         resultData.error("Must Select Actionable Item");
      }
      if (!Strings.isValid(searchStr)) {
         resultData.error("Must Enter Search String");
      }
      if (!Strings.isValid(replaceStr)) {
         resultData.error("Must Enter Replace String");
      }
      if (searchStr != null && replaceStr != null && searchStr.equals(replaceStr)) {
         resultData.errorf("Search string [%s] can't equal replace string [%s]", searchStr, replaceStr);
      }
   }

   public String getSearchStr() {
      return searchStr;
   }

   public void setSearchStr(String searchStr) {
      this.searchStr = searchStr;
   }

   public String getReplaceStr() {
      return replaceStr;
   }

   public void setReplaceStr(String replaceStr) {
      this.replaceStr = replaceStr;
   }

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public boolean isRetainTeamLeads() {
      return retainTeamLeads;
   }

   public void setRetainTeamLeads(boolean retainTeamLeads) {
      this.retainTeamLeads = retainTeamLeads;
   }

   public boolean isPersistChanges() {
      return persistChanges;
   }

   public void setPersistChanges(boolean persistChanges) {
      this.persistChanges = persistChanges;
   }

   public IAtsTeamDefinition getParentTeamDef() throws OseeCoreException {
      IAtsTeamDefinition parentTeamDef = null;
      if (teamDef.getParentTeamDef() != null) {
         parentTeamDef = teamDef.getParentTeamDef();
      } else {
         parentTeamDef = TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService());
      }
      return parentTeamDef;
   }

   public IAtsActionableItem getParentActionableItem() throws OseeCoreException {
      IAtsActionableItem parentActionableItem = null;
      // Determine parent actionable item if possible, otherwise use top actionable item
      Collection<IAtsActionableItem> fromAias = teamDef.getActionableItems();
      if (fromAias.size() == 1) {
         parentActionableItem = fromAias.iterator().next().getParentActionableItem();
      } else {
         parentActionableItem = ActionableItems.getTopActionableItem(AtsClientService.get());
      }
      return parentActionableItem;
   }

   public IAtsActionableItem getActionableItem() {
      return actionableItem;
   }

   public void setActionableItem(IAtsActionableItem actionableItem) {
      this.actionableItem = actionableItem;
   }

   public Long getNewProgramUuid() {
      return newProgramUuid;
   }

   public void setNewProgramUuid(Long newProgramUuid) {
      this.newProgramUuid = newProgramUuid;
   }

}
