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
