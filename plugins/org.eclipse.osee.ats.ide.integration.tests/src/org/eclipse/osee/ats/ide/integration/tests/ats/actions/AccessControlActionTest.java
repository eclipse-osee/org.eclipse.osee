/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.actions.AccessControlAction;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
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
   public Action createAction() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      return new AccessControlAction(AtsTestUtil.getTeamWf());
   }
}
