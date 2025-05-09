/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.demo.workflow.pr;

import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.config.tx.IAtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class BuildImpactDataSampleDemo {

   public BuildImpactDataSampleDemo() {
   }

   public static BuildImpactDatas get() {

      long SENTINEL = -1L;
      BuildImpactDatas bids = new BuildImpactDatas();

      BuildImpactData bid = new BuildImpactData();
      bid.setBids(bids);
      bid.setBidArt(ArtifactToken.valueOf(ArtifactId.SENTINEL, DemoArtifactToken.SAW_PL_SBVT1.getName()));
      bid.setBuild(DemoArtifactToken.SAW_PL_SBVT1);
      bid.setProgram(DemoArtifactToken.SAW_PL_Program);
      bid.setState(BuildImpactState.InWork.getName());
      createTeamWf(bid, "Team Wf 1", SENTINEL, "Endorse", StateType.Working, DemoArtifactToken.SAW_PL_Test_AI);
      createTeamWf(bid, "Team Wf 2", SENTINEL, "Completed", StateType.Completed, DemoArtifactToken.SAW_PL_Code_AI);
      createTeamWf(bid, "Team Wf 3", SENTINEL, "Cancelled", StateType.Completed, DemoArtifactToken.SAW_PL_Test_AI);
      bids.addBuildImpactData(bid);

      bid = new BuildImpactData();
      bid.setBids(bids);
      bid.setBidArt(ArtifactToken.valueOf(ArtifactId.SENTINEL, DemoArtifactToken.SAW_PL_SBVT2.getName()));
      bid.setBuild(DemoArtifactToken.SAW_PL_SBVT2);
      bid.setProgram(DemoArtifactToken.SAW_PL_Program);
      bid.setState(BuildImpactState.InWork.getName());
      createTeamWf(bid, "Team Wf A", SENTINEL, "Endorse", StateType.Working, DemoArtifactToken.SAW_PL_Requirements_AI);
      createTeamWf(bid, "Team Wf B", SENTINEL, "Analyze", StateType.Working, DemoArtifactToken.SAW_PL_Code_AI);
      createTeamWf(bid, "Team Wf C", SENTINEL, "Implement", StateType.Working, DemoArtifactToken.SAW_PL_Test_AI);
      bids.addBuildImpactData(bid);

      bid = new BuildImpactData();
      bid.setBids(bids);
      bid.setBuild(DemoArtifactToken.SAW_PL_SBVT3);
      bid.setBidArt(ArtifactToken.valueOf(ArtifactId.SENTINEL, DemoArtifactToken.SAW_PL_SBVT3.getName()));
      bid.setProgram(DemoArtifactToken.SAW_PL_Program);
      bid.setState(BuildImpactState.Open.getName());
      bids.addBuildImpactData(bid);

      return bids;

   }

   private static void createTeamWf(BuildImpactData bid, String name, long id, String string, //
      StateType stateType, IAtsActionableItemArtifactToken ai) {
      JaxTeamWorkflow teamWf = new JaxTeamWorkflow();
      teamWf.setAtsApi(AtsApiService.get());
      teamWf.setName(name);
      teamWf.setId(id);
      teamWf.setCurrentState(string);
      teamWf.setStateType(stateType);
      teamWf.setNewAi(ai);
      bid.addTeamWorkflow(teamWf);
   }
}
