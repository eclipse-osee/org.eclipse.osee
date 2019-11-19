/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.operation;

import java.util.Arrays;
import org.eclipse.osee.ats.core.access.AtsArtifactChecks;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.operation.MoveTeamWorkflowsOperation;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class MoveTeamWorkflowsOperationTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @org.junit.Test
   public void testDoWork() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      ActionArtifact actArt = AtsTestUtil.getActionArt();
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      ActionArtifact actArt2 = AtsTestUtil.getActionArt2();
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();

      try {
         AtsArtifactChecks.setDeletionChecksEnabled(false);
         MoveTeamWorkflowsOperation operation =
            new MoveTeamWorkflowsOperation("Move", teamWf, Arrays.asList(teamWf2), "new title");
         Operations.executeWorkAndCheckStatus(operation);
      } finally {
         AtsArtifactChecks.setDeletionChecksEnabled(false);
      }

      Assert.assertEquals("Parent Actions should be same", teamWf.getParentActionArtifact(),
         teamWf.getParentActionArtifact());
      Assert.assertEquals("new title", actArt.getName());
      Assert.assertTrue("Action Artifact 2 should be deleted", actArt2.isDeleted());
      Assert.assertFalse("No artifact should be dirty",
         actArt.isDirty() && teamWf.isDirty() && actArt2.isDirty() && teamWf2.isDirty());
   }
}
