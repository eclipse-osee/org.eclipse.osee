/*
 * Created on Mar 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.copy;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionManager;
import org.eclipse.osee.ats.core.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

public class CopyAtsValidation {

   private final ConfigData configData;
   protected final XResultData resultData;

   public CopyAtsValidation(ConfigData configData, XResultData resultData) {
      this.configData = configData;
      this.resultData = resultData;
   }

   public void validate() throws OseeCoreException {
      configData.validateData(resultData);
      if (resultData.isErrors()) {
         return;
      }

      performValidateAtsDatabaseChecks();
      if (resultData.isErrors()) {
         return;
      }

      validateTeamDefinition(configData.getTeamDef());
      validateActionableItem(configData.getActionableItem());
   }

   private void performValidateAtsDatabaseChecks() throws OseeCoreException {
      HashCollection<String, String> testNameToResultsMap = new HashCollection<String, String>();

      // Validate AIs to TeamDefs
      Set<Artifact> aias = new HashSet<Artifact>();
      aias.addAll(ActionableItemManagerCore.getActionableItemsFromItemAndChildren(configData.getActionableItem()));
      ValidateAtsDatabase.testActionableItemToTeamDefinition(testNameToResultsMap, aias);

      // Validate TeamDefs have Workflow Definitions
      Set<Artifact> teamDefs = new HashSet<Artifact>();
      teamDefs.addAll(TeamDefinitionManager.getTeamsFromItemAndChildren(configData.getTeamDef()));
      ValidateAtsDatabase.testTeamDefinitionHasWorkflow(testNameToResultsMap, teamDefs);

      ValidateAtsDatabase.addResultsMapToResultData(resultData, testNameToResultsMap);
   }

   private void validateTeamDefinition(TeamDefinitionArtifact teamDef) throws OseeCoreException {
      String newName = CopyAtsUtil.getConvertedName(configData, teamDef.getName());
      if (newName.equals(teamDef.getName())) {
         resultData.logErrorWithFormat("Could not get new name from name conversion for Team Definition [%s]",
            teamDef.getName());
      }
      for (TeamDefinitionArtifact childTeamDef : TeamDefinitionManager.getTeamsFromItemAndChildren(teamDef)) {
         if (!teamDef.equals(childTeamDef)) {
            validateTeamDefinition(childTeamDef);
         }
      }
   }

   private void validateActionableItem(ActionableItemArtifact aiArt) throws OseeCoreException {
      String newName = CopyAtsUtil.getConvertedName(configData, aiArt.getName());
      if (newName.equals(aiArt.getName())) {
         resultData.logErrorWithFormat("Could not get new name from name conversion for ActionableItem [%s]",
            aiArt.getName());
      }
      for (ActionableItemArtifact childAiArt : ActionableItemManagerCore.getActionableItemsFromItemAndChildren(aiArt)) {
         if (!aiArt.equals(childAiArt)) {
            validateActionableItem(childAiArt);
         }
      }
   }
}
