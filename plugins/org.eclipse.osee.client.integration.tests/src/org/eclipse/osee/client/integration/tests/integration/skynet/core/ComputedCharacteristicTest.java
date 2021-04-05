/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SafetySeverity;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SoftwareControlCategory;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SoftwareCriticalityIndex;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.demo.DemoOseeTypes;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Stephen J. Molaro
 */
public class ComputedCharacteristicTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(DemoChoice.OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private BranchToken workingBranch;
   private Artifact newArtifact;

   @Before
   public void setup() {
      workingBranch = BranchToken.create(testInfo.getQualifiedTestName());
      BranchManager.createWorkingBranch(DemoBranches.SAW_Bld_2, workingBranch);
      newArtifact =
         ArtifactTypeManager.addArtifact(DemoOseeTypes.DemoArtifactWithComputedCharacteristics, workingBranch);
   }

   @Test
   public void testCalculateComputedCharacteristics() {
      Assert.assertEquals(125, newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationSum));
      Assert.assertEquals(5.4, newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationProduct));
      Assert.assertEquals(217L, newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationAverage));
      Assert.assertEquals(4, newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationQuotient));
      Assert.assertEquals(2.1, newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationDelta));
      Assert.assertEquals(-1, newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationDivideByZero));
   }

   @Test(expected = OseeCoreException.class)
   public void testComputedCharacteristicsFailure() {
      newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationFailure);
   }

   @Test(expected = OseeCoreException.class)
   public void testComputedCharacteristicsInvalid() {
      newArtifact.getComputedCharacteristicValue(DemoOseeTypes.ComputationInvalid);
   }

   @Test
   public void testSoftwareCriticalityIndex() {
      Assert.assertEquals(SoftwareCriticalityIndex.Unspecified,
         newArtifact.getComputedCharacteristicValue(SoftwareCriticalityIndex));
      newArtifact.setSoleAttributeValue(SoftwareControlCategory, CoreAttributeTypes.SoftwareControlCategory._3Rft);
      newArtifact.setSoleAttributeValue(SafetySeverity, SafetySeverity.Marginal);
      Assert.assertEquals(SoftwareCriticalityIndex.SwCI4,
         newArtifact.getComputedCharacteristicValue(SoftwareCriticalityIndex));
   }

   @After
   public void tearDown() {
      if (workingBranch != null) {
         BranchManager.purgeBranch(workingBranch);
      }
   }
}