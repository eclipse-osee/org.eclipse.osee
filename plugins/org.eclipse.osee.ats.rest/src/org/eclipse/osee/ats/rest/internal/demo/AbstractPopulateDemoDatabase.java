/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.demo;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractPopulateDemoDatabase {

   boolean debug = false;
   boolean isSuccessful = true;
   protected final XResultData rd;
   protected final AtsApi atsApi;

   public AbstractPopulateDemoDatabase(XResultData rd, AtsApi atsApi) {
      this.rd = rd;
      this.atsApi = atsApi;
   }

   public boolean isDebug() {
      return debug;
   }

   public abstract void run();

   public void setValidationRequired(IAtsChangeSet changes, IAtsTeamWorkflow teamWf) {
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ValidationRequired, true);
   }

   public TeamWorkFlowManager getTransitionMgr(IAtsTeamWorkflow teamWf) {
      return new TeamWorkFlowManager(teamWf, atsApi, TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideWorkingBranchCheck);
   }

   public Pair<IAtsTeamWorkflow, Result> transitionToWithPersist(IAtsTeamWorkflow teamWf, TeamState toState,
      AtsUser currentStateUser, Collection<AtsUser> transitionToAssignees, AtsApi atsApi) {
      while (teamWf.getCurrentState().isNotState(toState)) {
         TeamWorkFlowManager mgr = getTransitionMgr(teamWf);
         IAtsChangeSet changes = atsApi.createChangeSet("Transition to " + toState.getName());
         Result result = mgr.transitionTo(toState, currentStateUser, transitionToAssignees, false, changes);
         if (result.isFalse()) {
            return new Pair<IAtsTeamWorkflow, Result>(teamWf, result);
         }
         TransactionToken tx = changes.executeIfNeeded();
         if (tx.isInvalid()) {
            break;
         }
         teamWf = atsApi.getQueryService().getTeamWf(teamWf.getId());
      }
      return new Pair<IAtsTeamWorkflow, Result>(teamWf, Result.TrueResult);
   }

   public IAtsTeamWorkflow setVersionAndReload(IAtsTeamWorkflow teamWf, ArtifactToken versionToken) {
      IAtsChangeSet changes = atsApi.createChangeSet("Set Version");
      IAtsVersion version = atsApi.getVersionService().getVersionById(versionToken);
      atsApi.getVersionService().setTargetedVersion(teamWf, version, changes);
      changes.execute();
      return atsApi.getQueryService().getTeamWf(teamWf.getId());
   }

   // Transition to desired state
   public void transitionTo(IAtsTeamWorkflow teamWf, TeamState state, IAtsChangeSet changes) {

      TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, atsApi, TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideTransitionValidityCheck);

      Result result = dtwm.transitionTo(state, teamWf.getAssignees().iterator().next(), false, changes);
      if (result.isFalse()) {
         throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(), state,
            result.getText());
      }
      if (!teamWf.isCompletedOrCancelled()) {
         // Reset assignees that may have been overwritten during transition
         changes.setAssignees(teamWf, atsApi.getTeamDefinitionService().getLeads(teamWf.getTeamDefinition()));
      }
   }

   public IAtsTeamWorkflow reload(IAtsTeamWorkflow teamWf) {
      teamWf = (IAtsTeamWorkflow) atsApi.getWorkItemService().getWorkItem(teamWf.getArtifactId());
      return teamWf;
   }

   public IAtsTeamWorkflow transitionAndReload(IAtsTeamWorkflow teamWf, TeamState toState) {
      IAtsChangeSet changes = atsApi.createChangeSet("Transition Workflows");
      TeamWorkFlowManager dtwm = new TeamWorkFlowManager(teamWf, atsApi, TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideTransitionValidityCheck);
      Result result = dtwm.transitionTo(toState, teamWf.getAssignees().iterator().next(), false, changes);
      if (result.isFalse()) {
         throw new OseeCoreException("Error transitioning [%s] to state [%s]: [%s]", teamWf.toStringWithId(),
            toState.getName(), result.getText());
      }
      changes.execute();
      ArtifactToken art = atsApi.getQueryService().getArtifact(teamWf.getId());
      return atsApi.getWorkItemService().getTeamWf(art);
   }

   public boolean dataErrored(NewActionData data) {
      if (data.getRd().isErrors()) {
         rd.errorf("Error [%s]", data.getRd().toString());
         return true;
      }
      return false;
   }

}
