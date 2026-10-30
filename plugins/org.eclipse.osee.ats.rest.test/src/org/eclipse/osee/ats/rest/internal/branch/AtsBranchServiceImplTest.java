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

package org.eclipse.osee.ats.rest.internal.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.*;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Test;

public class AtsBranchServiceImplTest {

   @Test
   public void testWorkingBranchInWorkUsesPersistedState() {
      Branch snapshot = branch(BranchState.MODIFIED);
      Branch persisted = branch(BranchState.COMMITTED);
      OrcsApi orcsApi = mock(OrcsApi.class);
      QueryFactory queryFactory = mock(QueryFactory.class);
      BranchQuery query = mock(BranchQuery.class);
      when(orcsApi.getQueryFactory()).thenReturn(queryFactory);
      when(queryFactory.branchQuery()).thenReturn(query);
      when(query.andId(snapshot)).thenReturn(query);
      when(query.getResults()).thenReturn(ResultSets.singleton(persisted));
      AtsBranchServiceImpl service = new AtsBranchServiceImpl(null, orcsApi, null) {
         @Override
         public BranchToken getWorkingBranch(IAtsTeamWorkflow teamWf) {
            return snapshot;
         }
      };

      assertFalse(service.isWorkingBranchInWork(mock(IAtsTeamWorkflow.class)));
      assertEquals(BranchState.MODIFIED, snapshot.getBranchState());
      persisted.setBranchState(BranchState.MODIFIED);
      assertTrue(service.isWorkingBranchInWork(mock(IAtsTeamWorkflow.class)));
   }

   private Branch branch(BranchState state) {
      return new Branch(100L, "Working branch", ArtifactId.SENTINEL, TransactionId.SENTINEL,
         TransactionId.SENTINEL, BranchId.valueOf(8L), false, state, BranchType.WORKING, false,
         ArtifactId.SENTINEL);
   }
}
