/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo.populate;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.demo.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IPopulateDemoDatabase {

   boolean debug = false;
   boolean isSuccessful = true;
   TeamState toState = TeamState.Implement;

   default public boolean isDebug() {
      return debug;
   }

   default void run() {
      //
   }

   default void setValidationRequired(IAtsChangeSet changes, IAtsTeamWorkflow teamWf) {
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ValidationRequired, true);
   }

   default void setVersion(IAtsTeamWorkflow teamWf, ArtifactToken versionToken, IAtsChangeSet changes) {
      IAtsVersion version = AtsClientService.get().getVersionService().getById(versionToken);
      AtsClientService.get().getVersionService().setTargetedVersion(teamWf, version, changes);
   }

   // Transition to desired state
   default void transitionTo(IAtsTeamWorkflow teamWf, TeamState state, IAtsChangeSet changes) {

      TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, AtsClientService.get().getServices(),
         TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck);

      Result result = dtwm.transitionTo(state, teamWf.getAssignees().iterator().next(), false, changes);
      if (result.isFalse()) {
         throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(), state,
            result.getText());
      }
      if (!teamWf.isCompletedOrCancelled()) {
         // Reset assignees that may have been overwritten during transition
         teamWf.getStateMgr().setAssignees(
            AtsClientService.get().getTeamDefinitionService().getLeads(teamWf.getTeamDefinition()));
      }
   }

}
