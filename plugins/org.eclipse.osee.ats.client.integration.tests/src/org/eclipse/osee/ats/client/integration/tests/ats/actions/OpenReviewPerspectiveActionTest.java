/*
 * Created on Oct 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.OpenReviewPerspectiveAction;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Donald G. Dunne
 */
public class OpenReviewPerspectiveActionTest extends AbstractAtsActionPerspectiveTest {

   @Override
   public IWorkbenchWindowActionDelegate getPerspectiveAction() {
      return new OpenReviewPerspectiveAction();
   }

}
