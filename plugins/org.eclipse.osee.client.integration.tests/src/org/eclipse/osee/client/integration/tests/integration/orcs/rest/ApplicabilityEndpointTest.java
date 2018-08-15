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
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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

}
