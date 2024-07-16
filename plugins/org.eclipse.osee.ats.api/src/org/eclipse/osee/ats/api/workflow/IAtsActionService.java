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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionService {

   ActionResult createAction(AtsUser user, String title, String desc, ChangeTypes changeType, String priority, boolean validationRequired, Date needByDate, Collection<IAtsActionableItem> actionableItems, Date createdDate, AtsUser createdBy, Collection<INewActionListener> newActionListeners, IAtsChangeSet changes);

   IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, List<AtsUser> assignees, IAtsChangeSet changes, Date createdDate, AtsUser createdBy, Collection<INewActionListener> newActionListeners, CreateTeamOption... createTeamOption);

   IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, List<? extends AtsUser> assignees, Date createdDate, AtsUser createdBy, ArtifactTypeToken artifactType, Collection<INewActionListener> newActionListeners, IAtsChangeSet changes, CreateTeamOption... createTeamOption);

   /**
    * Auto-add actions to a goal configured with relations to the given ActionableItem or Team Definition
    *
    * @param handledGoal don't handle this goal, else null
    */
   void addActionToConfiguredGoal(IAtsTeamDefinition teamDef, IAtsTeamWorkflow teamWf, Collection<IAtsActionableItem> actionableItems, IAtsGoal handledGoal, IAtsChangeSet changes);

   Collection<IAtsTeamWorkflow> getSiblingTeamWorkflows(IAtsTeamWorkflow teamWf);

   IAtsAction getAction(IAtsTeamWorkflow teamWf);

   void initializeNewStateMachine(IAtsWorkItem workItem, List<? extends AtsUser> assignees, Date createdDate, AtsUser createdBy, WorkDefinition workDefinition, IAtsChangeSet changes);

   void setAtsId(IAtsObject atsObject, IAtsTeamDefinition teamDef, IWorkItemListener workItemListener, IAtsChangeSet changes);

   void setCreatedBy(IAtsWorkItem workItem, AtsUser user, boolean logChange, Date date, IAtsChangeSet changes);

   ActionResult createAction(NewActionData newActionData, IAtsChangeSet changes);

   String getActionStateJson(Collection<IAtsWorkItem> workItemsByLegacyPcrId2);

   /**
    * This will only create the action artifact and not the workflows. It should only be used during cloning and not by
    * normal action/workflow creation.
    */
   IAtsAction createAction(String title, String desc, ChangeTypes changeType, String priority, boolean validationRequired, Date needByDate, IAtsChangeSet changes);

   IAtsGoal createGoal(String title, IAtsChangeSet changes);

   IAtsGoal createGoal(String title, ArtifactTypeToken artifactType, WorkDefinition workDefinition, IAtsTeamDefinition teamDef, IAtsChangeSet changes, IWorkItemListener workItemListener);

}