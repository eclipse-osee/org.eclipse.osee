/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.actions.DuplicateWorkflowViaWorldEditorAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.actions.SelectedAtsArtifactsAdapter;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowViaWorldEditorActionTest extends AbstractAtsActionRunTest {

   @Override
   public Action createAction() {
      return new DuplicateWorkflowViaWorldEditorAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf());
   }

   @Test(expected = OseeArgumentException.class)
   public void testException() throws OseeCoreException {
      DuplicateWorkflowViaWorldEditorAction action =
         new DuplicateWorkflowViaWorldEditorAction(new SelectedAtsArtifactsAdapter());
      action.runWithException();
   }
}
