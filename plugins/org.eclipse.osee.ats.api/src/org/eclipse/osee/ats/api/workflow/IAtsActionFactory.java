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
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.framework.core.data.IArtifactType;

/**
 * @author Donald G. Dunne
 */
public interface IAtsActionFactory {

   ActionResult createAction(IAtsUser user, String title, String desc, ChangeType changeType, String priority, boolean validationRequired, Date needByDate, Collection<IAtsActionableItem> actionableItems, Date createdDate, IAtsUser createdBy, INewActionListener newActionListener, IAtsChangeSet changes);

   IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, List<IAtsUser> assignees, IAtsChangeSet changes, Date createdDate, IAtsUser createdBy, INewActionListener newActionListener, CreateTeamOption... createTeamOption);

   IAtsTeamWorkflow createTeamWorkflow(IAtsAction action, IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IArtifactType artifactType, INewActionListener newActionListener, IAtsChangeSet changes, CreateTeamOption... createTeamOption);

   /**
    * Auto-add actions to a goal configured with relations to the given ActionableItem or Team Definition
    */
   void addActionToConfiguredGoal(IAtsTeamDefinition teamDef, IAtsTeamWorkflow teamWf, Collection<IAtsActionableItem> actionableItems, IAtsChangeSet changes);

   void initializeNewStateMachine(IAtsWorkItem workItem, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes);

   Collection<IAtsTeamWorkflow> getSiblingTeamWorkflows(IAtsTeamWorkflow teamWf);

   IAtsAction getAction(IAtsTeamWorkflow teamWf);

   void initializeNewStateMachine(IAtsWorkItem workItem, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsWorkDefinition workDefinition, IAtsChangeSet changes);

   void setAtsId(IAtsObject atsObject, IAtsTeamDefinition teamDef, IAtsChangeSet changes);

   void setCreatedBy(IAtsWorkItem workItem, IAtsUser user, boolean logChange, Date date, IAtsChangeSet changes);

   ActionResult createAction(NewActionData newActionData, IAtsChangeSet changes);

}