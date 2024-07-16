/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
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
import org.junit.rules.MethodRule;

/**
 * Tests the Change Manager.
 *
 * @author Jeff C. Phillips
 */
public class ChangeManagerTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static Artifact newArtifact;
   private static Artifact modArtifact;
   private BranchToken branch;

   @Before
   public void setUp() throws Exception {
      assertFalse("This test can not be run on Production", ClientSessionManager.isProductionDataStore());

      modArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, CoreBranches.SYSTEM_ROOT);
      modArtifact.persist(getClass().getSimpleName());

      String branchName = "Change Manager Test Branch" + GUID.create();

      branch = BranchManager.createWorkingBranch(CoreBranches.SYSTEM_ROOT, branchName);

      newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, branch);
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

   public static boolean checkArtifactModType(Artifact artifact, ModificationType modificationType) {
      boolean pass = false;
      Collection<Change> changes = new ArrayList<>();
      IOperation operation = ChangeManager.comparedToParent(artifact.getBranch(), changes);
      Operations.executeWorkAndCheckStatus(operation);

      for (Change change : changes) {
         if (change.getArtId().equals(artifact)) {
            ModificationType type = change.getModificationType();
            if (type != ModificationType.APPLICABILITY) { // skip if applicability change
               if (type == modificationType) {
                  pass = true;
               }
            }
         }
      }
      return pass;
   }
}