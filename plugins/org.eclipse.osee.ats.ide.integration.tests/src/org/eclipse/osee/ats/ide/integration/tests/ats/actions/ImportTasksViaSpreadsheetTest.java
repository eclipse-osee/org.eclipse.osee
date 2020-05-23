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

import org.eclipse.osee.ats.ide.actions.ImportListener;
import org.eclipse.osee.ats.ide.actions.ImportTasksViaSpreadsheet;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksViaSpreadsheetTest {

   @Test
   public void test() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      ImportTasksViaSpreadsheet action = new ImportTasksViaSpreadsheet(AtsTestUtil.getTeamWf(), new ImportListener() {

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
