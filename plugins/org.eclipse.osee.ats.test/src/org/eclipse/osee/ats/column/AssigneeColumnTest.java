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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests AssigneeColumn
 * @author Donald G. Dunne
 */
public class AssigneeColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("Joe Smith",
         AssigneeColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));

      Artifact actionArt = codeArt.getParentActionArtifact();
      List<String> results = Arrays.asList("Kay Jones; Joe Smith", "Joe Smith; Kay Jones");
      Assert.assertTrue(results.contains(AssigneeColumn.getInstance().getColumnText(actionArt,
         AssigneeColumn.getInstance(), 0)));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

   public void testGetColumnImage() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertNotNull(ActionableItemsColumn.getInstance().getColumnImage(codeArt, AssigneeColumn.getInstance(), 0));

      Artifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertNotNull(ActionableItemsColumn.getInstance().getColumnImage(actionArt, AssigneeColumn.getInstance(),
         0));

      Assert.assertNull(ActionableItemsColumn.getInstance().getColumnImage("String", AssigneeColumn.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
