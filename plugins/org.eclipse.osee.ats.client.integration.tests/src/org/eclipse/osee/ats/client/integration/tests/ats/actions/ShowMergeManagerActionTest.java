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
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.ShowMergeManagerAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
@Ignore
public class ShowMergeManagerActionTest extends AbstractAtsActionRunTest {

   private static BranchId createdBranch = null;

   @After
   public void cleanup_closeMergeView() {
      if (createdBranch != null) {
         BranchManager.deleteBranchAndPend(createdBranch);
      }
   }

   @Override
   public ShowMergeManagerAction createAction()  {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      createdBranch = AtsTestUtil.getTeamWf().getWorkingBranch();
      Assert.assertTrue(result.getText(), result.isTrue());

      ShowMergeManagerAction action = new ShowMergeManagerAction(AtsTestUtil.getTeamWf());
      return action;
   }

   @Override
   @Test
   public void getImageDescriptor() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      ShowMergeManagerAction action = new ShowMergeManagerAction(AtsTestUtil.getTeamWf());
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
      TestUtil.severeLoggingEnd(monitor);
   }

}
