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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowTest {

   @Test
   public void testGetActionableItems() {
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      Set<IAtsActionableItem> ais = teamWf.getActionableItems();
      Assert.assertEquals(1, ais.size());
      Assert.assertEquals(DemoArtifactToken.SAW_Code_AI, ais.iterator().next());
   }

   @Test
   public void testGetTeamDefinition() {
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
      Assert.assertNotNull(teamDef);
      Assert.assertEquals(DemoArtifactToken.SAW_Code, teamDef);
   }

}
