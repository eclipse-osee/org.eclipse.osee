/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksViaSimpleListTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      ImportTasksViaSimpleList action = new ImportTasksViaSimpleList(AtsTestUtil.getTeamWf(), new ImportListener() {

         @Override
         public void importCompleted() {
            System.out.println(" ");
         }

      });
      action.runWithException();
      AtsTestUtil.cleanup();
      TestUtil.severeLoggingEnd(monitor);
   }

}
