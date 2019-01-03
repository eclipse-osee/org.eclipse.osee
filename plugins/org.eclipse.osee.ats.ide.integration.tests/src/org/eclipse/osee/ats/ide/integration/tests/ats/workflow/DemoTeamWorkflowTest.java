/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.ide.branch.AtsBranchUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
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
      assertTrue("This can not be run on production databse.",
         !AtsClientService.get().getStoreService().isProductionDb());

      AtsTestUtil.cleanupSimpleTest(getClass().getSimpleName());
   }

   @Test
   public void testCreateSawTestWf() throws Exception {
      Collection<IAtsActionableItem> aias = new HashSet<>();
      aias.add(AtsClientService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Test_AI));
      String title = getClass().getSimpleName() + " testCreateSawTestWf";

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Create SAW Test Action title: " + title);

      ActionResult result =
         AtsClientService.get().getActionFactory().createAction(null, title, title, ChangeType.Improvement, "1", false,
            null, aias, new Date(), AtsClientService.get().getUserService().getCurrentUser(), null, changes);

      TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) result.getFirstTeam().getStoreObject();

      //*** Transition Action to Analyze
      TransitionHelper helper = new TransitionHelper("Transition to Analyze", Arrays.asList(teamWf),
         TeamState.Analyze.getName(), Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null,
         changes, AtsClientService.get().getServices(), TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      assertTrue("Transition Error - " + results.toString(), results.isEmpty());

      //*** Transition Action to Implement
      helper = new TransitionHelper("Transition to Implement", Arrays.asList(teamWf), TeamState.Implement.getName(),
         Arrays.asList(AtsClientService.get().getUserService().getCurrentUser()), null, changes,
         AtsClientService.get().getServices(), TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideTransitionValidityCheck);
      transitionMgr = TransitionFactory.getTransitionManager(helper);
      results = transitionMgr.handleAllAndPersist();
      assertTrue("Transition Error - " + results.toString(), results.isEmpty());

      IAtsVersion sawBuild2Version = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      assertNotNull(sawBuild2Version);
      AtsClientService.get().getVersionService().setTargetedVersion(teamWf, sawBuild2Version, changes);
      changes.execute();

      //*** Create a new workflow branch
      Job createBranchJob = AtsBranchUtil.createWorkingBranch_Create(teamWf, true);
      createBranchJob.join();

      // verify working branch has title in it
      String name = teamWf.getWorkingBranchForceCacheUpdate().getName();
      assertTrue(String.format("branch name [%s] expected title [%s]", name, title), name.contains(title));

      // Verify that the working branch has the pacr number in it
      assertTrue(String.format("branch name [%s] expected SAW Test in title", name), name.contains("SAW Test"));
   }
}
