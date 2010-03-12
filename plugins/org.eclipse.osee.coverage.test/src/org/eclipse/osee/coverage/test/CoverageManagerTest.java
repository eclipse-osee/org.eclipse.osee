/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.test;

import static org.junit.Assert.fail;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestBlam;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;

/**
 * @author Donald G. Dunne
 */
public class CoverageManagerTest {

   private static SevereLoggingMonitor monitorLog;

   @org.junit.BeforeClass
   public static void testSetup() throws Exception {
      monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
   }

   @org.junit.AfterClass
   public static void testCleanup() throws Exception {
      List<IHealthStatus> stats = monitorLog.getAllLogs();
      for (IHealthStatus stat : new CopyOnWriteArrayList<IHealthStatus>(stats)) {
         if (stat.getException() != null) {
            fail("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
   }

   @org.junit.Test
   public void testImportCoverage() throws Exception {
      CoverageManager.importCoverage(new CoverageImport1TestBlam());
   }
}
