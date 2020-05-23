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

package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.search.LegacyPcrIdQuickSearch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link LegacyPcrIdQuickSearch}
 *
 * @author Donald G. Dunne
 */
public class LegacyPcrIdQuickSearchTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testPerformSearch() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName() + ".testPerformSearch");
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      teamWf.persist(getClass().getSimpleName());

      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();
      teamWf2.persist(getClass().getSimpleName());

      LegacyPcrIdQuickSearch srch = new LegacyPcrIdQuickSearch(Arrays.asList("67676"));
      Assert.assertTrue("No results should be found", srch.performSearch().isEmpty());

      teamWf.setSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "67676");
      teamWf.persist(getClass().getSimpleName());

      srch = new LegacyPcrIdQuickSearch(Arrays.asList("67676"));
      Assert.assertEquals("Should return teamWf", teamWf, srch.performSearch().iterator().next());

      teamWf2.setSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "32323");
      teamWf2.persist(getClass().getSimpleName());

      srch = new LegacyPcrIdQuickSearch(Arrays.asList("67676", "32323"));
      Collection<Artifact> results = srch.performSearch();
      Assert.assertTrue(results.contains(teamWf));
      Assert.assertTrue(results.contains(teamWf2));

      teamWf.setSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "RPCR_67676");
      teamWf.persist(getClass().getSimpleName());

      // As single string, neither should be found if exactMatch == true
      srch = new LegacyPcrIdQuickSearch(Arrays.asList("RPCR 67676"));
      results = srch.performSearch(true);
      Assert.assertTrue("No results should be found", results.isEmpty());

      // As single string, both should be found if exactMatch == false
      srch = new LegacyPcrIdQuickSearch(Arrays.asList("RPCR 67676"));
      results = srch.performSearch(false);
      Assert.assertTrue(results.contains(teamWf));

   }

}
