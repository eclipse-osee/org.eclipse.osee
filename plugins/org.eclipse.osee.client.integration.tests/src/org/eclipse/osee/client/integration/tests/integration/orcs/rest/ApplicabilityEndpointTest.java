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
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ViewDefinition;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
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
      OseeInfo.setValue("featuredefinition.use.json", "true");
      List<FeatureDefinition> fromJson = appl.getFeatureDefinitionData();
   }

   @Test
   public void testViewRestCalls() throws Exception {
      ApplicabilityEndpoint appl =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch);

      ViewDefinition updateView = appl.getView("Product A");
      ViewDefinition copyFromView = appl.getView("Product B");
      updateView.setCopyFrom(ArtifactId.valueOf(copyFromView.getId()));
      appl.updateView(updateView);

      ViewDefinition newView = new ViewDefinition();
      newView.setName("New View");
      newView.setCopyFrom(ArtifactId.valueOf(updateView.getId()));
      appl.createView(newView);

      appl.deleteView("Product D");

      List<ArtifactToken> viewList = appl.getViews();
      Assert.assertEquals(4, viewList.size());
      String viewTable = appl.getViewTable(null);
      Assert.assertFalse("ApplicabilityEndpoint.deleteView failure", viewTable.contains("Product D"));
      Assert.assertTrue("ApplicabilityEndpoint.createView failure", viewTable.contains(newView.getName()));

   }

   @Test
   public void testApplicabilityRestCalls() throws Exception {
      ApplicabilityEndpoint appl =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(DemoBranches.SAW_PL_Working_Branch);

      ViewDefinition view = appl.getView("Product A");
      XResultData createNewApp =
         appl.createApplicabilityForView(ArtifactId.valueOf(view.getId()), "ROBOT_ARM_LIGHT = Included");
      Assert.assertEquals("ApplicabilityEndpoint.createApplicabilityForView failure", createNewApp.getErrorCount(), 0);
      XResultData createInvalidApp =
         appl.createApplicabilityForView(ArtifactId.valueOf(view.getId()), "MADEUPFEATURE = Included");
      Assert.assertNotEquals("ApplicabilityEndpoint.createApplicabilityForView failure",
         createInvalidApp.getErrorCount(), 0);
      XResultData createMultiApp =
         appl.createApplicabilityForView(ArtifactId.valueOf(view.getId()), "ROBOT_SPEAKER = SPKR_C");
      Assert.assertEquals("ApplicabilityEndpoint.createApplicabilityForView with multiple values failure",
         createMultiApp.getErrorCount(), 0);
   }
}
