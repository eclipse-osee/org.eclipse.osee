/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.team.TeamState;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests ParentStateColumn
 * @tests ParentHridColumn
 * @author Donald G. Dunne
 */
public class ParentStateAndIdColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Artifact actionArt = codeArt.getParentActionArtifact();

      Assert.assertEquals("", ParentStateColumn.getInstance().getColumnText(codeArt, ParentHridColumn.getInstance(), 0));
      Assert.assertEquals(actionArt.getHumanReadableId(),
         ParentHridColumn.getInstance().getColumnText(codeArt, ParentHridColumn.getInstance(), 0));

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) codeArt.getRelatedArtifact(AtsRelationTypes.TeamWorkflowToReview_Review);
      Assert.assertEquals(TeamState.Implement.getName(),
         ParentStateColumn.getInstance().getColumnText(peerArt, ParentHridColumn.getInstance(), 0));
      Assert.assertEquals(codeArt.getHumanReadableId(),
         ParentHridColumn.getInstance().getColumnText(peerArt, ParentHridColumn.getInstance(), 0));

      Assert.assertEquals("",
         ParentStateColumn.getInstance().getColumnText(actionArt, ParentHridColumn.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
