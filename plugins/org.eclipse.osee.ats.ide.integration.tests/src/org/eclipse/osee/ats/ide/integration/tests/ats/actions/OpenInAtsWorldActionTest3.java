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

import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Test;

/**
 * Test case where no parent action
 * 
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldActionTest3 extends AbstractAtsActionRunTest {

   @Override
   @Test(expected = OseeStateException.class)
   public void test() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      OpenInAtsWorldAction action = createAction();
      action.runWithException();
   }

   @Override
   public OpenInAtsWorldAction createAction() {
      AtsTestUtil.getTeamWf().deleteRelations(AtsRelationTypes.ActionToWorkflow_Action);
      AtsTestUtil.getTeamWf().persist(getClass().getSimpleName());
      return new OpenInAtsWorldAction(AtsTestUtil.getTeamWf());
   }

}
