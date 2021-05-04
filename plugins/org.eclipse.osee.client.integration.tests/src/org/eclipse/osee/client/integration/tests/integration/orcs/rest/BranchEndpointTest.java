/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.JsonArtifact;
import org.eclipse.osee.framework.core.data.JsonRelations;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Audrey Denk
 */
public class BranchEndpointTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   private static BranchEndpoint branchEndpoint;

   @BeforeClass
   public static void testSetup() {
      OseeClient oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      branchEndpoint = oseeclient.getBranchEndpoint();
   }

   @Test
   public void getTxs() {
      List<Branch> baselineBranches = branchEndpoint.getBaselineBranches();
      Assert.assertFalse(baselineBranches.isEmpty());
   }

   @Test
   public void getBranches() {
      List<Branch> baselineBranches = branchEndpoint.getBranches("", "", "", false, false, "", "", null, null);
      List<BranchId> branchIds = new ArrayList<>();
      for (Branch branch : baselineBranches) {
         branchIds.add(BranchId.valueOf(branch.getId()));
      }
      List<BranchId> originalBranches = new ArrayList<>();
      //Selecting branches from CreateDemoBranches.java
      originalBranches.add(CoreBranches.COMMON);
      originalBranches.add(DemoBranches.CIS_Bld_1);
      originalBranches.add(DemoBranches.SAW_Bld_1);
      originalBranches.add(DemoBranches.SAW_PL);
      Assert.assertFalse(baselineBranches.isEmpty());
      boolean allBranchesContained = true;
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertTrue(allBranchesContained);
      originalBranches.add(BranchId.SENTINEL);
      for (BranchId branchID : originalBranches) {

         if (!branchIds.contains(branchID)) {
            allBranchesContained = false;
         }
      }
      Assert.assertFalse(allBranchesContained);

   }

   @Test
   public void getArtifactDetailsByTypeWithView() {
      List<JsonArtifact> artifacts =
         branchEndpoint.getArtifactDetailsByType(DemoBranches.SAW_Bld_1, ArtifactId.SENTINEL, "23");
      Assert.assertFalse(artifacts.isEmpty());
   }

   @Test
   public void getArtifactDetailsByType() {
      List<JsonArtifact> artifacts = branchEndpoint.getArtifactDetailsByType(DemoBranches.SAW_Bld_1, "23");
      Assert.assertFalse(artifacts.isEmpty());
   }

   @Test
   public void getRelationsByType() {
      JsonRelations relations =
         branchEndpoint.getRelationsByType(CoreBranches.COMMON, CoreRelationTypes.SupportingInfo.getIdString());
      Assert.assertTrue(relations.getRelations().isEmpty());

      User joe = UserManager.getUser();
      Artifact folder = ArtifactQuery.getArtifactFromId(CoreArtifactTokens.OseeConfiguration, CoreBranches.COMMON);
      joe.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, folder);
      joe.persist(getClass().getSimpleName());

      relations =
         branchEndpoint.getRelationsByType(CoreBranches.COMMON, CoreRelationTypes.SupportingInfo.getIdString());
      Assert.assertEquals(1, relations.getRelations().size());
      Assert.assertEquals(joe.getIdString(), relations.getRelations().iterator().next().getArtA());
      Assert.assertEquals(joe.getName(), relations.getRelations().iterator().next().getArtAName());
      Assert.assertEquals(folder.getIdString(), relations.getRelations().iterator().next().getArtB());
      Assert.assertEquals(folder.getName(), relations.getRelations().iterator().next().getArtBName());
   }

}