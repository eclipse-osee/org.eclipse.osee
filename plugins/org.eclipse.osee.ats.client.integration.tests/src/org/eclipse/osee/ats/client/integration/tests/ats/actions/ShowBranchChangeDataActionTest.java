/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import junit.framework.Assert;
import org.eclipse.osee.ats.actions.ShowBranchChangeDataAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ShowBranchChangeDataActionTest extends AbstractAtsActionRunTest {

   @Override
   public ShowBranchChangeDataAction createAction() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      Assert.assertTrue(result.getText(), result.isTrue());
      TestUtil.sleep(2000);
      Assert.assertNotNull(AtsTestUtil.getTeamWf().getWorkingBranch());

      return new ShowBranchChangeDataAction(AtsTestUtil.getTeamWf());
   }

   @Override
   @Test
   public void getImageDescriptor() {
      ShowBranchChangeDataAction action = new ShowBranchChangeDataAction(null);
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
   }

}
