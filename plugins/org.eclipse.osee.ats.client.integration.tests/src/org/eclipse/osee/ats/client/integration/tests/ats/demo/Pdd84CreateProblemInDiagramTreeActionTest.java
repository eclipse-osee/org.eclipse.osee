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
package org.eclipse.osee.ats.client.integration.tests.ats.demo;

import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.populate.Pdd84CreateProblemInDiagramTreeAction;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.demo.api.DemoTeam;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd84CreateProblemInDiagramTreeActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd84CreateProblemInDiagramTreeAction create = new Pdd84CreateProblemInDiagramTreeAction();
      create.run();

      IAtsTeamWorkflow teamWf = AtsClientService.get().getTeamWf(DemoArtifactToken.ProblemInDiagramTree_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents(teamWf, DemoArtifactToken.ProblemInDiagramTree_TeamWf.getName(), "3", "",
         TeamState.Endorse.getName(), DemoArtifactToken.CIS_Test_AI.getName(), "Kay Jones",
         DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.CIS_Test));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
