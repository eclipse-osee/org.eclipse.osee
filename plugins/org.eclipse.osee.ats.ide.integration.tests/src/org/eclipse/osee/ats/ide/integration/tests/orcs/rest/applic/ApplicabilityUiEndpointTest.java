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

import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.orcs.rest.model.ApplicabilityUiEndpoint;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Case for {@link ApplicabilityUiEndpoint}
 *
 * @author Donald G. Dunne
 */
public class ApplicabilityUiEndpointTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Test
   public void testGet() throws Exception {
      ApplicabilityUiEndpoint applUiEndpoint = ServiceUtil.getOseeClient().getApplicabilityUiEndpoint();
      Assert.assertNotNull(applUiEndpoint);

      Response response = applUiEndpoint.get();
      String html = response.readEntity(String.class);
      Assert.assertTrue(html.contains("Configuration Matrix"));
   }

   @Test
   public void testGetApplicabilityBranches() throws Exception {
      ApplicabilityUiEndpoint applUiEndpoint = ServiceUtil.getOseeClient().getApplicabilityUiEndpoint();
      Assert.assertNotNull(applUiEndpoint);

      List<BranchId> applBranches = applUiEndpoint.getApplicabilityBranches();
      Assert.assertTrue("Should be at least 1 branches, was " + applBranches.size(), applBranches.size() > 1);
   }

   @Test
   public void testGetApplicabilityConfig() throws Exception {
      ApplicabilityUiEndpoint applUiEndpoint = ServiceUtil.getOseeClient().getApplicabilityUiEndpoint();
      Assert.assertNotNull(applUiEndpoint);

      ApplicabilityBranchConfig config = applUiEndpoint.getConfig(BranchId.valueOf(DemoBranches.SAW_PL.getId()), false);
      Assert.assertNotNull(config);

      Assert.assertEquals(4, config.getViews().size());
      Assert.assertEquals(DemoBranches.SAW_PL.getId(), config.getBranch().getId());

      config = applUiEndpoint.getConfig(BranchId.valueOf(DemoBranches.SAW_PL.getId()), true);
      Assert.assertNotNull(config);

   }

}
