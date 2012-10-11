/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import junit.framework.Assert;
import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class OpenChangeReportByIdActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenChangeReportByIdAction createAction() throws OseeCoreException {

      Result result = AtsTestUtil.createWorkingBranchFromTeamWf();
      Assert.assertTrue(result.getText(), result.isTrue());

      OpenChangeReportByIdAction action = new OpenChangeReportByIdAction();
      action.setOverrideId(AtsTestUtil.getTeamWf().getHumanReadableId());
      action.setPend(true);
      return action;
   }

   @Override
   @Test
   public void getImageDescriptor() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      OpenChangeReportByIdAction action = new OpenChangeReportByIdAction();
      Assert.assertNotNull("Image should be specified", action.getImageDescriptor());
      TestUtil.severeLoggingEnd(monitor);
   }

}
