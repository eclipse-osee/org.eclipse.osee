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
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd80CreateButtonSDoesntWorkAction;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd80CreateButtonSDoesntWorkActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd80CreateButtonSDoesntWorkAction create = new Pdd80CreateButtonSDoesntWorkAction();
      create.run();

      IAtsTeamWorkflow teamWf =
         AtsApiService.get().getQueryService().getTeamWf(DemoArtifactToken.ButtonSDoesntWorkOnHelp_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents(teamWf, DemoArtifactToken.ButtonSDoesntWorkOnHelp_TeamWf.getName(), "3", "",
         TeamState.Completed.getName(), DemoArtifactToken.Reader_AI.getName(), "", AtsArtifactTypes.TeamWorkflow,
         DemoTestUtil.getTeamDef(DemoArtifactToken.Tools_Team));

      DemoUtil.setPopulateDbSuccessful(true);
   }
}
