/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * Abstract for the simplest test case where Action just needs run called
 * 
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsActionRunTest extends AbstractAtsActionTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      Action action = createAction();
      // want the exception to propagate to top if AbstractAtsAction
      if (action instanceof AbstractAtsAction) {
         ((AbstractAtsAction) action).runWithException();
      } else {
         action.run();
      }
      TestUtil.severeLoggingEnd(monitor);
   }

}
