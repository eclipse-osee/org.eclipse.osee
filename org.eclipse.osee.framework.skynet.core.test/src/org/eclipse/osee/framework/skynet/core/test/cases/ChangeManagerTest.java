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
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.junit.After;
import org.junit.Before;

/**
 * Tests the Change Manager.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeManagerTest {
   private static Artifact newArtifact;
   private static Artifact modArtifact;
   private Branch branch;

   @Before
   public void setUp() throws Exception {
      assertFalse("This test can not be run on Production", ClientSessionManager.isProductionDataStore());

      modArtifact =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, BranchManager.getSystemRootBranch());
      modArtifact.persistAttributes();

      sleep(5000);

      String branchName = "Change Manager Test Branch" + GUID.generateGuidStr();

      branch =
            BranchManager.createWorkingBranch(BranchManager.getSystemRootBranch(), branchName,
                  UserManager.getUser(SystemUser.OseeSystem));
      sleep(5000);

      newArtifact = ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch);
      newArtifact.persistAttributes();
      sleep(5000);
   }

   @org.junit.Test
   public void testChangeManager() throws Exception {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);

      sleep(5000);

      modArtifact = ArtifactQuery.getArtifactFromId(modArtifact.getArtId(), branch);

      assertTrue("Check artifact new", checkArtifactModType(newArtifact, ModificationType.NEW));
      newArtifact.setSoleAttributeFromString(WordAttribute.WORD_TEMPLATE_CONTENT, "new content");
      assertTrue("Check artifact is still new", checkArtifactModType(newArtifact, ModificationType.NEW));
      modArtifact.setSoleAttributeFromString(WordAttribute.WORD_TEMPLATE_CONTENT, "changed content");
      modArtifact.persistAttributes();
      assertTrue("Check artifact has changed", checkArtifactModType(modArtifact, ModificationType.MODIFIED));
   }

   @After
   public void tearDown() throws Exception {
      BranchManager.purgeBranch(branch);
      sleep(5000);

      modArtifact.persistAttributes();
   }

   public static boolean checkArtifactModType(Artifact artifact, ModificationType modificationType) throws OseeCoreException {
      boolean pass = false;
      for (Change change : ChangeManager.getChangesPerBranch(artifact.getBranch(), new EmptyMonitor())) {
         if (change.getArtId() == artifact.getArtId()) {
            pass = change.getModificationType() == modificationType;
            break;
         }
      }
      return pass;
   }

   public static void sleep(long milliseconds) throws Exception {
      OseeLog.log(ChangeManagerTest.class, Level.INFO, "Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      OseeLog.log(ChangeManagerTest.class, Level.INFO, "Awake");
   }
}
