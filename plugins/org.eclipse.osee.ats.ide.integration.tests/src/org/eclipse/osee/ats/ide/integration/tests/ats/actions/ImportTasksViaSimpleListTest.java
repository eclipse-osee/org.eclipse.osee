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

import org.eclipse.osee.ats.ide.actions.ImportListener;
import org.eclipse.osee.ats.ide.actions.ImportTasksViaSimpleList;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
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
         public void importCompleted(XResultData results) {
            // do nothing
         }

      });
      action.runWithException();
      AtsTestUtil.cleanup();
      TestUtil.severeLoggingEnd(monitor);
   }

}
