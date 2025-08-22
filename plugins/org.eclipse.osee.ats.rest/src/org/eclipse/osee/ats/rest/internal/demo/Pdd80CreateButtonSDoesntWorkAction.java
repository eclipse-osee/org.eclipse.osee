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

import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.ButtonSDoesntWorkOnHelp_TeamWf;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public class Pdd80CreateButtonSDoesntWorkAction extends AbstractPopulateDemoDatabase {

   public Pdd80CreateButtonSDoesntWorkAction(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      NewActionData data = atsApi.getActionService() //
         .createActionData(getClass().getSimpleName(), ButtonSDoesntWorkOnHelp_TeamWf.getName(),
            "Problem with the help") //
         .andAiAndToken(DemoArtifactToken.Reader_AI, ButtonSDoesntWorkOnHelp_TeamWf) //
         .andChangeType(ChangeTypes.Problem).andPriority("3") //
         .andValadation();
      NewActionData newData = atsApi.getActionService().createAction(data);
      if (dataErrored(newData)) {
         return;
      }

      IAtsTeamWorkflow teamWf = newData.getActResult().getAtsTeamWfs().iterator().next();
      Pair<IAtsTeamWorkflow, Result> result = transitionToWithPersist(teamWf, TeamState.Completed,
         teamWf.getAssignees().iterator().next(), teamWf.getAssignees(), atsApi);
      if (result.getSecond().isFalse()) {
         rd.errorf("Transition Failed: " + result.getSecond().getText());
         return;
      }
   }

}
