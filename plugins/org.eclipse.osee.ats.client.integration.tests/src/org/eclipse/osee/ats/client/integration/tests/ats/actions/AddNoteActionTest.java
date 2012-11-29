/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.actions.AddNoteAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AddNoteActionTest extends AbstractAtsActionTest {

   @Test
   public void testRun() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AddNoteAction action = (AddNoteAction) createAction();
      action.setEmulateUi(true);
      action.runWithException();
      AtsTestUtil.getTeamWf().persist(getClass().getSimpleName());
      TestUtil.severeLoggingEnd(monitor);
   }

   @Override
   public Action createAction() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      return new AddNoteAction(AtsTestUtil.getTeamWf(), new IDirtiableEditor() {

         @Override
         public void onDirtied() {
            // do nothing
         }
      });
   }

}
