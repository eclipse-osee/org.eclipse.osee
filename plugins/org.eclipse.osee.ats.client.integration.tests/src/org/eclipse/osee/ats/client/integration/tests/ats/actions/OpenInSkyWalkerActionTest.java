/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.OpenInSkyWalkerAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class OpenInSkyWalkerActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenInSkyWalkerAction createAction() throws OseeCoreException {
      return new OpenInSkyWalkerAction(AtsTestUtil.getTeamWf());
   }

}
