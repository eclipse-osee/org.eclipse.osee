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
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.operation.MoveTeamWorkflowsOperation;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.TransactionToken;
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
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      IAtsAction actArt2 = AtsTestUtil.getActionArt2();
      TeamWorkFlowArtifact teamWf2 = AtsTestUtil.getTeamWf2();

      MoveTeamWorkflowsOperation operation = new MoveTeamWorkflowsOperation("Move", teamWf, Arrays.asList(teamWf2));
      Operations.executeWorkAndCheckStatus(operation);
      TransactionToken tx = operation.getTx();
      Assert.assertTrue(tx.isValid());

      Assert.assertEquals("Parent Actions should be same", teamWf.getParentAction(), teamWf2.getParentAction());
      Assert.assertTrue("Action Artifact 2 should be deleted", ((Artifact) actArt2.getStoreObject()).isDeleted());
   }
}
