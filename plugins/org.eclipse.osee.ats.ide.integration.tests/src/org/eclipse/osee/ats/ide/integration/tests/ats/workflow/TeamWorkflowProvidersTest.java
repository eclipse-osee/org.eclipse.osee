/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.List;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link TeamWorkflowProviders}
 *
 * @author Donald G. Dunne
 */
public class TeamWorkflowProvidersTest {

   public static TeamWorkFlowArtifact teamArt;

   @Test
   public void test() throws Exception {
      AtsApiService.get().getTeamWorkflowProviders();
      List<ITeamWorkflowProvider> providers = TeamWorkflowProviders.getTeamWorkflowProviders();
      Assert.assertEquals(1, providers.size());

      ITeamWorkflowProvider provider = providers.iterator().next();
      TeamWorkFlowArtifact sawCodeCommittedWf = (TeamWorkFlowArtifact) DemoUtil.getSawCodeCommittedWf();
      Assert.assertTrue(provider.isResponsibleFor(sawCodeCommittedWf));

      TeamWorkFlowArtifact swDesignNoBranchWf = (TeamWorkFlowArtifact) DemoUtil.getSwDesignNoBranchWf();
      Assert.assertFalse(provider.isResponsibleFor(swDesignNoBranchWf));
   }

}
