/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AccessControlActionTest extends AbstractAtsActionTest {

   @Test
   public void testRun() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      final AccessControlAction action = (AccessControlAction) createAction();
      Job job = new Job("Kill dialog") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               Thread.sleep(2000);
            } catch (InterruptedException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  action.getDialog().getShell().close();
               }
            });
            return Status.OK_STATUS;
         }
      };
      job.schedule();
      action.run();
      TestUtil.severeLoggingEnd(monitor);
   }

   @Override
   public Action createAction() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      return new AccessControlAction(AtsTestUtil.getTeamWf());
   }
}
