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
import org.eclipse.osee.ats.client.demo.populate.Pdd82CreateCantLoadDiagramTreeAction;
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
public class Pdd82CreateCantLoadDiagramTreeActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd82CreateCantLoadDiagramTreeAction create = new Pdd82CreateCantLoadDiagramTreeAction();
      create.run();

      IAtsTeamWorkflow teamWf = AtsClientService.get().getQueryService().getTeamWf(DemoArtifactToken.CantLoadDiagramTree_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents(teamWf, DemoArtifactToken.CantLoadDiagramTree_TeamWf.getName(), "3", "",
         TeamState.Endorse.getName(), DemoArtifactToken.CIS_Test_AI.getName(), "Kay Jones",
         DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.CIS_Test));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
