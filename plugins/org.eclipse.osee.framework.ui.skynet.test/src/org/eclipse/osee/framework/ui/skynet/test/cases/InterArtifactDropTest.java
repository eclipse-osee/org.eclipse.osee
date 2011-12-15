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
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandlerOperation;
import org.junit.Before;

/**
 * Tests cross branch drag and drop.
 * 
 * @author Jeff C. Phillips
 */
public class InterArtifactDropTest {
   private static Artifact sourceArtifact;
   private Branch sourceBranch;
   private Branch destinationBranch;
   private Branch updateTestParentSourceBranch;
   private Branch updateTestSourceBranch;

   @Before
   public void setUp() throws Exception {
      assertFalse("This test can not be run on Production", ClientSessionManager.isProductionDataStore());

      String sourceBranchName = "Source Branch" + GUID.create();
      String destinationBranchName = "Destination Branch" + GUID.create();
      String updateSourceBranchName = "updateTestParentSourceBranch" + GUID.create();
      String updateTestSourceName = "updateTestSourceBranch" + GUID.create();

      sourceBranch =
         BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, sourceBranchName,
            UserManager.getUser(SystemUser.OseeSystem));
      sleep(5000);

      sourceArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, sourceBranch);
      sourceArtifact.persist(getClass().getSimpleName());

      destinationBranch =
         BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, destinationBranchName,
            UserManager.getUser(SystemUser.OseeSystem));

      updateTestParentSourceBranch =
         BranchManager.createWorkingBranch(sourceBranch, updateSourceBranchName,
            UserManager.getUser(SystemUser.OseeSystem));

      updateTestSourceBranch =
         BranchManager.createWorkingBranch(updateTestParentSourceBranch, updateTestSourceName,
            UserManager.getUser(SystemUser.OseeSystem));

      sleep(5000);
   }

   @org.junit.Test
   public void testIntroduceCrossBranch() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(destinationBranch), new Artifact[] {sourceArtifact},
            false);
      Operations.executeWork(dropHandler);
      sleep(5000);
      //Acquire the introduced artifact
      Artifact destArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact.getArtId(), destinationBranch);

      assertTrue(sourceArtifact.getName().equals(destArtifact.getName()));
   }

   @org.junit.Test
   public void testUpdateBranch() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      Artifact updateTestArtifact = ArtifactQuery.getArtifactFromId(sourceArtifact.getArtId(), updateTestSourceBranch);
      updateTestArtifact.setName("I am an update branch test");
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(updateTestSourceBranch);
      root.addChild(updateTestArtifact);
      updateTestArtifact.persist(getClass().getSimpleName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(sourceArtifact, new Artifact[] {updateTestArtifact}, false);
      Operations.executeWork(dropHandler);

      sleep(5000);
      //Acquire the updated artifact
      Artifact destArtifact =
         ArtifactQuery.getArtifactFromId(updateTestArtifact.getArtId(), sourceArtifact.getBranch());
      destArtifact.reloadAttributesAndRelations();
      assertTrue(updateTestArtifact.getName().equals(destArtifact.getName()));
   }

   public static void sleep(long milliseconds) throws Exception {
      Thread.sleep(milliseconds);
   }

}
