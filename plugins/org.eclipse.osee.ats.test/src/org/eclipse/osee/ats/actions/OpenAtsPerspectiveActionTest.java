/*
 * Created on Oct 23, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Donald G. Dunne
 */
public class OpenAtsPerspectiveActionTest extends AbstractAtsActionPerspectiveTest {

   @Override
   public IWorkbenchWindowActionDelegate getPerspectiveAction() {
      return new OpenAtsPerspectiveAction();
   }

}
