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
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDataMulti;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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
         for (ArtifactToken prTok : Arrays.asList(DemoArtifactToken.PrCommButtonShowsYellow,
            DemoArtifactToken.PrFlightRecorderProblem, DemoArtifactToken.PrSendAckFailure,
            DemoArtifactToken.PrMsgButtonLocation, DemoArtifactToken.PrInitializationFailure,
            DemoArtifactToken.PrFixARadioSquawk, DemoArtifactToken.PrSquatSwitchNotShowing)) {
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
      } catch (Exception ex) {
         throw new OseeStateException("Exception creating PR workflows: %s", Lib.exceptionToString(ex));
      }
   }

}
