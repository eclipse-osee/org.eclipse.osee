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
package org.eclipse.osee.ats.client.integration.tests.ats.column;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.demo.api.DemoWorkType;
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
      Assert.assertEquals(SAW_Bld_2.getName(),
         TargetedVersionColumnUI.getInstance().getColumnText(reqArt, TargetedVersionColumnUI.getInstance(), 0));

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals(SAW_Bld_2.getName(),
         TargetedVersionColumnUI.getInstance().getColumnText(codeArt, TargetedVersionColumnUI.getInstance(), 0));

      Artifact actionArt = reqArt.getParentActionArtifact();
      Assert.assertEquals(SAW_Bld_2.getName(),
         TargetedVersionColumnUI.getInstance().getColumnText(actionArt, TargetedVersionColumnUI.getInstance(), 0));

      TeamWorkFlowArtifact toolsArt = DemoTestUtil.getToolsTeamWorkflow();
      Assert.assertEquals("",
         TargetedVersionColumnUI.getInstance().getColumnText(toolsArt, TargetedVersionColumnUI.getInstance(), 0));

      actionArt = toolsArt.getParentActionArtifact();
      Assert.assertEquals("",
         TargetedVersionColumnUI.getInstance().getColumnText(actionArt, TargetedVersionColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
