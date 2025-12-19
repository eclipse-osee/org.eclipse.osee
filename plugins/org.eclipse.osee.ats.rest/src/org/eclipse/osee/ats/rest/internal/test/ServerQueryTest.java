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
package org.eclipse.osee.ats.rest.internal.test;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class ServerQueryTest extends AbstractServerTest {

   private final XResultData rd;

   public ServerQueryTest(AtsApi atsApi, OrcsApi orcsApi, XResultData rd) {
      super(atsApi, orcsApi);
      this.rd = rd;
   }

   public XResultData run() {
      testSearchCriteria();
      return rd;
   }

   public void testSearchCriteria() {

      QueryBuilder query = orcsApi.getQueryFactory() //
         .fromBranch(atsApi.getAtsBranch()) //
         .and(AtsAttributeTypes.CurrentStateType, StateType.Working.name(), QueryOption.EXACT_MATCH_OPTIONS) //
         .andRelatedTo(AtsRelationTypes.TeamWorkflowToReview_TeamWorkflow, //
            Arrays.asList(DemoArtifactToken.SAW_Commited_Code_TeamWf));

      // Search results using legacy getResults()
      List<ArtifactReadable> asResultsList = query.getResults().getList();
      rd.logf("Testing getResults().getList()\n");
      rd.assertEquals(2, asResultsList.size());

      // New search results using legacy getResults()
      List<ArtifactReadable> asArtifacts = query.asArtifacts();
      rd.logf("\nTesting asArtifacts\n");
      rd.assertEquals(2, asArtifacts.size());

   }

}
