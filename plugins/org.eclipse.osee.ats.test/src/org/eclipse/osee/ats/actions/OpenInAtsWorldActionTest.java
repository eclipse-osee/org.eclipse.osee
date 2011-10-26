/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenInAtsWorldAction createAction() throws OseeCoreException {
      return new OpenInAtsWorldAction(AtsTestUtil.getTeamWf());
   }

   @Test(expected = OseeStateException.class)
   public void testNoParentAction() throws OseeCoreException {
      AtsTestUtil.getTeamWf().deleteRelations(AtsRelationTypes.ActionToWorkflow_Action);
      AtsTestUtil.getTeamWf().persist(getClass().getSimpleName());

      OpenInAtsWorldAction action = createAction();
      action.runWithException();
   }

}
