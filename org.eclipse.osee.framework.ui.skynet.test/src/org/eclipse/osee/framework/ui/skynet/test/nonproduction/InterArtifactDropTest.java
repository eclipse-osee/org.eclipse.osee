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
package org.eclipse.osee.framework.ui.skynet.test.nonproduction;

import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandler;

/**
 * Tests the BLAM operation updateFromParentBranch.
 * 
 * @author Jeff C. Phillips
 */
public class InterArtifactDropTest extends TestCase {
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet.test/debug/Junit"));
   private static Artifact source;
   private static final String SOURCE_BRANCH = "Source Branch";
   private static final String DESTINATION_BRANCH = "Destination Branch";

   protected void tearDown() throws Exception {
      super.tearDown();

      BranchManager.deleteBranch(BranchManager.getBranch(DESTINATION_BRANCH));
      BranchManager.deleteBranch(BranchManager.getBranch(SOURCE_BRANCH));
   }

   protected void setUp() throws Exception {
      assertFalse("This test can not be run on Production", ClientSessionManager.isProductionDataStore());

      super.setUp();

      Branch sourceBranch =
            BranchManager.createWorkingBranch(BranchManager.getSystemRootBranch(), SOURCE_BRANCH,
                  UserManager.getUser(SystemUser.OseeSystem));
      sleep(5000);

      source = ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, sourceBranch);
      source.persistAttributes();

      BranchManager.createWorkingBranch(BranchManager.getSystemRootBranch(), DESTINATION_BRANCH,
            UserManager.getUser(SystemUser.OseeSystem));
      sleep(5000);
   }

   public void testIntroduceCrossBranch() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      Branch destinationBranch = BranchManager.getBranch(DESTINATION_BRANCH);

      InterArtifactExplorerDropHandler dropHandler = new InterArtifactExplorerDropHandler();
      dropHandler.dropArtifactIntoDifferentBranch(
            ArtifactQuery.getDefaultHierarchyRootArtifact(destinationBranch, true), new Artifact[] {source});

      sleep(5000);

      Artifact destArtifact = ArtifactQuery.getArtifactFromId(source.getArtId(), destinationBranch);

      assertTrue(source.getDescriptiveName().equals(destArtifact.getDescriptiveName()));
   }

   public static void sleep(long milliseconds) throws Exception {
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Awake");
   }
}
