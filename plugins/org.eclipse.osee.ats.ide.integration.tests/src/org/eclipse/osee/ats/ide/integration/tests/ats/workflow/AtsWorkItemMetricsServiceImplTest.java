/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemMetricsService;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for AtsWorkItemMetricsServiceImpl
 *
 * @author Donald G. Dunne
 */
public class AtsWorkItemMetricsServiceImplTest {

   @Test
   public void testHoursSpent() {
      AtsApi atsApi = AtsApiService.get();
      IAtsWorkItemMetricsService metricsService = atsApi.getWorkItemMetricsService();
      IAtsPeerToPeerReview rev =
         (IAtsPeerToPeerReview) atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.PeerToPeerReview,
            DemoArtifactToken.PeerReview2.getName());
      Assert.assertNotNull(rev);

      // Hours set during review creation
      double hoursSpentWorkflow = metricsService.getHoursSpentWorkflow(rev);
      Assert.assertEquals(0.4, hoursSpentWorkflow, 0.00);
      double hoursSpentRoles = metricsService.getHoursSpentRoles(rev);
      Assert.assertEquals(2.0, hoursSpentRoles, 0.00);

      // Add 3 workflow hours
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      AtsUser currentUser = atsApi.getUserService().getCurrentUser();
      IStateToken currentState = rev.getCurrentState();
      metricsService.updateMetrics(rev, currentState, 3, 43, false, currentUser, changes);
      changes.execute();

      hoursSpentWorkflow = metricsService.getHoursSpentWorkflow(rev);
      Assert.assertEquals(3.4, hoursSpentWorkflow, 0.000);
      hoursSpentRoles = metricsService.getHoursSpentRoles(rev);
      Assert.assertEquals(2.0, hoursSpentRoles, 0.00);

      // Add 2.2 role hours
      changes = atsApi.createChangeSet(getClass().getSimpleName());
      IAtsPeerReviewRoleManager roleManager = rev.getRoleManager();
      UserRole myRole = roleManager.getUserRoles(ReviewRole.Author).iterator().next();
      myRole.addHoursSpent(2.2);
      roleManager.addOrUpdateUserRole(myRole, changes);
      roleManager.saveToArtifact(changes);
      changes.execute();

      hoursSpentWorkflow = metricsService.getHoursSpentWorkflow(rev);
      Assert.assertEquals(3.4, hoursSpentWorkflow, 0.000);
      hoursSpentRoles = metricsService.getHoursSpentRoles(rev);
      Assert.assertEquals(4.2, hoursSpentRoles, 0.000);

      double hoursSpentTotal = metricsService.getHoursSpentTotal(rev);
      Assert.assertEquals(7.6, hoursSpentTotal, 0.000);

   }

}
