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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsOperationCache;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;

public class TestWorkflowVersions implements IAtsHealthCheck {

   @Override
   public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
      for (IAtsWorkItem workItem : atsApi.getQueryService().getWorkItemsFromQuery(
         AtsHealthQueries.getArtIdsOfMuiltipleRelsOnSide(atsApi, atsApi.getAtsBranch(),
            AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow))) {
         error(results, workItem, "Team Workflow with mulitple versions found", workItem.toStringWithId());
      }
      return true;
   }

}
