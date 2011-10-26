/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class MyFavoritesActionTest extends AbstractAtsActionTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      MyFavoritesAction action = new MyFavoritesAction();
      action.runWithException();
      TestUtil.severeLoggingEnd(monitor);
   }

   @Override
   public MyFavoritesAction createAction() {
      return new MyFavoritesAction();
   }

}
