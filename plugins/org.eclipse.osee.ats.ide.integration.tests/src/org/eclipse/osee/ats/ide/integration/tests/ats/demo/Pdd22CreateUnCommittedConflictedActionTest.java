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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkflowTitles;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd22CreateUnCommittedConflictedAction;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.core.enums.DemoUsers;
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
      IAtsTeamWorkflow teamWf =
         AtsApiService.get().getQueryService().getTeamWf(DemoArtifactToken.SAW_UnCommitedConflicted_Req_TeamWf);
      Assert.assertNotNull(teamWf);

      testTeamContents(teamWf, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements",
         DemoUsers.Joe_Smith.getName(), AtsDemoOseeTypes.DemoReqTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Requirements));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
