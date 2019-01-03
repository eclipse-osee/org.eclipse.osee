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
package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd86CreateProblemWithTheUserWindowAction;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd86CreateProblemWithTheUserWindowActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd86CreateProblemWithTheUserWindowAction create = new Pdd86CreateProblemWithTheUserWindowAction();
      create.run();

      IAtsTeamWorkflow teamWf =
         AtsClientService.get().getQueryService().getTeamWf(DemoArtifactToken.ProblemWithTheUserWindow_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents(teamWf, DemoArtifactToken.ProblemWithTheUserWindow_TeamWf.getName(), "4", "",
         TeamState.Implement.getName(), DemoArtifactToken.Timesheet_AI.getName(), "Jeffery Kay",
         AtsArtifactTypes.TeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.Tools_Team));

      DemoUtil.setPopulateDbSuccessful(true);
   }
}