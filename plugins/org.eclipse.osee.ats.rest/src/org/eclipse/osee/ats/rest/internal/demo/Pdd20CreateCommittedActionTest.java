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

package org.eclipse.osee.ats.rest.internal.demo;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd20CreateCommittedActionTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd20CreateCommittedActionTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      assertEquals(3, DemoUtil.getSawCommittedTeamWfs().size());

      IAtsTeamWorkflow codeTeamArt = DemoUtil.getSawCodeCommittedWf();
      assertNotNull(codeTeamArt);
      IAtsTeamWorkflow testTeamArt = DemoUtil.getSawTestCommittedWf();
      assertNotNull(testTeamArt);
      IAtsTeamWorkflow reqTeamArt = DemoUtil.getSawReqCommittedWf();
      assertNotNull(reqTeamArt);

      testTeamContents(codeTeamArt, DemoArtifactToken.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Code", DemoUsers.Joe_Smith.getName(),
         AtsArtifactTypes.DemoCodeTeamWorkflow, getTeamDef(DemoArtifactToken.SAW_Code));
      assertTrue(codeTeamArt.getWorkTypes().contains(WorkType.Code));
      assertTrue(codeTeamArt.isWorkType(WorkType.Code));

      testTeamContents(testTeamArt, DemoArtifactToken.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Test", DemoUsers.Kay_Jones.getName(),
         AtsArtifactTypes.DemoTestTeamWorkflow, getTeamDef(DemoArtifactToken.SAW_Test));
      assertTrue(testTeamArt.getWorkTypes().contains(WorkType.Test));
      assertTrue(testTeamArt.isWorkType(WorkType.Test));

      testTeamContents(reqTeamArt, DemoArtifactToken.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "1",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements", DemoUsers.Joe_Smith.getName(),
         AtsArtifactTypes.DemoReqTeamWorkflow, getTeamDef(DemoArtifactToken.SAW_Requirements));
      assertTrue(reqTeamArt.getWorkTypes().contains(WorkType.Requirements));
      assertTrue(reqTeamArt.isWorkType(WorkType.Requirements));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
