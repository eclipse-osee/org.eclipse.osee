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
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkflowTitles;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd20CreateCommittedAction;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd20CreateCommittedActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd20CreateCommittedAction create = new Pdd20CreateCommittedAction();
      create.run();

      Assert.assertEquals(3, DemoUtil.getSawCommittedTeamWfs().size());

      TeamWorkFlowArtifact codeTeamArt = DemoUtil.getSawCodeCommittedWf();
      Assert.assertNotNull(codeTeamArt);
      TeamWorkFlowArtifact testTeamArt = DemoUtil.getSawTestCommittedWf();
      Assert.assertNotNull(testTeamArt);
      TeamWorkFlowArtifact reqTeamArt = DemoUtil.getSawReqCommittedWf();
      Assert.assertNotNull(reqTeamArt);

      testTeamContents(codeTeamArt, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Code", DemoUsers.Joe_Smith.getName(),
         AtsArtifactTypes.DemoCodeTeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Code));
      Assert.assertTrue(codeTeamArt.getWorkTypes().contains(WorkType.Code));
      Assert.assertTrue(codeTeamArt.isWorkType(WorkType.Code));

      testTeamContents(testTeamArt, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Test", DemoUsers.Kay_Jones.getName(),
         AtsArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Test));
      Assert.assertTrue(testTeamArt.getWorkTypes().contains(WorkType.Test));
      Assert.assertTrue(testTeamArt.isWorkType(WorkType.Test));

      testTeamContents(reqTeamArt, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements", DemoUsers.Joe_Smith.getName(),
         AtsArtifactTypes.DemoReqTeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Requirements));
      Assert.assertTrue(reqTeamArt.getWorkTypes().contains(WorkType.Requirements));
      Assert.assertTrue(reqTeamArt.isWorkType(WorkType.Requirements));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
