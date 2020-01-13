/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.agile.AgileBacklog;
import org.eclipse.osee.ats.core.agile.AgileSprint;
import org.eclipse.osee.ats.core.ai.ActionableItemServiceImpl;
import org.eclipse.osee.ats.core.review.DecisionReview;
import org.eclipse.osee.ats.core.review.PeerToPeerReview;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidateManager;
import org.eclipse.osee.ats.core.workflow.note.ArtifactNote;
import org.eclipse.osee.ats.core.workflow.note.AtsWorkItemNotes;
import org.eclipse.osee.ats.core.workflow.util.ChangeTypeUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceImpl implements IAtsWorkItemService {

   private final ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;
   private final AtsApi atsApi;
   private IAtsActionableItemService actionableItemService;
   private final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder() //
      .expireAfterWrite(1, TimeUnit.MINUTES);
   private final Cache<ArtifactId, IAtsWorkItem> workItemCache = cacheBuilder.build();
   private static final String CANCEL_HYPERLINK_URL_CONFIG_KEY = "CancelHyperlinkUrl";

   public AtsWorkItemServiceImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      this.atsApi = atsApi;
      this.teamWorkflowProvidersLazy = teamWorkflowProvidersLazy;
   }

   @Override
   public IStateToken getCurrentState(IAtsWorkItem workItem) {
      ArtifactId artifact = atsApi.getArtifactResolver().get(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Artifact matching [%s]", workItem.toString());
      return workItem.getStateDefinition();
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) {
      ArtifactId artifact = atsApi.getArtifactResolver().get(teamWf);
      Conditions.checkNotNull(artifact, "teamWf", "Can't Find Artifact matching [%s]", teamWf.toString());
      return atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.TeamWorkflowToReview_Review,
         IAtsAbstractReview.class);
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state) {
      ArtifactId artifact = atsApi.getArtifactResolver().get(teamWf);
      Conditions.checkNotNull(artifact, "teamWf", "Can't Find Artifact matching [%s]", teamWf.toString());
      List<IAtsAbstractReview> reviews = new LinkedList<>();
      for (IAtsAbstractReview review : atsApi.getRelationResolver().getRelated(teamWf,
         AtsRelationTypes.TeamWorkflowToReview_Review, IAtsAbstractReview.class)) {
         if (atsApi.getAttributeResolver().getSoleAttributeValue(review, AtsAttributeTypes.RelatedToState, "").equals(
            state.getName())) {
            reviews.add(review);
         }
      }
      return reviews;
   }

   @Override
   public IAtsTeamWorkflow getFirstTeam(Object object) {
      Collection<IAtsTeamWorkflow> related = getTeams(object);
      return related.isEmpty() ? null : related.iterator().next();
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeams(Object object) {
      List<IAtsTeamWorkflow> teams = new LinkedList<>();
      if (object instanceof IAtsAction) {
         for (ArtifactToken teamWfArt : atsApi.getRelationResolver().getRelated((IAtsAction) object,
            AtsRelationTypes.ActionToWorkflow_TeamWorkflow)) {
            teams.add(atsApi.getWorkItemService().getTeamWf(teamWfArt));
         }
      } else if (object instanceof ActionResult) {
         return Collections.castAll(AtsObjects.getArtifacts(((ActionResult) object).getTeamWfArts()));
      }
      return teams;
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      atsApi.clearImplementersCache(workItem);
   }

   @Override
   public Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef) {
      return AtsXWidgetValidateManager.validateTransition(workItem, toStateDef, atsApi);
   }

   @Override
   public Collection<ITransitionListener> getTransitionListeners() {
      return atsApi.getTransitionListeners();
   }

   @Override
   public String getTargetedVersionStr(IAtsTeamWorkflow teamWf) {
      IAtsVersion targetedVersion = atsApi.getVersionService().getTargetedVersionByTeamWf(teamWf);
      if (targetedVersion != null) {
         return targetedVersion.getName();
      }
      return "";
   }

   @Override
   public String getArtifactTypeShortName(IAtsTeamWorkflow teamWf) {
      for (ITeamWorkflowProvider atsTeamWorkflow : teamWorkflowProvidersLazy.getProviders()) {
         String typeName = atsTeamWorkflow.getArtifactTypeShortName(teamWf);
         if (Strings.isValid(typeName)) {
            return typeName;
         }
      }
      return null;
   }

   @Override
   public IAtsActionableItemService getActionableItemService() {
      if (actionableItemService == null) {
         actionableItemService =
            new ActionableItemServiceImpl(atsApi.getAttributeResolver(), atsApi.getStoreService(), atsApi);
      }
      return actionableItemService;
   }

   @Override
   public String getCombinedPcrId(IAtsWorkItem workItem) {
      String id = "";
      for (ITeamWorkflowProvider provider : TeamWorkflowProviders.getTeamWorkflowProviders()) {
         try {
            if (provider.isResponsibleFor(workItem)) {
               String computedPcrId = provider.getComputedPcrId(workItem);
               if (Strings.isValid(computedPcrId)) {
                  id = computedPcrId;
               }
            }
         } catch (Exception ex) {
            atsApi.getLogger().error(ex, "Error with provider %s", provider.toString());
         }
      }
      if (Strings.isInValid(id)) {
         String legacyPcrId =
            atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.LegacyPcrId, "");
         if (Strings.isValid(legacyPcrId)) {
            return String.format("%s - %s", workItem.getAtsId(), legacyPcrId);
         } else {
            id = workItem.getAtsId();
         }
      }
      return id;
   }

   @Override
   public IAtsWorkItemNotes getNotes(IAtsWorkItem workItem) {
      return new AtsWorkItemNotes(new ArtifactNote(workItem, atsApi), atsApi);
   }

   @Override
   public ITeamWorkflowProvidersLazy getTeamWorkflowProviders() {
      return teamWorkflowProvidersLazy;
   }

   @Override
   public void clearAssignees(IAtsWorkItem workItem, IAtsChangeSet changes) {
      workItem.getStateMgr().clearAssignees();
      changes.add(workItem);
   }

   @Override
   public void setAssignees(IAtsWorkItem workItem, Set<IAtsUser> assignees, IAtsChangeSet changes) {
      workItem.getStateMgr().setAssignees(assignees);
      changes.add(workItem);
   }

   @Override
   public IAtsWorkItem getWorkItemByAnyId(String actionId) {
      IAtsWorkItem workItem = null;
      ArtifactToken artifact = null;
      if (GUID.isValid(actionId)) {
         artifact = atsApi.getQueryService().getArtifactByGuidOrSentinel(actionId);
      } else if (Strings.isNumeric(actionId)) {
         artifact = atsApi.getQueryService().getArtifact(Long.valueOf(actionId));
      } else {
         artifact = atsApi.getQueryService().getArtifactByAtsId(actionId);
      }
      if (artifact.isValid()) {
         workItem = atsApi.getWorkItemService().getWorkItem(artifact);
      }
      return workItem;
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
   public IAtsWorkItem getWorkItem(Long id) {
      ArtifactToken art = atsApi.getQueryService().getArtifact(id);
      if (art == null || art.isInvalid() || !art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return null;
      }
      return getWorkItem(art);
   }

   @Override
   public IAtsWorkItem getWorkItem(ArtifactToken artifact) {
      IAtsWorkItem workItem = null;
      try {
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            workItem = getTeamWf(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.PeerToPeerReview) || artifact.isOfType(
            AtsArtifactTypes.DecisionReview)) {
            workItem = getReview(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.Task)) {
            workItem = getTask(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.AgileBacklog)) {
            // note, an agile backlog is also a goal type, so this has to be before the goal
            workItem = getAgileBacklog(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.Goal)) {
            workItem = getGoal(artifact);
         } else if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
            workItem = getAgileSprint(artifact);
         }
      } catch (OseeCoreException ex) {
         atsApi.getLogger().error(ex, "Error getting work item for [%s]", artifact);
      }
      return workItem;
   }

   @Override
   public IAtsTeamWorkflow getTeamWfNoCache(ArtifactToken artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         return new TeamWorkflow(atsApi.getLogger(), atsApi, artifact);
      }
      return null;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactToken artifact) {
      IAtsTeamWorkflow teamWf = null;
      if (artifact instanceof IAtsTeamWorkflow) {
         teamWf = (IAtsTeamWorkflow) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         teamWf = new TeamWorkflow(atsApi.getLogger(), atsApi, artifact);
      }
      return teamWf;
   }

   @Override
   public String getCancelUrl(IAtsWorkItem workItem, AtsApi atsApi) {
      String cancelActionUrl = atsApi.getConfigValue(CANCEL_HYPERLINK_URL_CONFIG_KEY);
      if (Strings.isValid(cancelActionUrl)) {
         return cancelActionUrl.replaceFirst("ID", String.valueOf(workItem.getId()));
      }
      return null;
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
      ArtifactToken art = atsApi.getQueryService().getArtifact(artifact);
      if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         item = new org.eclipse.osee.ats.core.agile.AgileItem(atsApi.getLogger(), atsApi, art);
      }
      return item;
   }

   @Override
   public IAtsWorkItem getWorkItemByAtsId(String atsId) {
      return atsApi.getQueryService().createQuery(WorkItemType.WorkItem).andAttr(AtsAttributeTypes.AtsId,
         atsId).getResults().getOneOrDefault(IAtsWorkItem.SENTINEL);
   }

   @Override
   public IAtsGoal getGoal(ArtifactToken artifact) {
      IAtsGoal goal = null;
      if (artifact instanceof IAtsGoal) {
         goal = (IAtsGoal) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.Goal)) {
         goal = new Goal(atsApi.getLogger(), atsApi, artifact);
      }
      return goal;
   }

   @Override
   public IAtsTask getTask(ArtifactToken artifact) {
      IAtsTask task = null;
      if (artifact instanceof IAtsTask) {
         task = (IAtsTask) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.Task)) {
         task = new Task(atsApi.getLogger(), atsApi, artifact);
      }
      return task;
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactToken artifact) {
      IAtsAbstractReview review = null;
      if (artifact instanceof IAtsAbstractReview) {
         review = (IAtsAbstractReview) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
         review = new PeerToPeerReview(atsApi.getLogger(), atsApi, artifact);
      } else {
         review = new DecisionReview(atsApi.getLogger(), atsApi, artifact);
      }
      return review;
   }

   @Override
   public IAtsAction getAction(ArtifactToken artifact) {
      IAtsAction action = null;
      if (artifact instanceof IAtsAction) {
         action = (IAtsAction) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.Action)) {
         action = new Action(atsApi, artifact);
      }
      return action;
   }

   @Override
   public String getHtmlUrl(IAtsWorkItem workItem, AtsApi atsApi) {
      String actionUrl = atsApi.getConfigValue("ActionUrl_26_0");
      if (Strings.isValid(actionUrl)) {
         return actionUrl.replaceFirst("ID", String.valueOf(workItem.getId()));
      }
      return null;
   }

   @Override
   public boolean isCancelHyperlinkConfigured() {
      return Strings.isValid(atsApi.getConfigValue(CANCEL_HYPERLINK_URL_CONFIG_KEY));
   }

   @Override
   public Collection<IAtsTeamWorkflow> getSiblings(IAtsTeamWorkflow teamWf, IAtsTeamDefinitionArtifactToken fromTeamDef) {
      List<IAtsTeamWorkflow> siblings = new ArrayList<IAtsTeamWorkflow>();
      IAtsAction action = teamWf.getParentAction();
      for (IAtsTeamWorkflow child : action.getTeamWorkflows()) {
         if (child.getTeamDefinition().equals(fromTeamDef)) {
            siblings.add(child);
         }
      }
      return siblings;
   }

   @Override
   public String getChangeTypeStr(IAtsWorkItem workItem) {
      return ChangeTypeUtil.getChangeTypeStr(workItem, atsApi);
   }

}
