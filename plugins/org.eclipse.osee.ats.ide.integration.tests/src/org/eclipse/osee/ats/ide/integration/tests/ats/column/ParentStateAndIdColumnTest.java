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

import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.column.ParentAtsIdColumn;
import org.eclipse.osee.ats.ide.column.ParentStateColumn;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
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
      ActionArtifact actionArt = codeArt.getParentActionArtifact();

      Assert.assertEquals("",
         ParentStateColumn.getInstance().getColumnText(codeArt, ParentAtsIdColumn.getInstance(), 0));
      Assert.assertEquals(actionArt.getAtsId(),
         ParentAtsIdColumn.getInstance().getColumnText(codeArt, ParentAtsIdColumn.getInstance(), 0));

      PeerToPeerReviewArtifact peerArt =
         (PeerToPeerReviewArtifact) codeArt.getRelatedArtifact(AtsRelationTypes.TeamWorkflowToReview_Review);
      Assert.assertEquals(TeamState.Implement.getName(),
         ParentStateColumn.getInstance().getColumnText(peerArt, ParentAtsIdColumn.getInstance(), 0));
      Assert.assertEquals(codeArt.getAtsId(),
         ParentAtsIdColumn.getInstance().getColumnText(peerArt, ParentAtsIdColumn.getInstance(), 0));

      Assert.assertEquals("",
         ParentStateColumn.getInstance().getColumnText(actionArt, ParentAtsIdColumn.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
