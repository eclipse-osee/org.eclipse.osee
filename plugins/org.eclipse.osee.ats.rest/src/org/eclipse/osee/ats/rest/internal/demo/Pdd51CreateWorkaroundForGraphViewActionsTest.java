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

import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd51CreateWorkaroundForGraphViewActionsTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd51CreateWorkaroundForGraphViewActionsTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      for (Entry<ArtifactToken, ArtifactToken> verToWf : Pdd51CreateWorkaroundForGraphViewActions.versionToWorkflowToken.entrySet()) {
         ArtifactToken version = verToWf.getKey();
         ArtifactToken teamWfArtToken = verToWf.getValue();
         IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(teamWfArtToken);

         assertNotNull(teamWf);

         testTeamContents(teamWf, teamWfArtToken.getName(), "1", version.getName(), getState(version).getName(),
            DemoArtifactToken.Adapter_AI.getName(), getAssignees(version), AtsArtifactTypes.DemoReqTeamWorkflow,
            getTeamDef(DemoArtifactToken.SAW_HW));
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
