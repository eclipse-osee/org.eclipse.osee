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
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkflowHook;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.framework.core.data.ArtifactToken;

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

   Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef);

   Collection<IAtsTransitionHook> getTransitionHooks();

   String getTargetedVersionStr(IAtsTeamWorkflow teamWf);

   String getArtifactTypeShortName(IAtsTeamWorkflow teamWf);

   Collection<IAtsTeamWorkflow> getTeams(Object object);

   IAtsActionableItemService getActionableItemService();

   /**
    * Assigned or Combined Id that will show at the top of the editor. Default is "<ATS Id> / <Legacy PCR Id (if set)>"
    */
   String getCombinedPcrId(IAtsWorkItem workItem);

   IAtsWorkItemNotes getNotes(IAtsWorkItem workItem);

   ITeamWorkflowProvidersLazy getTeamWorkflowProviders();

   IAtsWorkItem getWorkItemByAnyId(String actionId);

   void clearAssignees(IAtsWorkItem workItem, IAtsChangeSet changes);

   void setAssignees(IAtsWorkItem workItem, Set<IAtsUser> assignees, IAtsChangeSet changes);

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
   Collection<IAtsTeamWorkflow> getSiblings(IAtsTeamWorkflow teamWf, IAtsTeamDefinitionArtifactToken fromTeamDef);

   void addTransitionHook(IAtsTransitionHook hook);

   void addWorkflowHook(IAtsWorkflowHook hook);

   void removeListener(IAtsTransitionHook listener1);

   Collection<IAtsWorkflowHook> getWorkflowHooks();

   IAtsStateDefinition getStateByName(IAtsWorkItem workItem, String name);

   Collection<WorkType> getWorkTypes(IAtsWorkItem workItem);

   boolean isWorkType(IAtsWorkItem workItem, WorkType workType);

}
