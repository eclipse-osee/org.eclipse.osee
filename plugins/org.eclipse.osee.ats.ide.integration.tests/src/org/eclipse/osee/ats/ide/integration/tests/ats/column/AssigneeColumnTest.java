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

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.AssigneeColumn;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests AssigneeColumnUI
 * @author Donald G. Dunne
 */
public class AssigneeColumnTest {

   public void testGetColumnImage() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      IAtsTeamWorkflow codeArt = DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertNotNull(AssigneeColumnUI.getInstance().getColumnImage(codeArt, AssigneeColumnUI.getInstance(), 0));

      Artifact actionArt = (Artifact) codeArt.getParentAction().getStoreObject();
      Assert.assertNotNull(AssigneeColumnUI.getInstance().getColumnImage(actionArt, AssigneeColumnUI.getInstance(), 0));

      Assert.assertNull(AssigneeColumnUI.getInstance().getColumnImage("String", AssigneeColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

   @org.junit.Test
   public void testGetAssigneeStr_null() {
      Assert.assertEquals("", AssigneeColumn.getAssigneeStrr(null));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromInWorkWorkflow() {
      IAtsTeamWorkflow sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();
      Assert.assertEquals("Joe Smith", AssigneeColumn.getAssigneeStrr(sawCodeCommittedWf));
   }

   @org.junit.Test
   public void testGetAssigneeStrFromCompletedWorkflow() {
      ArtifactToken rev3 = AtsApiService.get().getQueryService().getArtifactByName(AtsArtifactTypes.PeerToPeerReview,
         "3 - Review new logic");
      String rev3Assignees = AssigneeColumn.getAssigneeStrr((IAtsPeerToPeerReview) rev3);
      Assert.assertEquals("(Alex Kay; Joe Smith; Kay Jones)", rev3Assignees);
      IAtsPeerToPeerReview peerRev = (IAtsPeerToPeerReview) AtsApiService.get().getReviewService().getReview(rev3);
      Assert.assertTrue(peerRev.getAssignees().isEmpty());
      Assert.assertEquals(0,
         AtsApiService.get().getAttributeResolver().getAttributeCount(rev3, AtsAttributeTypes.CurrentStateAssignee));

      ArtifactToken revButtonTeamWf = AtsApiService.get().getQueryService().getArtifactByName(
         AtsArtifactTypes.TeamWorkflow, DemoArtifactToken.ButtonSDoesntWorkOnHelp_TeamWf.getName());
      ((Artifact) revButtonTeamWf).reloadAttributesAndRelations();
      String revButtonAssignees = AssigneeColumn.getAssigneeStrr((IAtsTeamWorkflow) revButtonTeamWf);
      Assert.assertEquals("(Jeffery Kay)", revButtonAssignees);
      IAtsTeamWorkflow revWf = AtsApiService.get().getWorkItemService().getTeamWf(revButtonTeamWf);
      String revButtonWfImplementers = AtsApiService.get().getImplementerService().getImplementersStr(revWf);
      Assert.assertEquals("Jeffery Kay", revButtonWfImplementers);
   }

   @org.junit.Test
   public void testGetAssigneesStrAction() {
      IAtsTeamWorkflow sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();
      IAtsAction action = sawCodeCommittedWf.getParentAction();
      Assert.assertEquals("", AssigneeColumn.getAssigneeStrr(action));
   }

}
