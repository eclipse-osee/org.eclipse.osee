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

package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.populate.Pdd81CreateButtonWDoesntWorkAction;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd81CreateButtonWDoesntWorkActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd81CreateButtonWDoesntWorkAction create = new Pdd81CreateButtonWDoesntWorkAction();
      create.run();

      IAtsTeamWorkflow teamWf =
         AtsApiService.get().getQueryService().getTeamWf(DemoArtifactToken.ButtonWDoesntWorkOnSituationPage_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents(teamWf, DemoArtifactToken.ButtonWDoesntWorkOnSituationPage_TeamWf.getName(), "3", "",
         TeamState.Analyze.getName(), DemoArtifactToken.CIS_Test_AI.getName(), DemoUsers.Kay_Jones.getName(),
         AtsArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.CIS_Test));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
