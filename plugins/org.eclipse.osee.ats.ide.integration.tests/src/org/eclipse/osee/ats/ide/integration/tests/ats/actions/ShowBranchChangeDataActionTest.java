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

import org.eclipse.osee.ats.ide.actions.ShowBranchChangeDataAction;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ShowBranchChangeDataActionTest extends AbstractAtsActionRunTest {

   @Override
   public ShowBranchChangeDataAction createAction() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      Assert.assertTrue(result.getText(), result.isTrue());
      Assert.assertNotNull(AtsTestUtil.getTeamWf().getWorkingBranch());

      return new ShowBranchChangeDataAction(AtsTestUtil.getTeamWf());
   }

   @Override
   @Test
   public void getImageDescriptor() {
      ShowBranchChangeDataAction action = new ShowBranchChangeDataAction(null);
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
   }

}
