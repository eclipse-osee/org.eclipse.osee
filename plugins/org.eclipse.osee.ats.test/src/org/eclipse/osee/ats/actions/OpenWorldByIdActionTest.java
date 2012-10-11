/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class OpenWorldByIdActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenWorldByIdAction createAction() throws OseeCoreException {
      OpenWorldByIdAction action = new OpenWorldByIdAction();
      action.setOverrideIdString(AtsTestUtil.getTeamWf().getHumanReadableId());
      action.setPend(true);
      return action;
   }

}
