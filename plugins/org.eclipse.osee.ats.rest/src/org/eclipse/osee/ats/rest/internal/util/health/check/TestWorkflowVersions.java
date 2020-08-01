/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.rest.internal.util.health.check;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;

public class TestWorkflowVersions implements IAtsHealthCheck {

   private final HashCollectionSet<IAtsTeamDefinition, IAtsVersion> teamDefToVersions =
      new HashCollectionSet<>(HashSet::new);

   @Override
   public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
      if (workItem.isTeamWorkflow()) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         Collection<ArtifactToken> versions =
            atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
         if (versions.size() == 1) {
            IAtsVersion version = atsApi.getQueryService().getConfigItem(versions.iterator().next());
            if (version != null && !getTeamVersions(teamWf.getTeamDefinition(), atsApi).contains(version)) {
               error(results, workItem,
                  "Team workflow " + teamWf.getAtsId() + " has version" + version.toStringWithId() + " that does not belong to teamDefHoldingVersions ");
            }
         }
      }
   }

   /**
    * Cache this cause it's expensive to do repeatedly for the same teamDef
    */
   private Collection<IAtsVersion> getTeamVersions(IAtsTeamDefinition teamDef, AtsApi atsApi) {
      Set<IAtsVersion> teamDefVersions = teamDefToVersions.getValues(teamDef);
      if (teamDefVersions == null) {
         IAtsTeamDefinition teamDefHoldingVers = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
         if (teamDefHoldingVers != null) {
            teamDefVersions = new HashSet<>();
            teamDefVersions.addAll(atsApi.getTeamDefinitionService().getVersions(teamDefHoldingVers));
            teamDefToVersions.put(teamDef, teamDefVersions);
         }
      }
      return teamDefVersions;
   }

   @Override
   public void check(HealthCheckResults results, AtsApi atsApi) {
      for (IAtsWorkItem workItem : atsApi.getQueryService().getWorkItemsFromQuery(
         AtsHealthQueries.getArtIdsOfMuiltipleRelsOnSide(atsApi, atsApi.getAtsBranch(),
            AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow))) {
         error(results, workItem, "Team Workflow with mulitple versions found", workItem.toStringWithId());
      }
   }

}
