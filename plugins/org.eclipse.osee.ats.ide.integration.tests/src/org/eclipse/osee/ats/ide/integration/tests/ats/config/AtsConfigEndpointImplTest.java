/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.column.AtsCoreAttrTokColumnToken;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for {@link AtsConfigEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsConfigEndpointImplTest {

   private AtsApiIde atsApi;
   private AtsConfigEndpointApi configEp;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      configEp = atsApi.getServerEndpoints().getConfigEndpoint();
   }

   @org.junit.Test
   public void testGenAttrTypeViews() {
      List<AtsCoreAttrTokColumnToken> generateAttrTypeViews = configEp.generateAttrTypeViews();
      Assert.assertNotNull(generateAttrTypeViews);
      Assert.assertTrue(generateAttrTypeViews.size() > 0);
   }

   @org.junit.Test
   public void testClearCache() {
      configEp.requestCacheReload();
      AtsConfigurations configs = configEp.getWithPend();
      Assert.assertNotNull(configs);
      Assert.assertTrue(configs.getIdToTeamDef().size() > 5);
   }

   @org.junit.Test
   public void testImage() {
      Collection<ArtifactImage> artifactImages = configEp.getArtifactImages();
      Assert.assertTrue(artifactImages.size() > 0);
   }

   @org.junit.Test
   public void testAlive() {
      XResultData resultData = configEp.alive();
      Assert.assertEquals("Alive", resultData.getResults().iterator().next());
   }

   @org.junit.Test
   public void testVersion() {
      Version ver = configEp.getVersion(DemoArtifactToken.SAW_PL_SBVT1);
      Assert.assertEquals(ver.getId(), DemoArtifactToken.SAW_PL_SBVT1.getId());
   }

   @Test
   public void testKeyValue() {
      String value = "This is the one line test";
      atsApi.setConfigValue("Singleline", value);
      atsApi.reloadServerAndClientCaches();
      String configValue = atsApi.getConfigValue("Singleline");
      Assert.assertEquals(value, configValue);

      value = "This is the multi-line test \n Second line \n Third line";
      atsApi.setConfigValue("Multiline", value);
      atsApi.reloadServerAndClientCaches();
      configValue = atsApi.getConfigValue("Multiline");
      Assert.assertEquals(value, configValue);
   }

   @Test
   public void testBranch() {
      AtsTestUtil.cleanupAndReset(getClass().getName() + "testBranch");
      IAtsTeamWorkflow teamWf = AtsTestUtil.getTeamWf();
      BranchToken parentBranch = atsApi.getBranchService().getBranch(DemoBranches.SAW_PL_Hardening_Branch);
      BranchData bd = new BranchData();
      bd.setParent(parentBranch);
      bd.setAssociatedArt(teamWf.getArtifactToken());
      bd.setAuthor(atsApi.getUserService().getCurrentUser().getArtifactToken());
      bd.setCreationComment(String.format("New Baseline Branch from %s", parentBranch.toStringWithId()));
      bd.setBranchType(BranchType.WORKING);
      bd.setBranchName("New Test Branch from " + teamWf.toStringWithAtsId());
      BranchData createBranch = configEp.createBranch(bd);
      Assert.assertTrue(createBranch.getNewBranch().isValid());
      BranchManager.purgeBranch(createBranch.getNewBranch());
      AtsTestUtil.cleanup();
   }

   @Test
   public void testBranchViews() {
      ArtifactToken verArt = atsApi.getQueryService().getArtifact(DemoArtifactToken.SAW_PL_Hardening_Branch);
      Assert.assertTrue(verArt.isValid());

      IAtsVersion version = atsApi.getVersionService().getVersion(verArt);
      Collection<ArtifactToken> branchViews = atsApi.getBranchService().getBranchViews(version);
      Assert.assertEquals(6, branchViews.size());

      ArtifactToken branchView = atsApi.getBranchService().getBranchView(version);
      Assert.assertTrue(branchView.isInvalid());

      TransactionToken tx = atsApi.getBranchService().setBranchView(version, branchViews.iterator().next());
      Assert.assertTrue(tx.isValid());

      ((Artifact) verArt).reloadAttributesAndRelations();
      version = atsApi.getVersionService().getVersion(verArt);
      branchView = atsApi.getBranchService().getBranchView(version);
      Assert.assertTrue(branchView.isValid());
      Assert.assertTrue(Strings.isValid(branchView.getName()));
   }

   @Test
   public void testGetTeamDefinitionsDefaultsToActive() {
      List<NamedIdBase> teamDefs = configEp.getTeamDefinitions("Active");
      Assert.assertFalse(teamDefs.isEmpty());
      Assert.assertTrue("Top Team Definition should be active",
         teamDefs.stream().anyMatch(t -> t.getId().equals(AtsArtifactToken.TopTeamDefinition.getId())));
      Assert.assertEquals(teamDefs.size(), configEp.getTeamDefinitions(null).size());
   }

   @Test
   public void testGetTeamDefinitionsSortedByNameIgnoringCase() {
      List<NamedIdBase> teamDefs = configEp.getTeamDefinitions("Both");
      for (int i = 1; i < teamDefs.size(); i++) {
         Assert.assertTrue(
            String.format("[%s] should not come before [%s]", teamDefs.get(i).getName(), teamDefs.get(i - 1).getName()),
            String.CASE_INSENSITIVE_ORDER.compare(teamDefs.get(i - 1).getName(), teamDefs.get(i).getName()) <= 0);
      }
   }

   @Test
   public void testGetTeamDefinitionsActiveFilterIsCaseInsensitiveAndBothIsSuperset() {
      List<NamedIdBase> active = configEp.getTeamDefinitions("active");
      List<NamedIdBase> inActive = configEp.getTeamDefinitions("INACTIVE");
      List<NamedIdBase> both = configEp.getTeamDefinitions("Both");

      Assert.assertEquals(active.size() + inActive.size(), both.size());
      Assert.assertTrue(both.containsAll(active));
      Assert.assertTrue(both.containsAll(inActive));
      for (NamedIdBase teamDef : inActive) {
         Assert.assertFalse(active.contains(teamDef));
      }
   }

   @Test
   public void testGetTeamDefinitionsInvalidActiveFails() {
      try {
         configEp.getTeamDefinitions("Bogus");
         Assert.fail("Expected failure for invalid active value");
      } catch (Exception ex) {
         // expected: server rejects value that is not Active, InActive or Both
         Assert.assertNotNull(ex);
      }
   }

   @Test
   public void testCreateVersion() {
      Version createVersion = atsApi.getVersionService().createVersion("Test Version", "Test Version Description",
         AtsArtifactToken.TopTeamDefinition, BranchId.SENTINEL);
      Assert.assertTrue(createVersion.isValid());
   }

}