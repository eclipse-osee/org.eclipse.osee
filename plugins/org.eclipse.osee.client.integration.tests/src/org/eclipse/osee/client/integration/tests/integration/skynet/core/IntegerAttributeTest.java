/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
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

   private IOseeBranch workingBranch;

   @Before
   public void setup()  {
      workingBranch = IOseeBranch.create(testInfo.getQualifiedTestName());
      BranchManager.createWorkingBranch(SAW_Bld_2, workingBranch);
   }

   @Test(expected = ClassCastException.class)
   public void testSetIntegerAttributeFail()  {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Breaker, workingBranch);
      newArtifact.setSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, "50");
      TransactionId txId = newArtifact.persist(getClass().getSimpleName());
      List<Integer> attrIds = newArtifact.getAttributeIds(CoreAttributeTypes.CircuitBreakerId);
      String output = loadAttributeValue(attrIds.get(0), txId, newArtifact);
      Assert.assertTrue(output.equals("50"));
   }

   @Test
   public void testSetIntegerAttributeSucceed()  {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Breaker, workingBranch);
      newArtifact.setSoleAttributeValue(CoreAttributeTypes.CircuitBreakerId, 50);
      TransactionId txId = newArtifact.persist(getClass().getSimpleName());
      List<Integer> attrIds = newArtifact.getAttributeIds(CoreAttributeTypes.CircuitBreakerId);
      String output = loadAttributeValue(attrIds.get(0), txId, newArtifact);
      Assert.assertTrue(output.equals("50"));
   }

   @After
   public void tearDown() {
      if (workingBranch != null) {
         BranchManager.purgeBranch(workingBranch);
      }
   }

   private String loadAttributeValue(int attrId, TransactionId transactionId, Artifact artifact) {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("orcs").path("branch").path(artifact.getBranch().getIdString()).path(
         "artifact").path(artifact.getIdString()).path("attribute").path(String.valueOf(attrId)).path("version").path(
            String.valueOf(transactionId)).path("text").build();
      try {
         return JaxRsClient.newClient().target(uri).request(MediaType.TEXT_PLAIN).get(String.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}
