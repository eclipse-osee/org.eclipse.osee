/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.core.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Vaibhav Y Patel
 */
public class FeatureImpactReferenceColumn extends AtsCoreCodeColumn {

   public FeatureImpactReferenceColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.FeatureImpactReferenceColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsApi.getStoreService().isDeleted(atsObject)) {
         return "<deleted>";
      }
      if (atsObject instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsObject;
         Collection<ArtifactId> featureImpactArtIds = atsApi.getAttributeResolver().getArtifactIdReferences(
            atsObject.getArtifactToken(), AtsAttributeTypes.FeatureImpactReference);
         List<String> featureImpactNames = new ArrayList<>();
         for (ArtifactId featureImpactArtId : featureImpactArtIds) {
            featureImpactNames.add(getFeatureImpactName(teamWf, featureImpactArtId));
         }
         return featureImpactNames.size() > 0 ? Collections.toString(", ", featureImpactNames) : result;
      }
      return result;
   }

   private String getFeatureImpactName(IAtsTeamWorkflow teamWf, ArtifactId featureImpactArtId) {
      IAtsTeamDefinition mainTeamDef =
         atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamWf.getTeamDefinition());
      if (mainTeamDef != null) {
         for (IAtsVersion ver : atsApi.getVersionService().getVersions(mainTeamDef)) {
            BranchId branch = atsApi.getBranchService().getBranch(ver);
            if (branch.isValid()) {
               return atsApi.getQueryService().getArtifact(featureImpactArtId, branch).getName();
            }
         }
      }
      return "Feature Not Found for " + featureImpactArtId.getId();
   }

}
