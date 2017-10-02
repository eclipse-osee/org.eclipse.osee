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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the Change Manager.
 *
 * @author Jeff C. Phillips
 */
public class ChangeManagerTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static Artifact newArtifact;
   private static Artifact modArtifact;
   private IOseeBranch branch;

   @Before
   public void setUp() throws Exception {
      assertFalse("This test can not be run on Production", ClientSessionManager.isProductionDataStore());

      modArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, CoreBranches.SYSTEM_ROOT);
      modArtifact.persist(getClass().getSimpleName());

      String branchName = "Change Manager Test Branch" + GUID.create();

      branch = BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, branchName);

      newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, branch);
      newArtifact.persist(getClass().getSimpleName());
      BranchManager.refreshBranches();
   }

   @org.junit.Test
   public void testChangeManager() throws Exception {
      modArtifact = ArtifactQuery.getArtifactFromId(modArtifact, branch);

      assertTrue("Check artifact new", checkArtifactModType(newArtifact, ModificationType.NEW));
      newArtifact.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent, "new content");
      assertTrue("Check artifact is still new", checkArtifactModType(newArtifact, ModificationType.NEW));
      modArtifact.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent, "changed content");
      modArtifact.persist(getClass().getSimpleName());
      assertTrue("Check artifact has changed", checkArtifactModType(modArtifact, ModificationType.NEW));
   }

   @Ignore
   @Test
   public void testArtifactTypeChange() {
      // TODO Add artifact type change
   }

   @After
   public void tearDown() throws Exception {
      modArtifact.persist(getClass().getSimpleName());
      BranchManager.purgeBranch(branch);
   }

   public static boolean checkArtifactModType(Artifact artifact, ModificationType modificationType)  {
      boolean pass = false;
      Collection<Change> changes = new ArrayList<>();
      IOperation operation = ChangeManager.comparedToParent(artifact.getBranch(), changes);
      Operations.executeWorkAndCheckStatus(operation);

      for (Change change : changes) {
         if (change.getArtId().getId().intValue() == artifact.getArtId()) {
            pass = change.getModificationType() == modificationType;
            break;
         }
      }
      return pass;
   }
}