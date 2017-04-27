/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.world.search.MyWorldSearchItem;
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
      IAtsUser currentUser = AtsClientService.get().getUserService().getCurrentUser();
      MyWorldSearchItem search = new MyWorldSearchItem("Search", currentUser);
      Collection<Artifact> results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 11, IAtsWorkItem.class);
      DemoTestUtil.assertTypes(results, 7, IAtsTeamWorkflow.class);
      DemoTestUtil.assertTypes(results, 2, IAtsDecisionReview.class);
      DemoTestUtil.assertTypes(results, 2, IAtsPeerToPeerReview.class);

      search = new MyWorldSearchItem("Search",
         AtsClientService.get().getUserService().getUserById(DemoUsers.Kay_Jones.getUserId()));
      results = search.performSearchGetResults();
      DemoTestUtil.assertTypes(results, 12, IAtsWorkItem.class);
      DemoTestUtil.assertTypes(results, 1, IAtsPeerToPeerReview.class);
   }

}
