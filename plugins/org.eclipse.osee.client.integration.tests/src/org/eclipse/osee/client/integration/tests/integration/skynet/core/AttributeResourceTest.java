/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.osee.framework.core.data.IOseeBranch;
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
public class AttributeResourceTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private IOseeBranch workingBranch;
   private final String testString = "This is the test string";

   @Before
   public void setup() {
      workingBranch = IOseeBranch.create(testInfo.getQualifiedTestName());
      BranchManager.createWorkingBranch(SAW_Bld_2, workingBranch);
   }

   @Test
   public void testGetArtifactFromGUIDDeleted() {
      Artifact newArtifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirementMsWord, workingBranch);
      newArtifact.setSoleAttributeFromString(CoreAttributeTypes.WordTemplateContent, String.format(
         "<w:p wsp:rsidR=\"006A3C0C\" wsp:rsidRDefault=\"006A3C0C\" wsp:rsidP=\"00E54E52\"><w:r><w:t>%s</w:t></w:r></w:p>",
         testString));
      TransactionId txId = newArtifact.persist(getClass().getSimpleName());
      List<Integer> attrIds = newArtifact.getAttributeIds(CoreAttributeTypes.WordTemplateContent);
      String output = ServiceUtil.getOseeClient().loadAttributeValue(attrIds.get(0), txId, newArtifact);
      Assert.assertTrue(output.trim().equals(testString));
   }

   @After
   public void tearDown() {
      if (workingBranch != null) {
         BranchManager.purgeBranch(workingBranch);
      }
   }
}