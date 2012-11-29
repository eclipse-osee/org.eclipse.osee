/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.CopyActionDetailsAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetailsActionTest extends AbstractAtsActionRunTest {

   @Override
   public CopyActionDetailsAction createAction() throws OseeCoreException {
      return new CopyActionDetailsAction(AtsTestUtil.getTeamWf());
   }

}
