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

import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.BranchStatusColumn;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests BranchStatusColumn
 * @author Donald G. Dunne
 */
public class BranchStatusColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      IAtsTeamWorkflow reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals("Working", BranchStatusColumn.getInstance().getBranchStatus(reqArt));

      IAtsTeamWorkflow testArt = (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      Assert.assertEquals("", BranchStatusColumn.getInstance().getBranchStatus(testArt));

      TeamWorkFlowArtifact reqArt2 =
         (TeamWorkFlowArtifact) DemoTestUtil.getCommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals("Needs Commit", BranchStatusColumn.getInstance().getBranchStatus(reqArt2));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
