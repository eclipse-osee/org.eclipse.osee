/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.IAtsOperationCache;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * This cache should only be used once and thrown away. eg: ATS Health Check where many checks make the same calls, like
 * get Parent Workflow, and would perform better if it was only searched/loaded once.
 *
 * @author Donald G. Dunne
 */
public class AtsOperationCache implements IAtsOperationCache {

   private final AtsApi atsApi;
   private final Map<Long, IAtsTeamWorkflow> idToTeamWf = new HashMap<>(20000);
   private final Map<Long, IAtsTask> idToTask = new HashMap<>(30000);
   private final Map<Long, IAtsAbstractReview> idToReview = new HashMap<>(4000);
   private List<ArtifactToken> teamDefArts;
   private List<ArtifactToken> aiArts;
   private boolean debug = false;

   public AtsOperationCache(AtsApi atsApi, boolean debug) {
      this.atsApi = atsApi;
      this.debug = debug;
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow(IAtsWorkItem workItem, HealthCheckResults results) {
      if (workItem.isTeamWorkflow()) {
         return (IAtsTeamWorkflow) workItem;
      }
      Collection<ArtifactId> ids = null;
      if (workItem.isTask()) {
         ids = ((ArtifactReadable) workItem.getStoreObject()).getRelatedIds(AtsRelationTypes.TeamWfToTask_TeamWorkflow);
      } else if (workItem.isReview()) {
         ids = ((ArtifactReadable) workItem.getStoreObject()).getRelatedIds(
            AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow);
      }
      if (ids == null || ids.size() == 0) {
         if (debug) {
            results.log(workItem.getArtifactId(), "getParentTeamWorkflow",
               String.format("Error: No parent workflow for %s", workItem.toStringWithId()));
         }
         return null;
      } else if (ids.size() == 2) {
         if (debug) {
            results.log(workItem.getArtifactId(), "getParentTeamWorkflow",
               String.format("Error: Multiple parent workflows for %s", workItem.toStringWithId()));
         }
         return null;
      }

      ArtifactId id = ids.iterator().next();

      IAtsTeamWorkflow teamWf = idToTeamWf.get(id.getId());
      if (teamWf == null) {
         teamWf = atsApi.getQueryService().getTeamWf(id);
         idToTeamWf.put(id.getId(), teamWf);
      }
      return teamWf;
   }

   @Override
   public List<ArtifactToken> getTeamDefinitions() {
      if (teamDefArts == null) {
         teamDefArts = atsApi.getQueryService().getArtifacts(AtsArtifactTypes.TeamDefinition);
      }
      return teamDefArts;
   }

   @Override
   public List<ArtifactToken> getActionableItems() {
      if (aiArts == null) {
         aiArts = atsApi.getQueryService().getArtifacts(AtsArtifactTypes.ActionableItem);
      }
      return aiArts;
   }

   @Override
   public void addTeamWf(ArtifactToken teamWfArt) {
      IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);
      idToTeamWf.put(teamWf.getId(), teamWf);
   }

   @Override
   public void addTask(IAtsTask task) {
      idToTask.put(task.getId(), task);
   }

   @Override
   public Map<Long, IAtsTask> getTasks() {
      return idToTask;
   }

   @Override
   public void addReview(IAtsAbstractReview review) {
      idToReview.put(review.getId(), review);
   }

   @Override
   public Map<Long, IAtsAbstractReview> getReviews() {
      return idToReview;
   }

}
