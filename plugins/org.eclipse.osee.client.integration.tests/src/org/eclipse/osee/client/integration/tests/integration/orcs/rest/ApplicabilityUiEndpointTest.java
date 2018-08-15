/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
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
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Test
   public void testGetApplicabilityBranches() throws Exception {
      ApplicabilityUiEndpoint applUiEndpoint = ServiceUtil.getOseeClient().getApplicabilityUiEndpoint();
      Assert.assertNotNull(applUiEndpoint);
      List<BranchViewToken> applBranches = applUiEndpoint.getApplicabilityBranches();
      Assert.assertTrue("Should be at least 1 branches, was " + applBranches.size(), applBranches.size() == 1);
   }

   @Test
   public void testGetApplicabilityConfig() throws Exception {
      ApplicabilityUiEndpoint applUiEndpoint = ServiceUtil.getOseeClient().getApplicabilityUiEndpoint();
      Assert.assertNotNull(applUiEndpoint);

      ApplicabilityBranchConfig config = applUiEndpoint.getConfig(BranchId.valueOf(DemoBranches.SAW_PL.getId()));
      Assert.assertNotNull(config);

      Assert.assertEquals(4, config.getFeatures().size());
      Assert.assertEquals(4, config.getVariants().size());
      Assert.assertEquals(DemoBranches.SAW_PL.getId(), config.getBranch().getId());
      Assert.assertEquals(4, config.getFeatureToValueMaps().size());
   }

}
