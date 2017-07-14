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

import org.eclipse.osee.ats.actions.ShowBranchChangeDataAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ShowBranchChangeDataActionTest extends AbstractAtsActionRunTest {

   @Override
   public ShowBranchChangeDataAction createAction() throws OseeCoreException {
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
