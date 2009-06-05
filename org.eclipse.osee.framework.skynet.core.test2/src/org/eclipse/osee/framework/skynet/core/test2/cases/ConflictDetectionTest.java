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
package org.eclipse.osee.framework.skynet.core.test2.cases;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.HashSet;
import junit.framework.TestCase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class ConflictDetectionTest {

   /**
    * @param name
    */
   public ConflictDetectionTest(String name) {
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal#getConflictsPerBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.transaction.TransactionId)}
    * .
    */
   @org.junit.Test
public void testGetConflictsPerBranch() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      Collection<Conflict> conflicts = new HashSet<Conflict>();
      try {
         conflicts =
               ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionIdManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getKey(), new EmptyMonitor());
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertEquals("Number of conflicts found is not equal to the number of conflicts expected",
            ConflictTestManager.numberOfConflicts(), conflicts.toArray().length);
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }

}
