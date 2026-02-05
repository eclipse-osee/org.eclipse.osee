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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.OrcsQueryService;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactSearchCriteriaTest extends AbstractRestTest {
   private AtsApiIde atsApi;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
   }

   @Test
   public void testGetRelatedTo__ClientLegacy() {

      // Test legacy client query builder and search
      List<ArtifactId> ids = ArtifactQuery.createQueryBuilder( //
         atsApi.getAtsBranch()) //
         .andIsOfType(AtsArtifactTypes.PeerToPeerReview) //
         .and(AtsAttributeTypes.CurrentStateType, StateType.Working.name(), QueryOption.EXACT_MATCH_OPTIONS) //
         .andRelatedTo(AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow, //
            Arrays.asList(DemoArtifactToken.SAW_Commited_Code_TeamWf)).getIds();
      Assert.assertEquals(2, ids.size());

   }

   @Test
   public void testGetRelatedTo__Server() {
      // Test server query builder and search, should come back with same results
      XResultData rd = atsApi.getServerEndpoints().getTestEp().testSearchCriteria();
      Assert.assertTrue(rd.toString(), rd.isSuccess());
   }

   @Test
   public void testGetRelatedTo__ClientViaRest() {
      // Test client calling REST query builder search
      QueryBuilder restQuery =
         OrcsQueryService.fromBranch(atsApi.getAtsBranch()).and(AtsAttributeTypes.CurrentStateType,
            StateType.Working.name()) //
            .andRelatedTo(AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow, //
               Arrays.asList(DemoArtifactToken.SAW_Commited_Code_TeamWf));
      List<ArtifactReadable> ideRestSearch = atsApi.getServerEndpoints().getQueryEp().query(restQuery);
      Assert.assertEquals(2, ideRestSearch.size());
   }

}
