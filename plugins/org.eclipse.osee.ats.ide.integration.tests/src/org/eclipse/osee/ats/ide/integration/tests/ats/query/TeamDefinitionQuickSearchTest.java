/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.search.TeamDefinitionQuickSearch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link TeamDefinitionQuickSearch}
 *
 * @author Donald G. Dunne
 */
public class TeamDefinitionQuickSearchTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
      AtsTestUtil.cleanupSimpleTest(TeamDefinitionQuickSearchTest.class.getSimpleName());
   }

   @Test
   public void testPerformSearch() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName() + ".testPerformSearch");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      teamWf.persist(getClass().getSimpleName());

      IAtsChangeSet changes = AtsApiService.get().getStoreService().createAtsChangeSet(getClass().getSimpleName(),
         AtsApiService.get().getUserService().getCurrentUser());
      IAtsTeamDefinition randomTeamDef =
         AtsApiService.get().getTeamDefinitionService().createTeamDefinition(getClass().getSimpleName(), changes);
      changes.execute();

      TeamDefinitionQuickSearch srch = new TeamDefinitionQuickSearch(Arrays.asList(randomTeamDef));
      Assert.assertTrue("No results should be found", srch.performSearch().isEmpty());

      IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();

      srch = new TeamDefinitionQuickSearch(Arrays.asList(teamDef));
      Assert.assertEquals("Should return teamWf", teamWf, srch.performSearch().iterator().next());

      srch = new TeamDefinitionQuickSearch(Arrays.asList(teamDef, randomTeamDef));
      Assert.assertEquals("Should return teamWf", teamWf, srch.performSearch().iterator().next());

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      teamWf2.persist(getClass().getSimpleName());

      srch = new TeamDefinitionQuickSearch(Arrays.asList(teamDef, randomTeamDef));
      Collection<Artifact> results = srch.performSearch();
      Assert.assertTrue(results.contains(teamWf));
      Assert.assertTrue(results.contains(teamWf2));
   }

}
