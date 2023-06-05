/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.note.IAtsStateNoteService;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.agile.AgileBacklog;
import org.eclipse.osee.ats.core.agile.AgileSprint;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.core.review.DecisionReviewOnTransitionToHook;
import org.eclipse.osee.ats.core.review.PeerReviewOnTransitionToHook;
import org.eclipse.osee.ats.core.review.hooks.AtsDecisionReviewPrepareWorkItemHook;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.hooks.AtsNotificationTransitionHook;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidateManager;
import org.eclipse.osee.ats.core.workflow.hooks.AtsCommitBranchWhenCompleteHook;
import org.eclipse.osee.ats.core.workflow.hooks.AtsForceAssigneesToTeamLeadsWorkItemHook;
import org.eclipse.osee.ats.core.workflow.hooks.AtsHoldOrBlockedTransitionHook;
import org.eclipse.osee.ats.core.workflow.hooks.AtsPeerReviewRoleDefectValidator;
import org.eclipse.osee.ats.core.workflow.hooks.AtsPeerToPeerReviewReviewWorkItemHook;
import org.eclipse.osee.ats.core.workflow.hooks.AtsWriteDiffWhenCompleteHook;
import org.eclipse.osee.ats.core.workflow.hooks.ConfirmPlarbApprovalHook;
import org.eclipse.osee.ats.core.workflow.note.AtsStateNoteServiceImpl;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.util.CopyActionDetails;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceImpl implements IAtsWorkItemService {

   private final ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy;
   protected final AtsApi atsApi;
   private IAtsStateNoteService stateNoteService;
   private static final String CANCEL_HYPERLINK_URL_CONFIG_KEY = "CancelHyperlinkUrl";
   public static final String ATS_DEFAULT_JOURNAL_URL = "/ats/ui/action/ID/journal/USERID";
   protected static Set<IAtsWorkItemHook> workflowHooks = new HashSet<>();
   private static Set<IAtsTransitionHook> transitionHooks = null;

   @Override
   public void addTransitionHook(IAtsTransitionHook hook) {
      getTransitionHooks();
      transitionHooks.add(hook);
   }

   @Override
   public void addWorkItemHook(IAtsWorkItemHook hook) {
      getWorkItemHooks();
      workflowHooks.add(hook);
   }

   public AtsWorkItemServiceImpl() {
      this(null, null);
      // for osgi
   }

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
      } else {
         throw new OseeArgumentException("Unhandled object type");
      }
      return teams;
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      atsApi.clearImplementersCache(workItem);
   }

   @Override
   public Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, StateDefinition toStateDef) {
      return AtsXWidgetValidateManager.validateTransition(workItem, toStateDef, atsApi);
   }

   @Override
   public Collection<IAtsTransitionHook> getTransitionHooks() {
      if (transitionHooks == null) {
         transitionHooks = new HashSet<>();
         transitionHooks.add(new DecisionReviewOnTransitionToHook());
         transitionHooks.add(new PeerReviewOnTransitionToHook());
         transitionHooks.add(new AtsNotificationTransitionHook());
         transitionHooks.add(new AtsDecisionReviewPrepareWorkItemHook());
         transitionHooks.add(new AtsForceAssigneesToTeamLeadsWorkItemHook());
         transitionHooks.add(new AtsPeerToPeerReviewReviewWorkItemHook());
         transitionHooks.add(new AtsCommitBranchWhenCompleteHook());
         transitionHooks.add(new AtsWriteDiffWhenCompleteHook());
         transitionHooks.add(new AtsHoldOrBlockedTransitionHook());
         transitionHooks.add(new AtsPeerReviewRoleDefectValidator());
      }
      return transitionHooks;
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
   public IAtsStateNoteService getStateNoteService() {
      if (stateNoteService == null) {
         stateNoteService = new AtsStateNoteServiceImpl(atsApi);
      }
      return stateNoteService;
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
   public void setAssignees(IAtsWorkItem workItem, Set<AtsUser> assignees, IAtsChangeSet changes) {
      workItem.getStateMgr().setAssignees(assignees);
      changes.add(workItem);
   }

   @Override
   public IAtsWorkItem getWorkItemByAnyId(String actionId) {
      IAtsWorkItem workItem = null;
      ArtifactToken artifact = ArtifactToken.SENTINEL;
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
         } else {
            throw new OseeArgumentException("Artifact %s must be of type IAtsWorkItem", artifact.toStringWithId());
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
      } else {
         artifact = atsApi.getQueryService().getArtifact(artifact);
         teamWf = new TeamWorkflow(atsApi.getLogger(), atsApi, artifact);
      }
      return teamWf;
   }

   @Override
   public String getCancelUrl(IAtsWorkItem workItem, AtsApi atsApi) {
      String cancelActionUrl = atsApi.getConfigValue(CANCEL_HYPERLINK_URL_CONFIG_KEY);
      if (Strings.isValid(cancelActionUrl)) {
         return cancelActionUrl.replaceFirst("ID", workItem.getIdString());
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
      try {
         ArtifactToken art = atsApi.getQueryService().getArtifact(artifact);
         if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
            item = new org.eclipse.osee.ats.core.agile.AgileItem(atsApi.getLogger(), atsApi, art);
         }
      } catch (Exception ex) {
         atsApi.getLogger().error("Exception loading agile item %s: %s", artifact.getId(), Lib.exceptionToString(ex));
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
      return atsApi.getTaskService().getTask(artifact);
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactToken artifact) {
      return atsApi.getReviewService().getReview(artifact);
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
         return actionUrl.replaceFirst("ID", workItem.getIdString());
      }
      return null;
   }

   @Override
   public boolean isCancelHyperlinkConfigured() {
      return Strings.isValid(atsApi.getConfigValue(CANCEL_HYPERLINK_URL_CONFIG_KEY));
   }

   @Override
   public Collection<IAtsTeamWorkflow> getSiblings(IAtsTeamWorkflow teamWf, IAtsTeamDefinition fromTeamDef) {
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
   public Collection<IAtsTeamWorkflow> getSiblings(IAtsTeamWorkflow teamWf) {
      List<IAtsTeamWorkflow> siblings = new ArrayList<IAtsTeamWorkflow>();
      IAtsAction action = teamWf.getParentAction();
      for (IAtsTeamWorkflow child : action.getTeamWorkflows()) {
         if (!child.equals(teamWf)) {
            siblings.add(child);
         }
      }
      return siblings;
   }

   @Override
   public String getChangeTypeStr(IAtsWorkItem workItem) {
      return ChangeTypeColumn.getChangeTypeStr(workItem, atsApi);
   }

   @Override
   public Collection<IAtsWorkItemHook> getWorkItemHooks() {
      if (workflowHooks.isEmpty()) {
         workflowHooks.add(new ConfirmPlarbApprovalHook());
      }
      return workflowHooks;
   }

   @Override
   public void removeListener(IAtsTransitionHook hook) {
      if (transitionHooks != null) {
         transitionHooks.remove(hook);
      }
   }

   @Override
   public StateDefinition getStateByName(IAtsWorkItem workItem, String name) {
      for (StateDefinition stateDef : workItem.getWorkDefinition().getStates()) {
         if (stateDef.getName().equals(name)) {
            return stateDef;
         }
      }
      return null;
   }

   @Override
   public TransitionResults transition(TransitionData transData) {
      TransitionManager transitionMgr = new TransitionManager(transData);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      return results;
   }

   @Override
   public TransitionResults transitionValidate(TransitionData transData) {
      TransitionManager transitionMgr = new TransitionManager(transData);
      TransitionResults results = transitionMgr.handleTransitionValidation(new TransitionResults());
      return results;
   }

   @Override
   public List<StateDefinition> getAllToStates(IAtsWorkItem workItem) {
      List<StateDefinition> allPages = new ArrayList<>();
      StateDefinition currState = workItem.getStateDefinition();
      if (currState == null) {
         return allPages;
      }

      // Add default toState or complete/cancelled return states
      if (currState.getToStates().isEmpty()) {
         if (currState.isCompleted()) {
            StateDefinition completedFromState =
               workItem.getWorkDefinition().getStateByName(workItem.getCompletedFromState());
            if (completedFromState != null && !allPages.contains(completedFromState)) {
               allPages.add(completedFromState);
            }
         }
         if (currState.isCancelled()) {
            StateDefinition cancelledFromState =
               workItem.getWorkDefinition().getStateByName(workItem.getCancelledFromState());
            if (cancelledFromState != null && !allPages.contains(cancelledFromState)) {
               allPages.add(cancelledFromState);
            }
         }
      } else {
         allPages.add(currState.getToStates().iterator().next());
      }

      // Add return-to states
      for (String visitedStateName : workItem.getStateMgr().getVisitedStateNames()) {
         for (StateDefinition state : workItem.getWorkDefinition().getStates()) {
            if (state.getName().equals(visitedStateName)) {
               if (!allPages.contains(state)) {
                  allPages.add(state);
               }
               break;
            }
         }
      }

      // Add remaining toStates
      for (StateDefinition toState : currState.getToStates()) {
         if (!allPages.contains(toState)) {
            allPages.add(toState);
         }
      }

      // Remove current state
      allPages.remove(currState);
      return allPages;
   }

   @Override
   public StateDefinition getDefaultToState(IAtsWorkItem workItem) {
      List<StateDefinition> states = getAllToStates(workItem);
      if (!states.isEmpty()) {
         return states.iterator().next();
      }
      return null;
   }

   @Override
   public JournalData getJournalData(String atsId) {
      JournalData journalData = new JournalData();
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
      if (workItem == null) {
         journalData.getResults().errorf("Invalid ATS Id [%s]", atsId);
         return journalData;
      }
      getJournalData(workItem, journalData);
      return journalData;
   }

   @Override
   public JournalData getJournalData(IAtsWorkItem workItem, JournalData journalData) {
      String journalStr =
         atsApi.getAttributeResolver().getSoleAttributeValueAsString(workItem, AtsAttributeTypes.Journal, "");
      journalData.setCurrentMsg(journalStr);
      getJournalSubscribed(workItem, journalData);
      return journalData;
   }

   @Override
   public JournalData getJournalSubscribed(IAtsWorkItem workItem, JournalData journalData) {
      journalData.getSubscribed().clear();
      Collection<ArtifactId> userArts =
         atsApi.getAttributeResolver().getAttributeValues(workItem, AtsAttributeTypes.JournalSubscriber);
      for (ArtifactId userArt : userArts) {
         AtsUser user = atsApi.getConfigService().getUser(userArt);
         if (user != null) {
            journalData.getSubscribed().add(user);
         }
      }
      return journalData;
   }

   public static String getJournalUrl(String atsIdOrid, String userId, AtsApi atsApi) {
      String journalUrl = System.getProperty("osee.application.server") + ATS_DEFAULT_JOURNAL_URL;
      journalUrl = journalUrl.replaceFirst("ID", atsIdOrid);
      journalUrl = journalUrl.replaceFirst("USERID", userId);
      return journalUrl;
   }

   @Override
   public boolean isBlocked(IAtsWorkItem workItem) {
      return Strings.isValid(atsApi.getAttributeResolver().getSoleAttributeValue(workItem.getStoreObject(),
         AtsAttributeTypes.BlockedReason, ""));
   }

   @Override
   public boolean isOnHold(IAtsWorkItem workItem) {
      return Strings.isValid(atsApi.getAttributeResolver().getSoleAttributeValue(workItem.getStoreObject(),
         AtsAttributeTypes.HoldReason, ""));
   }

   @Override
   public String getCopyActionDetails(IAtsWorkItem workItem) {
      CopyActionDetails cad = new CopyActionDetails(workItem, atsApi);
      return cad.getDetailsString();
   }

   @Override
   public List<ChangeTypes> getChangeTypeOptions(IAtsObject atsObject) {

      WorkDefinition workDef = null;

      if (atsObject instanceof IAtsActionableItem) {
         IAtsActionableItem ai = (IAtsActionableItem) atsObject;
         IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getImpactedTeamDef(ai);
         ArtifactId workDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(teamDef,
            AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
         if (workDefId.isValid()) {
            workDef = atsApi.getWorkDefinitionService().getWorkDefinition(workDefId);
         }
      } else if (atsObject instanceof IAtsTeamDefinition) {
         IAtsTeamDefinition teamDef = (IAtsTeamDefinition) atsObject;
         ArtifactId workDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(teamDef,
            AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
         if (workDefId.isValid()) {
            workDef = atsApi.getWorkDefinitionService().getWorkDefinition(workDefId);
         }
      } else if (atsObject instanceof IAtsTeamWorkflow) {
         workDef = ((IAtsTeamWorkflow) atsObject).getWorkDefinition();
      }

      List<ChangeTypes> changeTypes = null;
      if (workDef != null) {
         List<ChangeTypes> cTypes = workDef.getChangeTypes();
         if (!cTypes.isEmpty()) {
            changeTypes = cTypes;
         }
      }

      if (changeTypes == null) {
         changeTypes = ChangeTypes.DEFAULT_CHANGE_TYPES;
      }

      return changeTypes;
   }

   @Override
   public Pair<Boolean, Collection<ChangeTypes>> hasSameChangeTypes(Collection<IAtsTeamWorkflow> teamWfs) {
      boolean sameChangeTypes = true;
      Collection<ChangeTypes> changeTypes = null;
      for (IAtsTeamWorkflow teamWf : teamWfs) {
         Collection<ChangeTypes> cTypes = getChangeTypeOptions(teamWf);
         if (changeTypes == null) {
            changeTypes = cTypes;
         } else {
            if (!Collections.isEqual(changeTypes, cTypes)) {
               sameChangeTypes = false;
               break;
            }
         }
      }
      return new Pair<Boolean, Collection<ChangeTypes>>(sameChangeTypes, changeTypes);
   }

   @Override
   public List<Priorities> getPrioritiesOptions(IAtsObject atsObject) {

      WorkDefinition workDef = null;

      if (atsObject instanceof IAtsActionableItem) {
         IAtsActionableItem ai = (IAtsActionableItem) atsObject;
         IAtsTeamDefinition teamDef = atsApi.getTeamDefinitionService().getImpactedTeamDef(ai);
         ArtifactId workDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(teamDef,
            AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
         if (workDefId.isValid()) {
            workDef = atsApi.getWorkDefinitionService().getWorkDefinition(workDefId);
         }
      } else if (atsObject instanceof IAtsTeamDefinition) {
         IAtsTeamDefinition teamDef = (IAtsTeamDefinition) atsObject;
         ArtifactId workDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(teamDef,
            AtsAttributeTypes.WorkflowDefinitionReference, ArtifactId.SENTINEL);
         if (workDefId.isValid()) {
            workDef = atsApi.getWorkDefinitionService().getWorkDefinition(workDefId);
         }
      } else if (atsObject instanceof IAtsTeamWorkflow) {
         workDef = ((IAtsTeamWorkflow) atsObject).getWorkDefinition();
      }

      List<Priorities> priorities = null;
      if (workDef != null) {
         List<Priorities> pris = workDef.getPriorities();
         if (!pris.isEmpty()) {
            priorities = pris;
         }
      }

      if (priorities == null) {
         priorities = Priorities.DEFAULT_PRIORITIES;
      }

      return priorities;
   }

   @Override
   public Pair<Boolean, Collection<Priorities>> hasSamePriorities(Collection<IAtsTeamWorkflow> teamWfs) {
      boolean samePriorities = true;
      Collection<Priorities> priorities = null;
      for (IAtsTeamWorkflow teamWf : teamWfs) {
         Collection<Priorities> pris = getPrioritiesOptions(teamWf);
         if (priorities == null) {
            priorities = pris;
         } else {
            if (!Collections.isEqual(priorities, pris)) {
               samePriorities = false;
               break;
            }
         }
      }
      return new Pair<Boolean, Collection<Priorities>>(samePriorities, priorities);
   }

   @Override
   public String getCurrentStateName(IAtsWorkItem workItem) {
      return workItem.getStateMgr().getCurrentStateNameInternal();
   }
}
