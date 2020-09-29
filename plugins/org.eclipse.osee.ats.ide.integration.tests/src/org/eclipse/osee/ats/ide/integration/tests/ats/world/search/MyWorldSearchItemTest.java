/*********************************************************************
 * Copyright (c) 2015 Boeing
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

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.world.search.MyWorldSearchItem;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Test;

/**
 * Test case for {@link MyWorldSearchItem}
 *
 * @author Donald G. Dunne
 */
public class MyWorldSearchItemTest {

   @Test
   public void testMyWorldSearchItem() {
      AtsUser currentUser = AtsApiService.get().getUserService().getCurrentUser();
      MyWorldSearchItem search = new MyWorldSearchItem("Search", currentUser);
      Collection<Artifact> results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 14, IAtsWorkItem.class);
      DemoTestUtil.assertTypes(results, 10, IAtsTeamWorkflow.class);
      DemoTestUtil.assertTypes(results, 2, IAtsDecisionReview.class);
      DemoTestUtil.assertTypes(results, 2, IAtsPeerToPeerReview.class);

      search = new MyWorldSearchItem("Search",
         AtsApiService.get().getUserService().getUserByUserId(DemoUsers.Kay_Jones.getUserId()));
      results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 14, IAtsWorkItem.class);
      DemoTestUtil.assertTypes(results, 1, IAtsPeerToPeerReview.class);
   }

}
