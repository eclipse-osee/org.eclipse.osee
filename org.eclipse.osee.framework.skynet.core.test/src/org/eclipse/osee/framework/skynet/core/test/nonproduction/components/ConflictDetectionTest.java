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
package org.eclipse.osee.framework.skynet.core.test.nonproduction.components;

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
public class ConflictDetectionTest extends TestCase {

   /**
    * @param name
    */
   public ConflictDetectionTest(String name) {
      super(name);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal#getConflictsPerBranch(org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.transaction.TransactionId)}
    * .
    */
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
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getSevereLogs().size()),
            monitorLog.getSevereLogs().size() == 0);
   }

}
