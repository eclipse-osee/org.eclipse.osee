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

import org.eclipse.osee.ats.actions.DeletePurgeAtsArtifactsAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
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
