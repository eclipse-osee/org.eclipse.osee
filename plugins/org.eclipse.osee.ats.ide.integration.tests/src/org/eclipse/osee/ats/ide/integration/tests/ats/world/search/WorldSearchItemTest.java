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

package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore.AtsTestUtilState;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.search.WorldSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class WorldSearchItemTest {

   @Test
   public void testWorldSearchItem_PeerReviews() {
      AtsSearchData data = new AtsSearchData("Peer Reviews");
      data.setStateTypes(Arrays.asList(StateType.Working));
      data.setTeamDefIds(Arrays.asList(DemoArtifactToken.SAW_Code.getId()));
      data.setWorkItemTypes(Arrays.asList(WorkItemType.PeerReview));
      WorldSearchItem search = new WorldSearchItem(data);
      Collection<Artifact> results = search.performSearch(SearchType.Search);
      Assert.assertEquals(2, results.size());

      data.setStateTypes(Arrays.asList(StateType.Completed));
      search = new WorldSearchItem(data);
      results = search.performSearch(SearchType.Search);
      Assert.assertEquals(1, results.size());

      data.setStateTypes(Arrays.asList(StateType.Working, StateType.Completed));
      search = new WorldSearchItem(data);
      results = search.performSearch(SearchType.Search);
      Assert.assertEquals(3, results.size());
   }

   @Test
   public void testWorldSearchItem_AssigneesWorking() {
      AtsSearchData data = new AtsSearchData("Assignees");
      data.setUserId(DemoUsers.Joe_Smith.getUserId());
      data.setUserType(AtsSearchUserType.Assignee);
      data.setStateTypes(Arrays.asList(StateType.Working));
      data.setTeamDefIds(Arrays.asList(DemoArtifactToken.SAW_Code.getId()));
      WorldSearchItem search = new WorldSearchItem(data);
      Collection<Artifact> results = search.performSearch(SearchType.Search);
      Assert.assertEquals(6, results.size());
   }

   @Test
   public void testWorldSearchItem_AssigneesCompletedCancelled() {

      // No completed workflows to begin with
      AtsSearchData data = new AtsSearchData("Assignees");
      data.setUserId(DemoUsers.Joe_Smith.getUserId());
      data.setUserType(AtsSearchUserType.Assignee);
      data.setStateTypes(Arrays.asList(StateType.Completed));
      data.setTeamDefIds(Arrays.asList(DemoArtifactToken.SAW_Code.getId()));
      WorldSearchItem search = new WorldSearchItem(data);
      Collection<Artifact> results = search.performSearch(SearchType.Search);
      Assert.assertEquals(0, results.size());

      // Add a completed workflow, test that it comes back
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName() + " - AssigneesCompleted");
      AtsTestUtil.transitionTo(AtsTestUtilState.Completed, AtsApiService.get().getUserService().getCurrentUser());

      data.setTeamDefIds(Arrays.asList(AtsTestUtil.getTestTeamDef().getId()));
      search = new WorldSearchItem(data);
      results = search.performSearch(SearchType.Search);
      Assert.assertEquals(1, results.size());

      // Add a cancelled workflow, test that it comes back
      IAtsTeamWorkflow teamWf2 = AtsTestUtil.getTeamWf2();
      AtsTestUtil.transitionTo(teamWf2, AtsTestUtilState.Cancelled,
         AtsApiService.get().getUserService().getCurrentUser());
      data.setStateTypes(Arrays.asList(StateType.Cancelled));
      search = new WorldSearchItem(data);
      results = search.performSearch(SearchType.Search);
      Assert.assertEquals(1, results.size());

      // Test that completed/cancelled returns 2
      data.setStateTypes(Arrays.asList(StateType.Cancelled, StateType.Completed));
      search = new WorldSearchItem(data);
      results = search.performSearch(SearchType.Search);
      Assert.assertEquals(2, results.size());

      AtsTestUtil.cleanup();
   }

}
