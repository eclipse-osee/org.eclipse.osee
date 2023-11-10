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

import java.util.HashMap;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.column.ParentAtsIdColumnUI;
import org.eclipse.osee.ats.ide.column.ParentStateColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests ParentStateColumn
 * @tests ParentAtsIdColumn
 * @author Donald G. Dunne
 */
public class ParentStateAndIdColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      IAtsAction actionArt = codeArt.getParentAction();

      Assert.assertEquals("",
         ParentStateColumnUI.getInstance().getColumnText(codeArt, ParentAtsIdColumnUI.getInstance(), 0));
      Assert.assertEquals(actionArt.getAtsId(), ParentAtsIdColumnUI.getInstance().getValue(codeArt, new HashMap<>()));

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) codeArt.getRelatedArtifact(AtsRelationTypes.TeamWorkflowToReview_Review);
      Assert.assertEquals(TeamState.Implement.getName(),
         ParentStateColumnUI.getInstance().getColumnText(peerArt, ParentAtsIdColumnUI.getInstance(), 0));
      Assert.assertEquals(codeArt.getAtsId(), ParentAtsIdColumnUI.getInstance().getValue(peerArt, new HashMap<>()));

      Assert.assertEquals("",
         ParentStateColumnUI.getInstance().getColumnText(actionArt, ParentAtsIdColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
