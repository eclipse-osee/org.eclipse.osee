/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class Pdd52CreateWorkingWithDiagramTreeActions extends AbstractPopulateDemoDatabase {

   public Pdd52CreateWorkingWithDiagramTreeActions(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   static Map<ArtifactToken, ArtifactToken> versionToWorkflowToken;

   static {
      versionToWorkflowToken = new HashMap<>(3);
      versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_1,
         DemoArtifactToken.WorkingWithDiagramTreeForBld1_TeamWf);
      versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_2,
         DemoArtifactToken.WorkingWithDiagramTreeForBld2_TeamWf);
      versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_3,
         DemoArtifactToken.WorkingWithDiagramTreeForBld3_TeamWf);
   }

   public synchronized Map<ArtifactToken, ArtifactToken> getVersionToWorkflowToken() {
      return versionToWorkflowToken;
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      for (ArtifactToken version : getVersionToWorkflowToken().keySet()) {

         ArtifactToken teamWfArtToken = getVersionToWorkflowToken().get(version);

         NewActionData data = atsApi.getActionService() //
            .createActionData(getClass().getSimpleName(), teamWfArtToken.getName(), "Problem with the Diagram Tree") //
            .andAiAndToken(DemoArtifactToken.SAW_SW_Design_AI, teamWfArtToken) //
            .andChangeType(ChangeTypes.Problem).andPriority("3");
         NewActionData newData = atsApi.getActionService().createAction(data);
         if (dataErrored(newData)) {
            return;
         }

         IAtsTeamWorkflow teamWf = newData.getActResult().getAtsTeamWfs().iterator().next();
         TeamState state = getState(version);
         Pair<IAtsTeamWorkflow, Result> result = transitionToWithPersist(teamWf, state,
            teamWf.getAssignees().iterator().next(), teamWf.getAssignees(), atsApi);
         if (result.getSecond().isFalse()) {
            rd.errorf("Transition Failed: " + result.getSecond().getText());
            return;
         }
         setVersionAndReload(teamWf, version);
      }

   }

   private TeamState getState(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? TeamState.Completed : TeamState.Implement;
   }

}
