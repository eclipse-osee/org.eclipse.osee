/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.pr;

import java.util.Arrays;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDataMulti;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class ProblemReportTest {

   AtsApi atsApi;
   public static String TITLE = "New PR - CreateNewAmsProblemReportBlamTest";

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
   }

   @org.junit.Test
   public void testCreateWorkflows() {
      try {
         NewActionDataMulti datas = new NewActionDataMulti(getClass().getSimpleName(), atsApi.user());
         for (ArtifactToken prTok : Arrays.asList( //
            DemoArtifactToken.PrCommButtonShowsYellow, //
            DemoArtifactToken.PrWarning34InComm, //
            DemoArtifactToken.PrAseStatusNotFound, //
            DemoArtifactToken.PrFlightRecorderProblem, //
            DemoArtifactToken.PrSendAckFailure, //
            DemoArtifactToken.PrInitializationFailure,

            DemoArtifactToken.PrFixARadioSquawk_Completed, //
            DemoArtifactToken.PrSquatSwitchNotShowing_Completed, //

            DemoArtifactToken.PrMsgButtonLocation_Cancelled

         )) {
            NewActionData data = atsApi.getActionService() //
               .createActionData(getClass().getSimpleName(), prTok.getName(), prTok.getName()) //
               .andAiAndToken(DemoArtifactToken.SAW_PL_PR_AI, prTok.getToken()) //
               .andArtType(AtsArtifactTypes.DemoProblemReportTeamWorkflow) //
               .andChangeType(ChangeTypes.Problem).andPriority("3");

            datas.add(data);
         }
         NewActionDataMulti newData = atsApi.getActionService().createActions(datas);
         if (newData.getRd().isErrors()) {
            throw new OseeStateException(newData.getRd().toString());
         }

         IAtsChangeSet changes = atsApi.createChangeSet("Tansition " + getClass().getSimpleName());
         for (IAtsTeamWorkflow teamWf : newData.getAtsTeamWfs()) {
            String toState = null;
            if (teamWf.getName().endsWith(TeamState.Completed.getName())) {
               toState = TeamState.Closed.getName();
            } else if (teamWf.getName().endsWith(TeamState.Cancelled.getName())) {
               toState = TeamState.Cancelled.getName();
            } else {
               continue;
            }
            TransitionData transData = new TransitionData("Transition", Arrays.asList(teamWf), toState, null, null,
               changes, TransitionOption.OverrideAssigneeCheck, TransitionOption.OverrideTransitionValidityCheck,
               TransitionOption.OverrideIdeTransitionCheck);
            transData.setTransitionUser(atsApi.user());
            TransitionManager transitionMgr = new TransitionManager(transData);
            TransitionResults results = transitionMgr.handleAll();
            if (!results.isEmpty()) {
               throw new OseeStateException("Error transitioning: %s ", results.toString());
            }
         }
         TransactionToken execute = changes.execute();
         if (execute.isInvalid()) {
            throw new OseeStateException("Error transitioning: %s ", getClass().getSimpleName());
         }
      } catch (Exception ex) {
         throw new OseeStateException("Exception creating PR workflows: %s", Lib.exceptionToString(ex));
      }
   }

}
