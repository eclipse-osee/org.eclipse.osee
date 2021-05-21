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

package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
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
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
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
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

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
    * This test runs the blockVisibility rest call on a set of files, some having applicability and some not. The files
    * that have applicability need to be tested against their expected output, as the tags need to be removed from the
    * new file. The files that do not have detected applicability changes need to be a link to the original location,
    * which is just a check that the file is the same in each location.
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

      ArtifactId productA =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.BranchView, "Product A", DemoBranches.SAW_PL);
      BlockApplicabilityStageRequest data = new BlockApplicabilityStageRequest(productA, false, inputPath, stagePath);

      appl.applyBlockVisibility(data);

      // Traversing the Staging Folders checking to see if each creation was successful
      File stagingFolder = new File(stagePath, "Staging");
      assertTrue(stagingFolder.exists());

      File viewFolder = new File(stagingFolder, "Product A");
      assertTrue(viewFolder.exists());

      File inputFolder = new File(viewFolder, "InputFiles");
      assertTrue(inputFolder.exists());

      File codeFolder = new File(inputFolder, "Code");
      assertTrue(codeFolder.exists());

      // Testing the Cpp file with applicability, delete when done
      File cppFile = new File(codeFolder, "TestCpp.cpp");
      assertTrue(cppFile.exists());
      String actualOutput = Lib.fileToString(cppFile);
      String expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/TestCpp.cpp");
      assertTrue(actualOutput.equals(expectedOutput));
      cppFile.delete();

      // Testing the Java file with applicability
      File javaFile = new File(codeFolder, "TestJava.java");
      assertTrue(javaFile.exists());
      actualOutput = Lib.fileToString(javaFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/TestJava.java");
      assertTrue(actualOutput.equals(expectedOutput));
      javaFile.delete();

      // Testing that the java file included in a config file was properly excluded
      File excludedJavaFile = new File(codeFolder, "TestExcludedJava.java");
      assertFalse(excludedJavaFile.exists());
      codeFolder.delete();

      File resourcesFolder = new File(inputFolder, "Resources");
      assertTrue(resourcesFolder.exists());

      // Test that the icon exists and is equal to the input file, delete when done
      File iconFile = new File(resourcesFolder, "TestIcon.ico");
      assertTrue(iconFile.exists());
      actualOutput = Lib.fileToString(iconFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/InputFiles/Resources/TestIcon.ico");
      assertTrue(actualOutput.equals(expectedOutput));
      iconFile.delete();

      // Test that the unchanged Xml file exists and is equal to the input file, delete when done
      File noChangeXmlFile = new File(resourcesFolder, "TestXmlNoChange.xml");
      assertTrue(noChangeXmlFile.exists());
      actualOutput = Lib.fileToString(noChangeXmlFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/InputFiles/Resources/TestXmlNoChange.xml");
      assertTrue(actualOutput.equals(expectedOutput));
      noChangeXmlFile.delete();

      // Testing the Xml file with applicability, delete when done along with resources folder
      File xmlFile = new File(resourcesFolder, "TestXml.xml");
      assertTrue(xmlFile.exists());
      actualOutput = Lib.fileToString(xmlFile);
      expectedOutput = OsgiUtil.getResourceAsString(ApplicabilityEndpointTest.class,
         "support/BlockApplicabilityTest/ExpectedOutputs/TestXml.xml");
      assertTrue(actualOutput.equals(expectedOutput));
      xmlFile.delete();
      resourcesFolder.delete();

      // Checking that the readMe file still exists after processing. An out of scope config file tried to exclude this file.
      File readMeFile = new File(inputFolder, "readMeTest.txt");
      assertTrue(readMeFile.exists());
      readMeFile.delete();

      // Cleaning up the remaining folders in the directory
      inputFolder.delete();
      viewFolder.delete();
      stagingFolder.delete();
   }
}
