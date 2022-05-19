/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

//import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BlockApplicabilityStageRequest;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Case for {@link ApplicabilityEndpoint}
 *
 * @author Donald G. Dunne
 */
public class ApplicabilityEndpointTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Test
   public void testcreateDemoApplicability() throws Exception {
      Assert.assertEquals(1,
         ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.FeatureDefinition, DemoBranches.SAW_PL).size());
      Assert.assertEquals(4,
         ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.Feature, DemoBranches.SAW_PL).size());
      Assert.assertEquals(4,
         ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.BranchView, DemoBranches.SAW_PL).size());
   }

   @Test
   public void testFeatureRestCalls() throws Exception {

      ApplicabilityEndpoint appl =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch);

      FeatureDefinition updateFd = appl.getFeature("ROBOT_SPEAKER");
      updateFd.setDefaultValue("SPKR_B");
      appl.updateFeature(updateFd);

      FeatureDefinition newFeature = new FeatureDefinition();
      newFeature.setName("TESTFEATURE1");
      newFeature.setDescription("description");
      newFeature.setDefaultValue("Included");
      newFeature.setValues(Arrays.asList(new String[] {"Included", "Excluded"}));
      newFeature.setType("single");
      newFeature.setValueType("String");
      appl.createFeature(newFeature);
      FeatureDefinition deleteFeature = appl.getFeature("ROBOT_ARM_LIGHT");

      appl.deleteFeature(ArtifactId.valueOf(deleteFeature.getId()));

      List<FeatureDefinition> fList = appl.getFeatureDefinitionData();
      Assert.assertEquals(4, fList.size());
      Assert.assertFalse("ApplicabilityEndpoing.deleteFeature failure", fList.contains(newFeature));
      Assert.assertTrue("ApplicabilityEndpoint.addFeature failure", fList.contains(appl.getFeature("TESTFEATURE1")));
      FeatureDefinition checkUpdatedFeature = appl.getFeature("ROBOT_SPEAKER");
      Assert.assertTrue("ApplicabilityEntpoint.updateFeature failure",
         checkUpdatedFeature.getDefaultValue().equals("SPKR_B"));
   }

   @Test
   public void testViewRestCalls() throws Exception {
      ApplicabilityEndpoint appl =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch);
      int viewCount = appl.getViews().size();
      CreateViewDefinition updateView = appl.getView("Product A");
      CreateViewDefinition copyFromView = appl.getView("Product B");
      updateView.setCopyFrom(ArtifactId.valueOf(copyFromView.getId()));
      appl.updateView(updateView);

      CreateViewDefinition newView = new CreateViewDefinition();
      newView.setName("New View");
      newView.setCopyFrom(ArtifactId.valueOf(updateView.getId()));
      List<String> pApps = new ArrayList<>();
      pApps.add("Code");
      pApps.add("Test");
      newView.setProductApplicabilities(pApps);
      appl.createView(newView);

      appl.deleteView("Product D");

      List<ArtifactToken> viewList = appl.getViews();
      Assert.assertEquals(viewCount, viewList.size());
      String viewTable = appl.getViewTable(null);
      Assert.assertFalse("ApplicabilityEndpoint.deleteView failure", viewTable.contains("Product D"));
      Assert.assertTrue("ApplicabilityEndpoint.createView failure", viewTable.contains(newView.getName()));

      ConfigurationGroupDefinition group = new ConfigurationGroupDefinition();
      group.setName("abGrp");
      List<String> cfgs = new ArrayList<>();
      cfgs.add(updateView.getIdString());
      cfgs.add(copyFromView.getIdString());
      appl.createCfgGroup(group);
      appl.syncCfgGroup();
      viewList = appl.getViews();
      Assert.assertEquals(viewCount + 1, viewList.size());
   }

   @Test
   public void testApplicabilityRestCalls() throws Exception {
      ApplicabilityEndpoint appl =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch);
      ArtifactEndpoint artifactEndpoint =
         ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL_Working_Branch);

      CreateViewDefinition view = appl.getView("Product A");
      XResultData createNewApp =
         appl.createApplicabilityForView(ArtifactId.valueOf(view.getId()), "ROBOT_ARM_LIGHT = Included");
      Assert.assertEquals(createNewApp.getErrorCount(), 0);
      XResultData createInvalidApp =
         appl.createApplicabilityForView(ArtifactId.valueOf(view.getId()), "MADEUPFEATURE = Included");
      Assert.assertNotEquals(createInvalidApp.getErrorCount(), 0);
      XResultData createMultiApp =
         appl.createApplicabilityForView(ArtifactId.valueOf(view.getId()), "ROBOT_SPEAKER = SPKR_C");
      Assert.assertEquals(createMultiApp.getErrorCount(), 0);
      Collection<ApplicabilityToken> appTokens = appl.getApplicabilityTokens();
      ApplicabilityToken robotIncluded = appTokens.stream().filter(
         appToken -> "ROBOT_ARM_LIGHT = Included".equals(appToken.getName())).findAny().orElse(
            ApplicabilityToken.SENTINEL);
      Assert.assertTrue(robotIncluded.isValid());
      ArtifactToken newArtifact = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.Folder, CoreArtifactTokens.DefaultHierarchyRoot, "TestArtifact");
      appl.setApplicability(robotIncluded, Collections.singletonList(newArtifact));
      ApplicabilityToken testAppToken = appl.getApplicabilityToken(newArtifact);
      Assert.assertTrue(testAppToken.equals(robotIncluded));
      artifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, newArtifact);
   }

   /**
    * This test runs the BlockVisibility rest call on a set of files, executing the BlockApplicabilityTool. Most files
    * have applicability tags meant for processing and are to be tested against their expected output. The rest call is
    * ran twice, once with comments turned on, then again without comments and using the saved off cache file.
    *
    * @throws Exception
    */
   @Test
   public void testBlockVisibility() throws Exception {
      ApplicabilityEndpoint appl = ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL);

      // Get Starting Paths
      String inputPath = OsgiUtil.getResourceAsUrl(ApplicabilityEndpointTest.class,
         "/support/BlockApplicabilityTest/InputFiles").getPath();
      String stagePath = OseeData.getPath().toString();

      // First testing by commenting instead of removing all non-applicable code
      ArtifactId productA =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.BranchView, "Product A", DemoBranches.SAW_PL);
      Map<Long, String> views = new HashMap<>();
      views.put(productA.getId(), "");
      BlockApplicabilityStageRequest blockApplicabilityData =
         new BlockApplicabilityStageRequest(views, true, inputPath, stagePath);
      appl.applyBlockVisibility(blockApplicabilityData);

      // Traversing the Staging Folders checking to see if each creation was successful
      File stagingFolder = new File(stagePath, "Staging");
      assertTrue(stagingFolder.exists());
      File viewFolder = new File(stagingFolder, "Product_A");
      assertTrue(viewFolder.exists());
      File inputFolder = new File(viewFolder, "InputFiles");
      assertTrue(inputFolder.exists());

      // Checking that readme.txt exists
      File readmeFile = new File(inputFolder, "readme.txt");
      assertTrue(readmeFile.exists());

      // Checking Code Folder
      File codeFolder = new File(inputFolder, "Code");
      assertTrue(codeFolder.exists());

      // Testing CppTest.cpp against CppTestComments.cpp
      File cppFile = new File(codeFolder, "CppTest.cpp");
      assertTrue(cppFile.exists());
      String actualOutput = Lib.fileToString(cppFile);
      String expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/CppTestComments.cpp");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing JavaTestWithTags.java against JavaTestWithTagsComments.java
      File javaFile = new File(codeFolder, "JavaTestWithTags.java");
      assertTrue(javaFile.exists());
      actualOutput = Lib.fileToString(javaFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/JavaTestWithTagsComments.java");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing JavaTestWithoutTags.java against the original file
      File javaFileWithoutTags = new File(codeFolder, "JavaTestWithoutTags.java");
      assertTrue(javaFileWithoutTags.exists());
      actualOutput = Lib.fileToString(javaFileWithoutTags);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/InputFiles/Code/JavaTestWithoutTags.java");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing that CppTest_Exclude.cpp was excluded
      File excludedCppFile = new File(codeFolder, "CppTest_Exclude.cpp");
      assertFalse(excludedCppFile.exists());

      // Adding in a fake directory that should be deleted in future refresh test
      File fakeDir = new File(codeFolder, "Fake Directory");
      fakeDir.mkdir();

      // Check Resources Folder
      File resourcesFolder = new File(inputFolder, "Resources");
      assertTrue(resourcesFolder.exists());

      // Testing CMDTest.cmd against CMDTestComments.cmd
      File cmdFile = new File(resourcesFolder, "CMDTest.cmd");
      assertTrue(cmdFile.exists());
      actualOutput = Lib.fileToString(cmdFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/CMDTestComments.cmd");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing LSTTest.lst against LSTTestComments.lst
      File lstFile = new File(resourcesFolder, "LSTTest.lst");
      assertTrue(lstFile.exists());
      actualOutput = Lib.fileToString(lstFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/LSTTestComments.lst");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing MDGTest.mdgsource against MDGTestComments.mdgsource
      File mdgFile = new File(resourcesFolder, "MDGTest.mdgsource");
      assertTrue(mdgFile.exists());
      actualOutput = Lib.fileToString(mdgFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/MDGTestComments.mdgsource");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing TestXml.xml against TestXmlComments.xml
      File xmlFile = new File(resourcesFolder, "TestXml.xml");
      assertTrue(xmlFile.exists());
      actualOutput = Lib.fileToString(xmlFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/TestXmlComments.xml");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing that TestXml_Exclude.xml was excluded
      File excludedXmlFile = new File(codeFolder, "TestXml_Exclude.xml");
      assertFalse(excludedXmlFile.exists());

      // Testing VMFTest.VMF against VMFTestComments.VMF
      File vmfFile = new File(resourcesFolder, "VMFTest.VMF");
      assertTrue(vmfFile.exists());
      actualOutput = Lib.fileToString(vmfFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/VMFTestComments.VMF");
      assertTrue(actualOutput.equals(expectedOutput));

      // Acquiring the .applicabilityCache file for next test
      File cacheFile = new File(viewFolder, ".applicabilityCache");
      assertTrue(cacheFile.exists());

      // Set the cache file and turn off commenting for the next test
      views.put(productA.getId(), cacheFile.getAbsolutePath());
      blockApplicabilityData.setViews(views);
      blockApplicabilityData.setCommentNonApplicableBlocks(false);
      appl.applyBlockVisibility(blockApplicabilityData);

      // Testing CppTest.cpp against expected CppTest.cpp output
      cppFile = new File(codeFolder, "CppTest.cpp");
      assertTrue(cppFile.exists());
      actualOutput = Lib.fileToString(cppFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/CppTest.cpp");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing JavaTestWithTags.java against expected JavaTestWithTags.java
      javaFile = new File(codeFolder, "JavaTestWithTags.java");
      assertTrue(javaFile.exists());
      actualOutput = Lib.fileToString(javaFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/JavaTestWithTags.java");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing the fake directory from earlier to make sure it was deleted on refresh
      fakeDir = new File(codeFolder, "Fake Directory");
      assertFalse(fakeDir.exists());

      // Testing CMDTest.cmd against expected CMDTest.cmd output
      cmdFile = new File(resourcesFolder, "CMDTest.cmd");
      assertTrue(cmdFile.exists());
      actualOutput = Lib.fileToString(cmdFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/CMDTest.cmd");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing LSTTest.lst against expected LSTTest.lst output
      lstFile = new File(resourcesFolder, "LSTTest.lst");
      assertTrue(lstFile.exists());
      actualOutput = Lib.fileToString(lstFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/LSTTest.lst");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing MDGTest.mdgsource against expected MDGTest.mdgsource output
      mdgFile = new File(resourcesFolder, "MDGTest.mdgsource");
      assertTrue(mdgFile.exists());
      actualOutput = Lib.fileToString(mdgFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/MDGTest.mdgsource");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing TestXml.xml against expected TestXml.xml output
      xmlFile = new File(resourcesFolder, "TestXml.xml");
      assertTrue(xmlFile.exists());
      actualOutput = Lib.fileToString(xmlFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/TestXml.xml");
      assertTrue(actualOutput.equals(expectedOutput));

      // Testing VMFTest.VMF against expected VMFTest.VMF output
      vmfFile = new File(resourcesFolder, "VMFTest.VMF");
      assertTrue(vmfFile.exists());
      actualOutput = Lib.fileToString(vmfFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/VMFTest.VMF");

      // Deleting all of the staging files created in the workspace
      Files.walk(stagingFolder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
   }
}
