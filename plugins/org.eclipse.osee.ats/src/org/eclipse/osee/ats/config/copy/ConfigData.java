/*
 * Created on Mar 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.copy;

import java.util.List;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemManager;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionManager;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

public class ConfigData {

   String searchStr;
   String replaceStr;
   TeamDefinitionArtifact teamDef;
   ActionableItemArtifact actionableItem;
   boolean retainTeamLeads;
   boolean persistChanges;

   public void validateData(XResultData resultData) {
      if (teamDef == null) {
         resultData.logError("Must Select Team Definition");
      }
      if (actionableItem == null) {
         resultData.logError("Must Select Actionable Item");
      }
      if (!Strings.isValid(searchStr)) {
         resultData.logError("Must Enter Search String");
      }
      if (!Strings.isValid(replaceStr)) {
         resultData.logError("Must Enter Replace String");
      }
      if (searchStr != null && replaceStr != null && searchStr.equals(replaceStr)) {
         resultData.logErrorWithFormat("Search string [%s] can't equal replace string [%s]", searchStr, replaceStr);
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

   public TeamDefinitionArtifact getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(TeamDefinitionArtifact teamDef) {
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

   public TeamDefinitionArtifact getParentTeamDef() throws OseeCoreException {
      TeamDefinitionArtifact parentTeamDef = null;
      if (teamDef.getParent() instanceof TeamDefinitionArtifact) {
         parentTeamDef = (TeamDefinitionArtifact) teamDef.getParent();
      } else {
         parentTeamDef = TeamDefinitionManager.getTopTeamDefinition();
      }
      return parentTeamDef;
   }

   public ActionableItemArtifact getParentActionableItem() throws OseeCoreException {
      ActionableItemArtifact parentActionableItem = null;
      // Determine parent actionable item if possible, otherwise use top actionable item
      List<Artifact> fromAias = teamDef.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_ActionableItem);
      if (fromAias.size() == 1) {
         parentActionableItem = (ActionableItemArtifact) fromAias.iterator().next().getParent();
      } else {
         parentActionableItem = ActionableItemManager.getTopActionableItem();
      }
      return parentActionableItem;
   }

   public ActionableItemArtifact getActionableItem() {
      return actionableItem;
   }

   public void setActionableItem(ActionableItemArtifact actionableItem) {
      this.actionableItem = actionableItem;
   }

}
