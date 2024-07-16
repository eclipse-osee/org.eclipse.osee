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

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.populate.Pdd52CreateWorkingWithDiagramTreeActions;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd52CreateWorkingWithDiagramTreeActionsTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd52CreateWorkingWithDiagramTreeActions create = new Pdd52CreateWorkingWithDiagramTreeActions();
      create.run();

      for (ArtifactToken version : create.getVersionToWorkflowToken().keySet()) {
         ArtifactToken teamWfArtToken = create.getVersionToWorkflowToken().get(version);

         IAtsTeamWorkflow teamWf = AtsApiService.get().getQueryService().getTeamWf(teamWfArtToken);
         Assert.assertNotNull(teamWf);

         testTeamContents(teamWf, teamWfArtToken.getName(), "3", version.getName(), getState(version).getName(),
            DemoArtifactToken.SAW_SW_Design_AI.getName(), getAssigneesStr(version), getArtifactType(),
            DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_SW_Design));

         DemoUtil.setPopulateDbSuccessful(true);
      }
   }

   private String getAssigneesStr(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? "" : DemoUsers.Kay_Jones.getName();
   }

   private ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.TeamWorkflow;
   }

   private TeamState getState(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? TeamState.Completed : TeamState.Implement;
   }

}
