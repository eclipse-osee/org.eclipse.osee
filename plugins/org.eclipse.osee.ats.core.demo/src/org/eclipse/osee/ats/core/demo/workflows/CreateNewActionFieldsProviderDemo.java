/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.core.demo.workflows;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.CreateNewActionField;
import org.eclipse.osee.ats.core.action.ICreateNewActionFieldsProvider;

/**
 * @author Ryan T. Baldwin
 */
public class CreateNewActionFieldsProviderDemo implements ICreateNewActionFieldsProvider {

   @Override
   public Collection<CreateNewActionField> getCreateNewActionFields(AtsApi atsApi) {
      Collection<CreateNewActionField> fields = new LinkedList<>();
      fields.add(CreateNewActionField.Originator);
      fields.add(CreateNewActionField.Assignees);
      fields.add(CreateNewActionField.TargetedVersion);
      fields.add(CreateNewActionField.Points);
      fields.add(CreateNewActionField.UnplannedWork);
      fields.add(CreateNewActionField.WorkPackage);
      fields.add(CreateNewActionField.Sprint);
      fields.add(CreateNewActionField.FeatureGroup);
      return fields;
   }

   @Override
   public boolean actionableItemHasFields(AtsApi atsApi, Collection<IAtsActionableItem> ais) {
      Collection<IAtsTeamDefinition> teams = atsApi.getActionableItemService().getImpactedTeamDefs(ais);
      for (IAtsTeamDefinitionArtifactToken teamDefToken : getTeamsDefTokens()) {
         IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(teamDefToken);
         if (teams.contains(teamDef)) {
            return true;
         }
      }
      return false;
   }

   private Collection<IAtsTeamDefinitionArtifactToken> getTeamsDefTokens() {
      return Arrays.asList(
         // SAW PL
         DemoArtifactToken.SAW_PL_ARB_TeamDef, DemoArtifactToken.SAW_PL_TeamDef, DemoArtifactToken.SAW_PL_CR_TeamDef,
         DemoArtifactToken.SAW_PL_HW_TeamDef, DemoArtifactToken.SAW_PL_Code_TeamDef,
         DemoArtifactToken.SAW_PL_Test_TeamDef, DemoArtifactToken.SAW_PL_SW_Design_TeamDef,
         DemoArtifactToken.SAW_PL_Requirements_TeamDef, DemoArtifactToken.SAW_PL_ARB_TeamDef,

         // SAW
         DemoArtifactToken.SAW_SW, DemoArtifactToken.SAW_HW, DemoArtifactToken.SAW_Code, DemoArtifactToken.SAW_Test,
         DemoArtifactToken.SAW_SW_Design, DemoArtifactToken.SAW_Requirements

      );
   }

}
