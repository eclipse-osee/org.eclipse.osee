/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.operation;

import java.util.Arrays;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.access.AtsArtifactChecks;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.operation.MoveTeamWorkflowsOperation;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
      IAtsAction actArt = AtsTestUtil.getActionArt();
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsAction actArt2 = AtsTestUtil.getActionArt2();
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();

      try {
         AtsArtifactChecks.setDeletionChecksEnabled(false);
         MoveTeamWorkflowsOperation operation =
            new MoveTeamWorkflowsOperation("Move", teamWf, Arrays.asList(teamWf2), "new title");
         Operations.executeWorkAndCheckStatus(operation);
      } finally {
         AtsArtifactChecks.setDeletionChecksEnabled(false);
      }

      Assert.assertEquals("Parent Actions should be same", teamWf.getParentAction(), teamWf.getParentAction());
      Assert.assertEquals("new title", actArt.getName());
      Assert.assertTrue("Action Artifact 2 should be deleted", ((Artifact) actArt2).isDeleted());
      Assert.assertFalse("No artifact should be dirty",
         ((Artifact) actArt).isDirty() && teamWf.isDirty() && ((Artifact) actArt2).isDirty() && teamWf2.isDirty());
   }
}
