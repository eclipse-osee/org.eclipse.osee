/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Result;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DirtyReportActionTest extends AbstractAtsActionTest {

   @Test(expected = OseeStateException.class)
   public void test() throws Exception {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      DirtyReportAction action = createAction();
      action.runWithException();
   }

   @Override
   public DirtyReportAction createAction() {
      return new DirtyReportAction(new IDirtyReportable() {
         @Override
         public Result isDirtyResult() {
            return new Result("Hello World");
         }
      });
   }

}
