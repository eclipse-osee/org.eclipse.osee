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

import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.CantSeeTheGraphView_TeamWf;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd83CreateCantSeeTheGraphViewAction extends AbstractPopulateDemoDatabase {

   public Pdd83CreateCantSeeTheGraphViewAction(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      NewActionData data = atsApi.getActionService() //
         .createActionData(getClass().getSimpleName(), CantSeeTheGraphView_TeamWf.getName(),
            "Problem with the Graph View") //
         .andAiAndToken(DemoArtifactToken.Adapter_AI, CantSeeTheGraphView_TeamWf) //
         .andChangeType(ChangeTypes.Problem).andPriority("1");
      NewActionData newData = atsApi.getActionService().createAction(data);
      if (dataErrored(newData)) {
         return;
      }

      IAtsTeamWorkflow teamWf = newData.getActResult().getAtsTeamWfs().iterator().next();
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      transitionTo(teamWf, TeamState.Implement, changes);
      changes.execute();

   }

}
