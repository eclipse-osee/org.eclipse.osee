/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util.health.check;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class TestTaskParent implements IAtsHealthCheck {

   @Override
   public void check(ArtifactId artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi) {
      if (workItem.isTask()) {
         if (atsApi.getRelationResolver().getRelatedOrSentinel(workItem,
            AtsRelationTypes.TeamWfToTask_TeamWorkflow).isInvalid()) {
            error(results, workItem, "Task has no parent");
         }
      }
   }

   @Override
   public void check(HealthCheckResults results, AtsApi atsApi) {
      for (IAtsWorkItem workItem : atsApi.getQueryService().getWorkItemsFromQuery(
         AtsHealthQueries.getArtIdsOfMuiltipleRelsOnSide(atsApi, atsApi.getAtsBranch(),
            AtsRelationTypes.TeamWfToTask_Task))) {
         error(results, workItem, "Orphaned Task ", workItem.toStringWithId());
      }
   }
}
