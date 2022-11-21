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
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsOperationCache;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class TestTaskParent implements IAtsHealthCheck {

   @Override
   public boolean check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes, IAtsOperationCache cache) {
      if (workItem.isTask()) {
         if (atsApi.getRelationResolver().getRelatedOrSentinel(workItem,
            AtsRelationTypes.TeamWfToTask_TeamWorkflow).isInvalid()) {
            error(results, workItem, "Task has no parent");
         }
      }
      return true;
   }

   @Override
   public boolean checkBefore(HealthCheckResults results, AtsApi atsApi, IAtsOperationCache cache) {
      for (IAtsWorkItem workItem : atsApi.getQueryService().getWorkItemsFromQuery(
         AtsHealthQueries.getArtIdsOfMuiltipleRelsOnSide(atsApi, atsApi.getAtsBranch(),
            AtsRelationTypes.TeamWfToTask_Task))) {
         error(results, workItem, "Orphaned Task ", workItem.toStringWithId());
      }
      return true;
   }

}
