/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
