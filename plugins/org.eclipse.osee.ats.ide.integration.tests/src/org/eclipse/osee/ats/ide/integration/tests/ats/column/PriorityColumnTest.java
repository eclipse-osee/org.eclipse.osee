/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.priority.PriorityColumnUI;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests PriorityColumn
 * @author Donald G. Dunne
 */
public class PriorityColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();
      AtsApi atsApi = AtsApiService.get();

      IAtsTeamWorkflow reqWf = DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals("3", PriorityColumnUI.getInstance().getColumnText(reqWf, PriorityColumnUI.getInstance(), 0));

      IAtsTeamWorkflow codeWf = DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("3", PriorityColumnUI.getInstance().getColumnText(codeWf, PriorityColumnUI.getInstance(), 0));

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) atsApi.getRelationResolver().getRelatedOrNull(codeWf,
            AtsRelationTypes.TeamWorkflowToReview_Review);
      Assert.assertEquals("", PriorityColumnUI.getInstance().getColumnText(peerArt, PriorityColumnUI.getInstance(), 0));

      TaskArtifact taskArt = (TaskArtifact) atsApi.getRelationResolver().getRelated(codeWf,
         AtsRelationTypes.TeamWfToTask_Task).iterator().next();
      Assert.assertEquals("", PriorityColumnUI.getInstance().getColumnText(taskArt, PriorityColumnUI.getInstance(), 0));

      Artifact actionArt = (Artifact) reqWf.getParentAction().getStoreObject();
      Assert.assertEquals("3",
         PriorityColumnUI.getInstance().getColumnText(actionArt, PriorityColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
