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

package org.eclipse.osee.define.operations.publisher.datarights;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Test;

/**
 * Unit tests for the cache-clear refresh trigger wired into
 * {@link DataRightsOperationsImpl#deleteCache()}.
 * <p>
 * The tests reside in the same package as {@link DataRightsOperationsImpl} so they can use its
 * package-private constructor to inject a mock {@link QueryFactory} and mock
 * {@link RequiredIndicatorRefresher}. This isolates the trigger wiring from the production singleton
 * (created through {@code create(OrcsApi)}) and from any real database access. The refresher is
 * verified to run exactly once per cache clear against the {@link CoreBranches#COMMON} query, proving
 * the additive refresh fires alongside cache invalidation without being invoked more than once per
 * clear.
 *
 * @author David W. Miller
 */

public class DataRightsOperationsImplCacheClearTest {

   /**
    * Requirements 7.2, 7.3: clearing the data rights cache must invoke the refresher exactly once with
    * a common-branch query. The query factory is verified to build the {@link CoreBranches#COMMON}
    * query and the refresher is verified to receive exactly that query object, proving the refresh
    * targets the common branch and fires once per cache clear.
    */

   @Test
   public void testDeleteCacheInvokesRefresherOnceWithCommonBranchQuery() {

      QueryFactory queryFactory = mock(QueryFactory.class);
      RequiredIndicatorRefresher refresher = mock(RequiredIndicatorRefresher.class);
      QueryBuilder commonQuery = mock(QueryBuilder.class);

      when(queryFactory.fromBranch(CoreBranches.COMMON)).thenReturn(commonQuery);

      DataRightsOperationsImpl dataRightsOperationsImpl =
         new DataRightsOperationsImpl(queryFactory, refresher);

      dataRightsOperationsImpl.deleteCache();

      verify(queryFactory, times(1)).fromBranch(CoreBranches.COMMON);
      verify(refresher, times(1)).refresh(commonQuery);
      verifyNoMoreInteractions(refresher);
   }

   /**
    * Requirement 7.3: the refresher must fire once per cache clear and not accumulate extra
    * invocations. Two calls to {@code deleteCache()} must invoke the refresher exactly twice (once per
    * clear), each time with the common-branch query.
    */

   @Test
   public void testEachCacheClearInvokesRefresherOnce() {

      QueryFactory queryFactory = mock(QueryFactory.class);
      RequiredIndicatorRefresher refresher = mock(RequiredIndicatorRefresher.class);
      QueryBuilder commonQuery = mock(QueryBuilder.class);

      when(queryFactory.fromBranch(CoreBranches.COMMON)).thenReturn(commonQuery);

      DataRightsOperationsImpl dataRightsOperationsImpl =
         new DataRightsOperationsImpl(queryFactory, refresher);

      dataRightsOperationsImpl.deleteCache();
      dataRightsOperationsImpl.deleteCache();

      verify(queryFactory, times(2)).fromBranch(CoreBranches.COMMON);
      verify(refresher, times(2)).refresh(commonQuery);
      verifyNoMoreInteractions(refresher);
   }

}
