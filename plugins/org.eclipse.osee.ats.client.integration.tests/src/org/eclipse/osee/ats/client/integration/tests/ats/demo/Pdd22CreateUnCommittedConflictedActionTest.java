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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.populate.Pdd22CreateUnCommittedConflictedAction;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.demo.api.DemoTeam;
import org.eclipse.osee.ats.demo.api.DemoWorkflowTitles;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd22CreateUnCommittedConflictedActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd22CreateUnCommittedConflictedAction create = new Pdd22CreateUnCommittedConflictedAction();
      create.run();

      String title = DemoWorkflowTitles.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW;
      IAtsTeamWorkflow teamWf = AtsClientService.get().getQueryService().getTeamWf(DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents(teamWf, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements",
         "Joe Smith", DemoArtifactTypes.DemoReqTeamWorkflow, DemoTestUtil.getTeamDef(DemoTeam.SAW_Requirements));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
