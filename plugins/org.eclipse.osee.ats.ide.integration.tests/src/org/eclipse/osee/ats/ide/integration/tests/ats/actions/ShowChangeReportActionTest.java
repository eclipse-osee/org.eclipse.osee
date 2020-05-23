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

package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import org.eclipse.osee.ats.ide.actions.ShowChangeReportAction;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ShowChangeReportActionTest extends AbstractAtsActionRunTest {

   @Override
   public ShowChangeReportAction createAction() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      Assert.assertTrue(result.getText(), result.isTrue());
      ShowChangeReportAction action = new ShowChangeReportAction(AtsTestUtil.getTeamWf());
      return action;
   }

   @Override
   @Test
   public void getImageDescriptor() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      ShowChangeReportAction action = new ShowChangeReportAction(AtsTestUtil.getTeamWf());
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
      TestUtil.severeLoggingEnd(monitor);
   }

}
