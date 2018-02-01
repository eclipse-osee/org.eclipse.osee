/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.agile.AgileBacklog;
import org.eclipse.osee.ats.core.agile.AgileSprint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkItemFactory implements IAtsWorkItemFactory {

   protected final AtsApi atsApi;
   CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder() //
      .expireAfterWrite(1, TimeUnit.MINUTES);

   private final Cache<ArtifactId, IAtsWorkItem> workItemCache = cacheBuilder.build();

   public AbstractWorkItemFactory(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public Collection<IAtsWorkItem> getWorkItems(Collection<? extends ArtifactToken> artifacts) {
      List<IAtsWorkItem> workItems = new LinkedList<>();
      for (ArtifactToken artifact : artifacts) {
         IAtsWorkItem workItem = getWorkItem(artifact);
         if (workItem != null) {
            workItems.add(workItem);
         }
      }
      return workItems;
   }

   @Override
   public IAtsWorkItem getWorkItem(ArtifactToken artifact) {
      IAtsWorkItem workItem = null;
      try {
         if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.TeamWorkflow)) {
            workItem = getTeamWf(artifact);
         } else if (atsApi.getStoreService().isOfType(artifact,
            AtsArtifactTypes.PeerToPeerReview) || atsApi.getStoreService().isOfType(artifact,
               AtsArtifactTypes.DecisionReview)) {
            workItem = getReview(artifact);
         } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.Task)) {
            workItem = getTask(artifact);
         } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileBacklog)) {
            // note, an agile backlog is also a goal type, so this has to be before the goal
            workItem = getAgileBacklog(artifact);
         } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.Goal)) {
            workItem = getGoal(artifact);
         } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileSprint)) {
            workItem = getAgileSprint(artifact);
         }
      } catch (OseeCoreException ex) {
         atsApi.getLogger().error(ex, "Error getting work item for [%s]", artifact);
      }
      return workItem;
   }

   @Override
   public IAtsTeamWorkflow getTeamWfNoCache(ArtifactId artifact) {
      if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.TeamWorkflow)) {
         return new TeamWorkflow(atsApi.getLogger(), atsApi, (ArtifactToken) artifact);
      }
      return null;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactId artifact) {
      IAtsTeamWorkflow team = (IAtsTeamWorkflow) workItemCache.getIfPresent(artifact);
      if (team == null) {
         if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.TeamWorkflow)) {
            team = new TeamWorkflow(atsApi.getLogger(), atsApi, (ArtifactToken) artifact);
            workItemCache.put(artifact, team);
         }
      }
      return team;
   }

   @Override
   public IAgileSprint getAgileSprint(ArtifactToken artifact) {
      IAgileSprint sprint = null;
      if (artifact instanceof IAgileSprint) {
         sprint = (IAgileSprint) artifact;
      } else {
         sprint = new AgileSprint(atsApi.getLogger(), atsApi, artifact);
      }
      return sprint;
   }

   @Override
   public IAgileBacklog getAgileBacklog(ArtifactToken artifact) {
      IAgileBacklog backlog = null;
      if (artifact instanceof IAgileBacklog) {
         backlog = (IAgileBacklog) artifact;
      } else {
         backlog = new AgileBacklog(atsApi.getLogger(), atsApi, artifact);
      }
      return backlog;
   }

   @Override
   public IAgileItem getAgileItem(ArtifactToken artifact) {
      IAgileItem item = null;
      ArtifactId art = atsApi.getQueryService().getArtifact(artifact);
      if (atsApi.getStoreService().isOfType(art, AtsArtifactTypes.AbstractWorkflowArtifact)) {
         item = new org.eclipse.osee.ats.core.agile.AgileItem(atsApi.getLogger(), atsApi, (ArtifactToken) art);
      }
      return item;
   }

   @Override
   public IAtsWorkItem getWorkItemByAtsId(String atsId) {
      return atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andAttr(AtsAttributeTypes.AtsId,
         atsId).getResults().getOneOrNull();
   }

}
