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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd84CreateProblemInTreeActionTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd84CreateProblemInTreeActionTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(DemoArtifactToken.ProblemInTree_TeamWf);
      assertNotNull(teamWf);

      testTeamContents(teamWf, DemoArtifactToken.ProblemInTree_TeamWf.getName(), "3", "", TeamState.Endorse.getName(),
         DemoArtifactToken.CIS_Test_AI.getName(), DemoUsers.Kay_Jones.getName(), AtsArtifactTypes.DemoTestTeamWorkflow,
         getTeamDef(DemoArtifactToken.CIS_Test));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
