/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.config.copy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.health.ValidateResults;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsValidation {

   private final ConfigData configData;
   protected final XResultData resultData;
   public static final String COPY_FROM_TAG = "CopyFromTag";

   public CopyAtsValidation(ConfigData configData, XResultData resultData) {
      this.configData = configData;
      this.resultData = resultData;
   }

   public void validate() {
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

   private void performValidateAtsDatabaseChecks() {
      ValidateResults results = new ValidateResults();

      boolean isCopyFromTagSet = configData.isCopyFromTag();

      // Validate AIs to TeamDefs
      Set<Artifact> aias = new HashSet<>();
      List<Artifact> allAiArts = Collections.castAll(AtsApiService.get().getQueryService().getArtifactsFromObjects(
         AtsApiService.get().getActionableItemService().getActionableItemsFromItemAndChildren(
            configData.getActionableItem())));
      for (Artifact aiArt : allAiArts) {
         if (!isCopyFromTagSet || (isCopyFromTagSet && aiArt.getTags().contains(COPY_FROM_TAG))) {
            aias.add(aiArt);
         }
      }

      // Validate TeamDefs have Workflow Definitions
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      Set<IAtsTeamDefinition> allTeamDefs =
         AtsApiService.get().getTeamDefinitionService().getTeamsFromItemAndChildren(configData.getTeamDef());
      for (IAtsTeamDefinition teamDefArt : allTeamDefs) {
         if (!isCopyFromTagSet || (isCopyFromTagSet && teamDefArt.getTags().contains(COPY_FROM_TAG))) {
            teamDefs.add(teamDefArt);
         }
      }

      results.addResultsMapToResultData(resultData);
   }

   private void validateTeamDefinition(IAtsTeamDefinition teamDef) {
      String newName = CopyAtsUtil.getConvertedName(configData, teamDef.getName());
      if (newName.equals(teamDef.getName())) {
         resultData.errorf("Could not get new name from name conversion for Team Definition [%s]", teamDef.getName());
      }
      for (IAtsTeamDefinition childTeamDef : AtsApiService.get().getTeamDefinitionService().getTeamsFromItemAndChildren(
         teamDef)) {
         if (teamDef.notEqual(childTeamDef)) {
            validateTeamDefinition(childTeamDef);
         }
      }
   }

   private void validateActionableItem(IAtsActionableItem aiArt) {
      String newName = CopyAtsUtil.getConvertedName(configData, aiArt.getName());
      if (newName.equals(aiArt.getName())) {
         resultData.errorf("Could not get new name from name conversion for ActionableItem [%s]", aiArt.getName());
      }
      for (IAtsActionableItem childAiArt : AtsApiService.get().getActionableItemService().getActionableItemsFromItemAndChildren(
         aiArt)) {
         if (aiArt.notEqual(childAiArt)) {
            validateActionableItem(childAiArt);
         }
      }
   }
}
