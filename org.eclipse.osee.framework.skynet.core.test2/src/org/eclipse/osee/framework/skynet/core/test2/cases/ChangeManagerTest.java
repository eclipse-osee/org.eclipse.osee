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

import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * Tests the Change Manager.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeManagerTest extends TestCase {
   private static Artifact artifact;
   private Branch branch;

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();

      BranchManager.purgeBranch(branch);
      sleep(5000);
   }

   @Override
   protected void setUp() throws Exception {
      assertFalse("This test can not be run on Production", ClientSessionManager.isProductionDataStore());

      super.setUp();

      String branchName = "Change Manager Test Branch";

      branch =
            BranchManager.createWorkingBranch(BranchManager.getSystemRootBranch(), branchName,
                  UserManager.getUser(SystemUser.OseeSystem));
      sleep(5000);

      artifact = ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch);
      artifact.persistAttributes();
      sleep(5000);
   }

   public void testIntroduceCrossBranch() throws Exception {
      boolean pass = false;
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);


      sleep(5000);

      for(Change change : ChangeManager.getChangesPerBranch(branch, new EmptyMonitor())){
         if(change.getArtId() == artifact.getArtId()){
            pass = change.getModificationType() == ModificationType.NEW;
         }
      }
      assertTrue(pass);
   }

   public static void sleep(long milliseconds) throws Exception {
      OseeLog.log(ChangeManagerTest.class, Level.INFO, "Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      OseeLog.log(ChangeManagerTest.class, Level.INFO, "Awake");
   }
}
