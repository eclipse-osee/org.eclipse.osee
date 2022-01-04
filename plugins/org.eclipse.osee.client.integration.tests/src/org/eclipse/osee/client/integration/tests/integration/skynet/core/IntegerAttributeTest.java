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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author David W. Miller
 */
public class IntegerAttributeTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private BranchToken workingBranch;

   @Before
   public void setup() {
      workingBranch = BranchToken.create(testInfo.getQualifiedTestName());
      BranchManager.createWorkingBranch(SAW_Bld_2, workingBranch);
   }

   @Test(expected = ClassCastException.class)
   public void testSetIntegerAttributeFail() {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Breaker, workingBranch);
      newArtifact.setSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, "50");
      TransactionId txId = newArtifact.persist(getClass().getSimpleName());
      List<AttributeId> attrIds = newArtifact.getAttributeIds(CoreAttributeTypes.CircuitBreakerId);
      String output = ServiceUtil.getOseeClient().loadAttributeValue(attrIds.get(0), txId, newArtifact);
      Assert.assertTrue(output.equals("50"));
   }

   @Test
   public void testSetIntegerAttributeSucceed() {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Breaker, workingBranch);
      newArtifact.setSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, 50);
      TransactionId txId = newArtifact.persist(getClass().getSimpleName());
      List<AttributeId> attrIds = newArtifact.getAttributeIds(CoreAttributeTypes.CircuitBreakerId);
      String output = ServiceUtil.getOseeClient().loadAttributeValue(attrIds.get(0), txId, newArtifact);
      Assert.assertTrue(output.equals("50"));
   }

   @After
   public void tearDown() {
      if (workingBranch != null) {
         BranchManager.purgeBranch(workingBranch);
      }
   }
}