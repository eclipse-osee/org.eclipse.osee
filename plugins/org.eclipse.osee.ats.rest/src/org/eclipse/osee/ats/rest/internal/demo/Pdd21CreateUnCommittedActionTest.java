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
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
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
public class Pdd21CreateUnCommittedActionTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd21CreateUnCommittedActionTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Collection<IAtsTeamWorkflow> sawUnCommittedTeamWfs = DemoUtil.getSawUnCommittedTeamWfs();
      assertEquals(4, sawUnCommittedTeamWfs.size());

      IAtsTeamWorkflow codeTeamArt = DemoUtil.getSawCodeUnCommittedWf();
      assertNotNull(codeTeamArt);
      IAtsTeamWorkflow testTeamArt = DemoUtil.getSawTestUnCommittedWf();
      assertNotNull(testTeamArt);
      IAtsTeamWorkflow reqTeamArt = DemoUtil.getSawReqUnCommittedWf();
      assertNotNull(reqTeamArt);
      IAtsTeamWorkflow designTeamArt = DemoUtil.getSawSWDesignUnCommittedWf();
      assertNotNull(designTeamArt);

      testTeamContents(codeTeamArt, DemoArtifactToken.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Code", DemoUsers.Joe_Smith.getName(),
         AtsArtifactTypes.DemoCodeTeamWorkflow, getTeamDef(DemoArtifactToken.SAW_Code));
      testTeamContents(testTeamArt, DemoArtifactToken.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Test", DemoUsers.Kay_Jones.getName(),
         AtsArtifactTypes.DemoTestTeamWorkflow, getTeamDef(DemoArtifactToken.SAW_Test));
      testTeamContents(reqTeamArt, DemoArtifactToken.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW Requirements", DemoUsers.Joe_Smith.getName(),
         AtsArtifactTypes.DemoReqTeamWorkflow, getTeamDef(DemoArtifactToken.SAW_Requirements));
      testTeamContents(designTeamArt, DemoArtifactToken.SAW_UNCOMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, "3",
         SAW_Bld_2.getName(), TeamState.Implement.getName(), "SAW SW Design", DemoUsers.Kay_Jones.getName(),
         AtsArtifactTypes.TeamWorkflow, getTeamDef(DemoArtifactToken.SAW_SW_Design));

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
