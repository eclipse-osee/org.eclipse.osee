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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandler;
import org.junit.After;
import org.junit.Before;

/**
 * Tests cross branch drag and drop.
 * 
 * @author Jeff C. Phillips
 */
public class InterArtifactDropTest {
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet.test/debug/Junit"));
   private static Artifact sourceArtifact;
   private Branch sourceBranch;
   private Branch destinationBranch;

   @Before
   public void setUp() throws Exception {
      assertFalse("This test can not be run on Production", ClientSessionManager.isProductionDataStore());

      String sourceBranchName = "Source Branch" + GUID.create();
      String destinationBranchName = "Destination Branch" + GUID.create();

      sourceBranch =
            BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, sourceBranchName,
                  UserManager.getUser(SystemUser.OseeSystem));
      sleep(5000);

      sourceArtifact = ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, sourceBranch);
      sourceArtifact.persist();

      destinationBranch =
            BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, destinationBranchName,
                  UserManager.getUser(SystemUser.OseeSystem));

      sleep(5000);
   }

   @org.junit.Test
   public void testIntroduceCrossBranch() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      InterArtifactExplorerDropHandler dropHandler = new InterArtifactExplorerDropHandler();
      dropHandler.dropArtifactIntoDifferentBranch(
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(destinationBranch), new Artifact[] {sourceArtifact},
            false);

      sleep(5000);
      //Acquire the introduced artifact
      Artifact destArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact.getArtId(), destinationBranch);

      assertTrue(sourceArtifact.getName().equals(destArtifact.getName()));
   }

   public static void sleep(long milliseconds) throws Exception {
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Awake");
   }
   
   @After
   public void tearDown() throws Exception {
      if(sourceBranch != null){
         BranchManager.purgeBranch(sourceBranch);
      }
      if(destinationBranch != null){
         BranchManager.purgeBranch(destinationBranch);
      }
   }
}
