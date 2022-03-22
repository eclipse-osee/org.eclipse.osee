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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests TargetedVersionColumn
 * @author Donald G. Dunne
 */
public class TargetedVersionColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals(SAW_Bld_2.getName(), TargetedVersionColumnUI.getInstance().getValue(reqArt));

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals(SAW_Bld_2.getName(), TargetedVersionColumnUI.getInstance().getValue(codeArt));

      Artifact actionArt = (Artifact) reqArt.getParentAction().getStoreObject();
      Assert.assertEquals(SAW_Bld_2.getName(), TargetedVersionColumnUI.getInstance().getValue(actionArt));

      TeamWorkFlowArtifact toolsArt = DemoTestUtil.getToolsTeamWorkflow();
      Assert.assertEquals("", TargetedVersionColumnUI.getInstance().getValue(toolsArt));

      actionArt = (Artifact) toolsArt.getParentAction().getStoreObject();
      Assert.assertEquals("", TargetedVersionColumnUI.getInstance().getValue(actionArt));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
