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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
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
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.ai.ActionableItemService;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidateManager;
import org.eclipse.osee.ats.core.workflow.note.ArtifactNote;
import org.eclipse.osee.ats.core.workflow.note.AtsWorkItemNotes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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
            AtsRelationTypes.ActionToWorkflow_WorkFlow)) {
            teams.add(atsApi.getWorkItemFactory().getTeamWf(teamWfArt));
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
            new ActionableItemService(atsApi.getAttributeResolver(), atsApi.getStoreService(), atsApi);
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
         artifact = atsApi.getQueryService().getConfigItem(actionId);
      } else if (Strings.isNumeric(actionId)) {
         artifact = atsApi.getQueryService().getArtifact(Long.valueOf(actionId));
      } else {
         artifact = atsApi.getQueryService().getArtifactByAtsId(actionId);
      }
      if (artifact != null) {
         workItem = atsApi.getWorkItemFactory().getWorkItem(artifact);
      }
      return workItem;
   }
}