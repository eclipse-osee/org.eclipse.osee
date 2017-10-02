/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.world.search;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.search.TeamDefinitionQuickSearch;
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
   public void testPerformSearch()  {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName() + ".testPerformSearch");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      teamWf.persist(getClass().getSimpleName());

      IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet(getClass().getSimpleName(),
         AtsClientService.get().getUserService().getCurrentUser());
      IAtsTeamDefinition randomTeamDef =
         AtsClientService.get().createTeamDefinition(getClass().getSimpleName(), changes, AtsClientService.get());
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
