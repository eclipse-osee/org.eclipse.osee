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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkflowTitles;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd23CreateNoBranchAction;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd23CreateNoBranchActionTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd23CreateNoBranchAction create = new Pdd23CreateNoBranchAction();
      create.run();

      String title = DemoWorkflowTitles.SAW_NO_BRANCH_REQT_CHANGES_FOR_DIAGRAM_VIEW;

      TeamWorkFlowArtifact codeTeamArt = DemoUtil.getSawCodeNoBranchWf();
      Assert.assertNotNull(codeTeamArt);
      TeamWorkFlowArtifact testTeamArt = DemoUtil.getSawTestNoBranchWf();
      Assert.assertNotNull(testTeamArt);
      TeamWorkFlowArtifact reqTeamArt = DemoUtil.getSawReqNoBranchWf();
      Assert.assertNotNull(reqTeamArt);
      TeamWorkFlowArtifact designTeamArt = DemoUtil.getSawSWDesignNoBranchWf();
      Assert.assertNotNull(designTeamArt);

      testTeamContents(codeTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Code",
         DemoUsers.Joe_Smith.getName(), AtsDemoOseeTypes.DemoCodeTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Code));
      testTeamContents(testTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Test",
         DemoUsers.Kay_Jones.getName(), AtsDemoOseeTypes.DemoTestTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Test));
      testTeamContents(reqTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements",
         DemoUsers.Joe_Smith.getName(), AtsDemoOseeTypes.DemoReqTeamWorkflow,
         DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Requirements));
      testTeamContents(designTeamArt, title, "3", SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW SW Design",
         DemoUsers.Kay_Jones.getName(), AtsArtifactTypes.TeamWorkflow,
         DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_SW_Design));

      // test sw_design 1 peer and 1 decision review
      testSwDesign1PeerAnd1DecisionReview(designTeamArt);

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
