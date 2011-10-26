/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.junit.Test;

/**
 * Abstract for the simplest test case where Action just needs run called
 * 
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsActionPerspectiveTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      IWorkbenchWindowActionDelegate action = getPerspectiveAction();
      action.run(null);
      TestUtil.severeLoggingEnd(monitor);
   }

   public abstract IWorkbenchWindowActionDelegate getPerspectiveAction();
}
