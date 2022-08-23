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
import java.util.List;
import java.util.Set;
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
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Pair;

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

   IAtsWorkItemNotes getNotes(IAtsWorkItem workItem);

   ITeamWorkflowProvidersLazy getTeamWorkflowProviders();

   IAtsWorkItem getWorkItemByAnyId(String actionId);

   void clearAssignees(IAtsWorkItem workItem, IAtsChangeSet changes);

   void setAssignees(IAtsWorkItem workItem, Set<AtsUser> assignees, IAtsChangeSet changes);

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

   TransitionResults transition(ITransitionHelper helper);

   TransitionResults transitionValidate(TransitionData transData);

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

}
