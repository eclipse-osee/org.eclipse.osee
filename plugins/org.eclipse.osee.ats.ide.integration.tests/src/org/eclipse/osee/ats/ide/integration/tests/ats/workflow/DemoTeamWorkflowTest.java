/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Unit for {@link DemoTeamWorkflow}
 *
 * @author Donald G. Dunne
 */
public class DemoTeamWorkflowTest {

   public static Artifact actionArt;

   @Before
   @After
   public void cleanup() throws Exception {
      assertTrue("This can not be run on production databse.", !AtsApiService.get().getStoreService().isProductionDb());

      AtsTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

   @Test
   public void testCreateSawTestWf() throws Exception {
      Collection<IAtsActionableItem> aias = new HashSet<>();
      aias.add(AtsApiService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Test_AI));
      String title = getClass().getSimpleName() + " testCreateSawTestWf";

      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Create SAW Test Action title: " + title);

      ActionResult result =
         AtsApiService.get().getActionService().createAction(null, title, title, ChangeTypes.Improvement, "1", false,
            null, aias, new Date(), AtsApiService.get().getUserService().getCurrentUser(), null, changes);
      changes.execute();

      TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) result.getFirstTeam().getStoreObject();

      //*** Transition Action to Analyze
      TransitionData transData = new TransitionData("Transition to Analyze", Arrays.asList(teamWf),
         TeamState.Analyze.getName(), Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()), null, null,
         TransitionOption.OverrideAssigneeCheck);
      TransitionResults results = AtsApiService.get().getWorkItemService().transition(transData);
      assertTrue("Transition Error - " + results.toString(), results.isEmpty());

      //*** Transition Action to Implement
      transData = new TransitionData("Transition to Implement", Arrays.asList(teamWf), TeamState.Implement.getName(),
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()), null, null, TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideTransitionValidityCheck);
      results = AtsApiService.get().getWorkItemService().transition(transData);
      assertTrue("Transition Error - " + results.toString(), results.isEmpty());

      IAtsVersion sawBuild2Version =
         AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      assertNotNull(sawBuild2Version);
      AtsApiService.get().getVersionService().setTargetedVersion(teamWf, sawBuild2Version, changes);
      changes.execute();

      //*** Create a new workflow branch
      Job createBranchJob = AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(teamWf, true);
      createBranchJob.join();

      // verify working branch has title in it
      String name = teamWf.getWorkingBranchForceCacheUpdate().getName();
      assertTrue(String.format("branch name [%s] expected title [%s]", name, title), name.contains(title));

      // Verify that the working branch has the pacr number in it
      assertTrue(String.format("branch name [%s] expected SAW Test in title", name), name.contains("SAW Test"));
   }
}
