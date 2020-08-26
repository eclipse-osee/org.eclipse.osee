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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review;

import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.review.GenerateReviewParticipationReport;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link GenerateReviewParticipationReport}
 *
 * @author Donald G. Dunne
 */
public class GenerateReviewParticipationReportTest {

   @Test
   public void test() {
      GenerateReviewParticipationReport report = new GenerateReviewParticipationReport(null);
      report.setSelectedUser(AtsApiService.get().getUserService().getUserByToken(DemoUsers.Joe_Smith));
      Set<Artifact> results = report.getResults();
      Assert.assertEquals(6, results.size());
      int decRevCount = 0, peerRevCount = 0;
      for (Artifact review : results) {
         if (review.isOfType(AtsArtifactTypes.DecisionReview)) {
            decRevCount++;
         }
         if (review.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
            peerRevCount++;
         }
      }
      Assert.assertEquals(3, decRevCount);
      Assert.assertEquals(3, peerRevCount);
   }

}
