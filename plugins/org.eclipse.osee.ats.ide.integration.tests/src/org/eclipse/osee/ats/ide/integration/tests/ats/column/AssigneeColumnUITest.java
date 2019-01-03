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
package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.demo.api.DemoWorkType;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests AssigneeColumnUI
 * @author Donald G. Dunne
 */
public class AssigneeColumnUITest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals(DemoUsers.Joe_Smith.getName(),
         AssigneeColumnUI.getInstance().getColumnText(codeArt, AssigneeColumnUI.getInstance(), 0));

      Artifact actionArt = codeArt.getParentActionArtifact();
      List<String> results = Arrays.asList(DemoUsers.Joe_Smith_And_Kay_Jones, DemoUsers.Kay_Jones_And_Joe_Smith);
      Assert.assertTrue(
         results.contains(AssigneeColumnUI.getInstance().getColumnText(actionArt, AssigneeColumnUI.getInstance(), 0)));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

   public void testGetColumnImage() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertNotNull(AssigneeColumnUI.getInstance().getColumnImage(codeArt, AssigneeColumnUI.getInstance(), 0));

      Artifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertNotNull(AssigneeColumnUI.getInstance().getColumnImage(actionArt, AssigneeColumnUI.getInstance(), 0));

      Assert.assertNull(AssigneeColumnUI.getInstance().getColumnImage("String", AssigneeColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
