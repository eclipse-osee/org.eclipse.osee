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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.TeamWorkflow;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowTest {

   @Test
   public void testGetActionableItems() {
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      Set<IAtsActionableItem> ais = teamWf.getActionableItems();
      Assert.assertEquals(1, ais.size());
      Assert.assertEquals(DemoArtifactToken.SAW_Code_AI, ais.iterator().next());

      TeamWorkflow tWf =
         new TeamWorkflow(AtsClientService.get().getLogger(), AtsClientService.get().getServices(), teamWf);
      Set<IAtsActionableItem> ais1 = tWf.getActionableItems();
      Assert.assertEquals(1, ais1.size());
      Assert.assertEquals(DemoArtifactToken.SAW_Code_AI, ais1.iterator().next());
   }

   @Test
   public void testGetTeamDefinition() {
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
      Assert.assertNotNull(teamDef);
      Assert.assertEquals(DemoArtifactToken.SAW_Code, teamDef);

      TeamWorkflow tWf =
         new TeamWorkflow(AtsClientService.get().getLogger(), AtsClientService.get().getServices(), teamWf);
      IAtsTeamDefinition teamDef1 = tWf.getTeamDefinition();
      Assert.assertNotNull(teamDef1);
      Assert.assertEquals(DemoArtifactToken.SAW_Code, teamDef1);
   }

}
