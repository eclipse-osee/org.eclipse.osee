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
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.populate.Pdd20CreateCommittedAction;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.ats.demo.api.DemoWorkflowTitles;
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
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Code", "Joe Smith",
         DemoArtifactTypes.DemoCodeTeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Code));
      testTeamContents(testTeamArt, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Test", "Kay Jones",
         DemoArtifactTypes.DemoTestTeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Test));
      testTeamContents(reqTeamArt, DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements", "Joe Smith",
         DemoArtifactTypes.DemoReqTeamWorkflow, DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_Requirements));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
