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
import java.util.List;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.JsonArtifact;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
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
   public void getArtifactDetailsByType() {
      List<JsonArtifact> artifacts =
         branchEndpoint.getArtifactDetailsByType(DemoBranches.SAW_Bld_1, ArtifactId.SENTINEL, "23");
      Assert.assertFalse(artifacts.isEmpty());
   }

}