/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for AtsRelationEndpointImpl and AtsRelationService
 *
 * @author Donald G. Dunne
 */
public class AtsRelationEndpointImplTest {

   private static AtsApiIde atsApi;
   private static IAtsTeamWorkflow sawCodeCommittedWf;

   public AtsRelationEndpointImplTest() {
   }

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
      atsApi = AtsApiService.get();
      sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();
   }

   @Test
   public void testRelSvcGetRelatedTokens() {
      Collection<Artifact> relatedfromSvc = Collections.castAll(
         atsApi.relSvc().getRelated(sawCodeCommittedWf.getArtifactId(), AtsRelationTypes.TeamWfToTask_Task));
      Assert.assertEquals(8, relatedfromSvc.size());

      Collection<ArtifactToken> relatedTokens =
         atsApi.relSvc().getRelatedTokens(sawCodeCommittedWf.getArtifactId(), AtsRelationTypes.TeamWfToTask_Task);
      Assert.assertEquals(8, relatedTokens.size());
   }

   @Test
   public void testRelEndpointGetRelatedTokens() {
      Collection<Artifact> relatedfromSvc =
         Collections.castAll(atsApi.getServerEndpoints().getRelationEp().getRelatedTokens(
            sawCodeCommittedWf.getArtifactId(), AtsRelationTypes.TeamWfToTask_Task));
      Assert.assertEquals(8, relatedfromSvc.size());

   }
}
