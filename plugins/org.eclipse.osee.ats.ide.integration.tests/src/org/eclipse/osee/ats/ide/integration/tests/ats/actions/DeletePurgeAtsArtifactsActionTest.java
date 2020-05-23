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

import org.eclipse.osee.ats.ide.actions.DeletePurgeAtsArtifactsAction;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DeletePurgeAtsArtifactsActionTest extends AbstractAtsActionTest {

   @Test
   public void testRun() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      DeletePurgeAtsArtifactsAction action = createAction();
      action.setPrompt(false);
      action.runWithException();
      TestUtil.severeLoggingEnd(monitor);
   }

   @Override
   public DeletePurgeAtsArtifactsAction createAction() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      return new DeletePurgeAtsArtifactsAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf(), true);
   }
}
