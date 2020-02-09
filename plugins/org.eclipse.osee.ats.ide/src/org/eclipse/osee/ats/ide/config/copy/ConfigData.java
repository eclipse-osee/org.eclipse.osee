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
package org.eclipse.osee.ats.ide.config.copy;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
   Long newProgramId;

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

   public IAtsTeamDefinition getParentTeamDef() {
      IAtsTeamDefinition parentTeamDef = null;
      if (AtsClientService.get().getTeamDefinitionService().getParentTeamDef(teamDef) != null) {
         parentTeamDef = AtsClientService.get().getTeamDefinitionService().getParentTeamDef(teamDef);
      } else {
         parentTeamDef = AtsClientService.get().getTeamDefinitionService().getTopTeamDefinition();
      }
      return parentTeamDef;
   }

   public IAtsActionableItem getParentActionableItem() {
      IAtsActionableItem parentActionableItem = null;
      // Determine parent actionable item if possible, otherwise use top actionable item
      Collection<ActionableItem> fromAias =
         AtsClientService.get().getActionableItemService().getActionableItems(teamDef);
      if (fromAias.size() == 1) {
         parentActionableItem = fromAias.iterator().next().getParentActionableItem();
      } else {
         parentActionableItem =
            AtsClientService.get().getActionableItemService().getTopActionableItem(AtsClientService.get());
      }
      return parentActionableItem;
   }

   public IAtsActionableItem getActionableItem() {
      return actionableItem;
   }

   public void setActionableItem(IAtsActionableItem actionableItem) {
      this.actionableItem = actionableItem;
   }

   public Long getNewProgramId() {
      return newProgramId;
   }

   public void setNewProgramId(Long newProgramId) {
      this.newProgramId = newProgramId;
   }

}
