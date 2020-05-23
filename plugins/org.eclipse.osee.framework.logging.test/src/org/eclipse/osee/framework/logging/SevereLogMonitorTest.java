/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.logging;

import static org.junit.Assert.assertTrue;
import java.util.logging.Level;

/**
 * @author Andrew M. Finkbeiner
 */
public class SevereLogMonitorTest {

   @org.junit.Test
   public void testCatchingOfException() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Exception ex = new Exception("this is my test exception");
      OseeLog.log(SevereLogMonitorTest.class, Level.SEVERE, "caught our exception in a junit", ex);
      OseeLog.unregisterLoggerListener(monitorLog);
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
         monitorLog.getAllLogs().size() == 1);
   }

}
