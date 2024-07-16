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
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd51CreateWorkaroundForGraphViewActions;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd51CreateWorkaroundForGraphViewActionsTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd51CreateWorkaroundForGraphViewActions create = new Pdd51CreateWorkaroundForGraphViewActions();
      create.run();

      for (ArtifactToken version : create.getVersionToWorkflowToken().keySet()) {
         ArtifactToken teamWfArtToken = create.getVersionToWorkflowToken().get(version);

         IAtsTeamWorkflow teamWf = AtsApiService.get().getQueryService().getTeamWf(teamWfArtToken);
         Assert.assertNotNull(teamWf);

         testTeamContents(teamWf, teamWfArtToken.getName(), "1", version.getName(), getState(version).getName(),
            DemoArtifactToken.Adapter_AI.getName(), getAssignees(version), AtsArtifactTypes.DemoReqTeamWorkflow,
            DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_HW));
      }

      DemoUtil.setPopulateDbSuccessful(true);
   }

   private String getAssignees(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? "" : DemoUsers.Jason_Michael.getName();
   }

   private TeamState getState(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? TeamState.Completed : TeamState.Implement;
   }

}
