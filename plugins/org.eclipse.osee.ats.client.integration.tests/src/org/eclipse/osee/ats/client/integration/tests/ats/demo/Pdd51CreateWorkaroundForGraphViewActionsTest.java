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
import org.eclipse.osee.ats.client.demo.populate.Pdd51CreateWorkaroundForGraphViewActions;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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

         IAtsTeamWorkflow teamWf = AtsClientService.get().getQueryService().getTeamWf(teamWfArtToken);
         Assert.assertNotNull(teamWf);

         testTeamContents(teamWf, teamWfArtToken.getName(), "1", version.getName(), getState(version).getName(),
            DemoArtifactToken.Adapter_AI.getName(), getAssignees(version), DemoArtifactTypes.DemoReqTeamWorkflow,
            DemoTestUtil.getTeamDef(DemoArtifactToken.SAW_HW));
      }

      DemoUtil.setPopulateDbSuccessful(true);
   }

   private String getAssignees(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? "" : "Jason Michael";
   }

   private TeamState getState(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? TeamState.Completed : TeamState.Implement;
   }

}
