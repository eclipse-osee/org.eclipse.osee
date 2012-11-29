/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.DeletePurgeAtsArtifactsAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
   public DeletePurgeAtsArtifactsAction createAction() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      return new DeletePurgeAtsArtifactsAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf(), true);
   }
}
