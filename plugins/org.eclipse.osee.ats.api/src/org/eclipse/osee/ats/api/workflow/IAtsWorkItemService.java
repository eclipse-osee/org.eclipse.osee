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

package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.note.IAtsStateNoteService;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemService {

   String getChangeTypeStr(IAtsWorkItem workItem);

   IStateToken getCurrentState(IAtsWorkItem workItem);

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf);

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state);

   IAtsTeamWorkflow getFirstTeam(Object object);

   void clearImplementersCache(IAtsWorkItem workItem);

   Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, StateDefinition toStateDef);

   Collection<IAtsTransitionHook> getTransitionHooks();

   String getTargetedVersionStr(IAtsTeamWorkflow teamWf);

   String getArtifactTypeShortName(IAtsTeamWorkflow teamWf);

   /*
    * @param object is IAtsAction or ActionResult
    */
   Collection<IAtsTeamWorkflow> getTeams(Object object);

   /**
    * Assigned or Combined Id that will show at the top of the editor. Default is "<ATS Id> / <Legacy PCR Id (if set)>"
    */
   String getCombinedPcrId(IAtsWorkItem workItem);

   IAtsStateNoteService getStateNoteService();

   ITeamWorkflowProvidersLazy getTeamWorkflowProviders();

   IAtsWorkItem getWorkItemByAnyId(String actionId);

   IAtsWorkItem getWorkItem(ArtifactToken artifact);

   IAtsTask getTask(ArtifactToken artifact);

   IAtsAbstractReview getReview(ArtifactToken artifact);

   IAtsGoal getGoal(ArtifactToken artifact);

   IAtsAction getAction(ArtifactToken artifact);

   IAtsWorkItem getWorkItemByAtsId(String atsId);

   IAgileSprint getAgileSprint(ArtifactToken artifact);

   IAgileBacklog getAgileBacklog(ArtifactToken artifact);

   IAgileItem getAgileItem(ArtifactToken artifact);

   Collection<IAtsWorkItem> getWorkItems(Collection<? extends ArtifactToken> artifacts);

   IAtsTeamWorkflow getTeamWfNoCache(ArtifactToken artifact);

   IAtsTeamWorkflow getTeamWf(ArtifactToken artifact);

   String getCancelUrl(IAtsWorkItem workItem, AtsApi atsApi);

   String getHtmlUrl(IAtsWorkItem workItem, AtsApi atsApi);

   boolean isCancelHyperlinkConfigured();

   IAtsWorkItem getWorkItem(Long id);

   /**
    * @return the siblings that are fromTeamDef; this can include the given teamWf
    */
   Collection<IAtsTeamWorkflow> getSiblings(IAtsTeamWorkflow teamWf, IAtsTeamDefinition fromSiblingTeam);

   /**
    * @return all sibling team workflow; does not include this one
    */
   Collection<IAtsTeamWorkflow> getSiblings(IAtsTeamWorkflow teamWf);

   void addTransitionHook(IAtsTransitionHook hook);

   void addWorkItemHook(IAtsWorkItemHook hook);

   void removeListener(IAtsTransitionHook listener1);

   Collection<IAtsWorkItemHook> getWorkItemHooks();

   StateDefinition getStateByName(IAtsWorkItem workItem, String name);

   TransitionResults transition(TransitionData transData);

   TransitionResults transitionValidate(TransitionData transData);

   /**
    * Validate if user is allowed to transition or not based on the user group set to the statedef.
    */
   void validateUserGroupTransition(IAtsWorkItem workItem, StateDefinition toStateDef, TransitionResults results);

   /**
    * @return Ordered list of states able to transition to with default, then return states, then toStates
    */
   List<StateDefinition> getAllToStates(IAtsWorkItem workItem);

   StateDefinition getDefaultToState(IAtsWorkItem workItem);

   JournalData getJournalData(IAtsWorkItem workItem, JournalData journalData);

   JournalData getJournalData(String atsId);

   JournalData getJournalSubscribed(IAtsWorkItem workItem, JournalData journalData);

   boolean isBlocked(IAtsWorkItem workItem);

   boolean isOnHold(IAtsWorkItem workItem);

   String getCopyActionDetails(IAtsWorkItem workItem);

   List<ChangeTypes> getChangeTypeOptions(IAtsObject atsObject);

   Pair<Boolean, Collection<ChangeTypes>> hasSameChangeTypes(Collection<IAtsTeamWorkflow> teamWfs);

   Pair<Boolean, Collection<Priorities>> hasSamePriorities(Collection<IAtsTeamWorkflow> teamWfs);

   List<Priorities> getPrioritiesOptions(IAtsObject atsObject);

   String getCurrentStateName(IAtsWorkItem workItem);

   boolean isFavorite(IAtsWorkItem workItem, AtsUser user);

   boolean isSubcribed(IAtsWorkItem workItem, AtsUser user);

   List<AtsUser> getAssignees(IAtsWorkItem workItem);

   default public boolean isUnAssignedSolely(IAtsWorkItem workItem) {
      return getAssignees(workItem).size() == 1 && isUnAssigned(workItem);
   }

   default public String getAssigneesStr(IAtsWorkItem workItem) {
      return Collections.toString("; ", getAssignees(workItem));
   }

   default public String getAssigneesStr(IAtsWorkItem workItem, int length) {
      return Strings.truncate(Collections.toString("; ", getAssignees(workItem)), length);
   }

   default public boolean isUnAssigned(IAtsWorkItem workItem) {
      return getAssignees(workItem).contains(AtsCoreUsers.UNASSIGNED_USER);
   }

   default public long getTimeInState(IAtsWorkItem workItem, IStateToken state) {
      if (state == null) {
         return 0;
      }
      IAtsLogItem logItem = getStateStartedData(workItem, state);
      if (logItem == null) {
         return 0;
      }
      return new Date().getTime() - logItem.getDate().getTime();
   }

   default public IAtsLogItem getStateStartedData(IAtsWorkItem workItem, IStateToken state) {
      return getStateStartedData(workItem, state.getName());
   }

   default public IAtsLogItem getStateStartedData(IAtsWorkItem workItem, String stateName) {
      return workItem.getLog().getStateEvent(LogType.StateEntered, stateName);
   }

   default public IAtsLogItem getStateCompletedData(IAtsWorkItem workItem, IStateToken state) {
      return getStateCompletedData(workItem, state.getName());
   }

   default public IAtsLogItem getStateCompletedData(IAtsWorkItem workItem, String stateName) {
      return workItem.getLog().getStateEvent(LogType.StateComplete, stateName);
   }

   default public IAtsLogItem getStateCancelledData(IAtsWorkItem workItem, IStateToken state) {
      return getStateCancelledData(workItem, state.getName());
   }

   default public IAtsLogItem getStateCancelledData(IAtsWorkItem workItem, String stateName) {
      return workItem.getLog().getStateEvent(LogType.StateCancelled, stateName);
   }

   default public double getTimeInState(IAtsWorkItem workItem) {
      return getTimeInState(workItem, workItem.getCurrentState());
   }

   /**
    * This method is for backward compatibility and should NOT be used outside core ATS
    */
   IAtsStateManager getStateMgr(IAtsWorkItem workItem);

   void internalClearStateManager(IAtsWorkItem workItem);

   default public String getWorkflowTitle(IAtsWorkItem workItem, String tabName) {
      String artifactTypeName = workItem.getArtifactTypeName();
      if (workItem.isChangeRequest()) {
         artifactTypeName = "CR";
      } else if (workItem.isTeamWorkflow()) {
         artifactTypeName = "TW";
      } else if (workItem.isBacklog()) {
         artifactTypeName = "BKLG";
      } else if (workItem.isTask()) {
         artifactTypeName = "TSK";
      } else if (workItem.isPeerReview()) {
         artifactTypeName = "PEER";
      } else if (workItem.isPeerReview()) {
         artifactTypeName = "DECISION";
      } else if (workItem.isSprint()) {
         artifactTypeName = "SPRINT";
      }

      String titleString = getTooltipTitle(workItem, tabName, artifactTypeName);
      String displayableTitle = Strings.escapeAmpersands(titleString);
      return displayableTitle;
   }

   default public String getTooltipTitle(IAtsWorkItem workItem, String tabName, String shortType) {
      String formTitle = (Strings.isValid(tabName) ? tabName + " - " : "");
      if (workItem.getParentTeamWorkflow() != null) {
         formTitle += String.format("%s - %s", shortType, workItem.getParentTeamWorkflow().getTeamDefinition());
      } else {
         formTitle += String.format("%s", shortType);
      }
      return formTitle;

   }

   boolean isAllowSiblingCreation(IAtsWorkItem workItem);
}