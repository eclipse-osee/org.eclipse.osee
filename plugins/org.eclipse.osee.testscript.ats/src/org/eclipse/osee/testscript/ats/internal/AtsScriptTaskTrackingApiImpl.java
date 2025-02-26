/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript.ats.internal;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.task.track.TaskTrackItem;
import org.eclipse.osee.ats.api.task.track.TaskTrackingData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.testscript.ats.AtsScriptTaskTrackingApi;

/**
 * @author Stephen J. Molaro
 */
public class AtsScriptTaskTrackingApiImpl implements AtsScriptTaskTrackingApi {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;

   public AtsScriptTaskTrackingApiImpl(OrcsApi orcsApi, AtsApi atsApi) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
   }

   @Override
   public void createFailureTasks(BranchId branch, ArtifactId ciSetId, ArtifactToken scriptToken,
      String scriptResultString) {

      ArtifactToken setArt = orcsApi.getQueryFactory().fromBranch(branch).andId(ciSetId).getArtifactOrSentinal();

      String topLevelArtName = String.format("Zenith Scripts: %s", setArt.getName());

      String baseUrl = atsApi.getWebBasepath();
      String branchType = atsApi.getBranchService().getBranchType(branch).getName().toLowerCase();
      String branchId = branch.getIdString();
      String ciSetIdString = ciSetId.getIdString();
      String setName = setArt.getName();

      //Create URLs to put in descriptions
      String zenithSetUrl = String.format("%sci/allScripts/%s/%s?set=%s", baseUrl, branchType, branchId, ciSetIdString);
      String zenithScriptUrl = String.format("%sci/results/%s/%s?set=%s&script=%s", baseUrl, branchType, branchId,
         ciSetIdString, scriptToken.getIdString());

      String zenithSetDesc = String.format(
         "Link to latest run of [%s]. Ensure you are on the correct branch after opening:\n%s", setName, zenithSetUrl);
      String zenithScriptDesc = String.format(
         "%sLink to latest run of this script on [%s]. Ensure you are on the correct branch after opening:\n%s",
         scriptResultString, setName, zenithScriptUrl);

      //Initialize data for task tracking
      TaskTrackingData tasksData = new TaskTrackingData();
      tasksData.setTitle(topLevelArtName);
      tasksData.setTasksReopen(true);
      tasksData.setAiArtId(AtsArtifactToken.ZenithActionableItem.getIdString());
      tasksData.setDescription(zenithSetDesc);

      //Create new task for specific script failure
      TaskTrackItem jTask = new TaskTrackItem();
      tasksData.getTrackItems().getTasks().add(jTask);
      jTask.setTitle(scriptToken.getName());
      jTask.setDescription(zenithScriptDesc);

      //Get the associated script team
      ArtifactReadable scriptTeam =
         orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(CoreArtifactTypes.ScriptTeam).andRelatedTo(
            CoreRelationTypes.TestScriptDefToTeam_TestScriptDef, scriptToken).asArtifactOrSentinel();

      //Determine default user to assign if no team is found.
      String defaultUser = "";
      orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.ScriptTeam).getResults();
      for (ArtifactReadable team : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.ScriptTeam).getResults()) {
         if (team.getSoleAttributeValue(CoreAttributeTypes.IsDefault, false)) {
            String userId = scriptTeam.getSoleAttribute(CoreAttributeTypes.UserId, "").getValue();
            defaultUser = atsApi.getUserService().getUserByUserId(userId).getArtifactId().getIdString();
            break;
         }
      }
      //Save to later determine if user is default and should be changed.
      jTask.setDefaultAssigneesArtIds(defaultUser);

      if (scriptTeam.isValid()) {
         String userId = scriptTeam.getSoleAttribute(CoreAttributeTypes.UserId, "").getValue();
         ArtifactId personOfContact = atsApi.getUserService().getUserByUserId(userId).getArtifactId();
         tasksData.setAssignees(personOfContact.getIdString());
         jTask.setAssigneesArtIds(personOfContact.getIdString());
      } else {
         //Only set to default if there is no associated team
         tasksData.setAssignees(defaultUser);
         jTask.setAssigneesArtIds(defaultUser);
      }

      atsApi.getActionService().createUpdateScriptTaskTrack(tasksData);
   }

}
