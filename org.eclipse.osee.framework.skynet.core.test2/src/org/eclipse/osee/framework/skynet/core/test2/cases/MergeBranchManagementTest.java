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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Theron Virgin
 */
public class MergeBranchManagementTest {

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core.test/debug/Junit"));

   /**
    * @param name
    */
   public MergeBranchManagementTest(String name) {
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(Branch, Branch)} .
    */
   @org.junit.Test
public void testGetMergeBranchNotCreated() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         Branch mergeBranch =
               BranchManager.getMergeBranch(ConflictTestManager.getSourceBranch(), ConflictTestManager.getDestBranch());

         assertTrue("The merge branch should be null as it hasn't been created yet", mergeBranch == null);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.skynet.core.artifact.BranchManager#getMergeBranch(Branch, Branch)} .
    */
   @org.junit.Test
public void testGetMergeBranchCreated() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         Branch mergeBranch =
               BranchManager.getMergeBranch(ConflictTestManager.getSourceBranch(), ConflictTestManager.getDestBranch());
         assertFalse(mergeBranch == null);
         Collection<Artifact> artifacts = ArtifactQuery.getArtifactsFromBranch(mergeBranch, true);
         if (DEBUG) {
            System.out.println("Found the following Artifacts on the branch ");
            System.out.print("     ");
            for (Artifact artifact : artifacts) {
               System.out.print(artifact.getArtId() + ", ");
            }
            System.out.println("\n");
         }
         assertEquals("The merge Branch does not contain the expected number of artifacts: ",
               ConflictTestManager.numberOfArtifactsOnMergeBranch(), artifacts.toArray().length);
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }

}
