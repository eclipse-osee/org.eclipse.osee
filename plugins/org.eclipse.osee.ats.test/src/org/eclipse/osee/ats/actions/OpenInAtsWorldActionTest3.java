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
 * Test case where no parent action
 * 
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldActionTest3 extends AbstractAtsActionRunTest {

   @Override
   @Test(expected = OseeStateException.class)
   public void test() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      OpenInAtsWorldAction action = createAction();
      action.runWithException();
   }

   @Override
   public OpenInAtsWorldAction createAction() throws OseeCoreException {
      AtsTestUtil.getTeamWf().deleteRelations(AtsRelationTypes.ActionToWorkflow_Action);
      AtsTestUtil.getTeamWf().persist(getClass().getSimpleName());
      return new OpenInAtsWorldAction(AtsTestUtil.getTeamWf());
   }

}
