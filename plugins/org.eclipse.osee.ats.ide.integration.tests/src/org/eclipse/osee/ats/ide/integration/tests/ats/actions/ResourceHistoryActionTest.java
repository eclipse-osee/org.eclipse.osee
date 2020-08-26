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

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.actions.ResourceHistoryAction;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ResourceHistoryActionTest extends AbstractAtsActionTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Assert.assertFalse(AtsApiService.get().getRelationResolver().getRelated(
         (IAtsObject) AtsApiService.get().getUserService().getCurrentUser(),
         AtsRelationTypes.FavoriteUser_Artifact).contains(AtsTestUtil.getTeamWf()));
      ResourceHistoryAction action = createAction();
      action.runWithException();
      TestUtil.severeLoggingEnd(monitor);
   }

   @Override
   public ResourceHistoryAction createAction() {
      return new ResourceHistoryAction(AtsTestUtil.getTeamWf());
   }

}
