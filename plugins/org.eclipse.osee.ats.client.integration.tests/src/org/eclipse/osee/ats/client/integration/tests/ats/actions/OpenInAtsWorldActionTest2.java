/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.OpenInAtsWorldAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldActionTest2 extends AbstractAtsActionRunTest {

   @Override
   public OpenInAtsWorldAction createAction() throws OseeCoreException {
      return new OpenInAtsWorldAction(AtsTestUtil.getOrCreateTaskOffTeamWf1());
   }

}
