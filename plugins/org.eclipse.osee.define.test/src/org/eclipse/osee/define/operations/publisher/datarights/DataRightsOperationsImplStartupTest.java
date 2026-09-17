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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Test;

/**
 * Unit tests for the server-startup refresh trigger wired into
 * {@link DataRightsOperationsImpl#refreshRequiredIndicators()}.
 * <p>
 * At server startup {@code DefineOperationsImpl.start()} invokes
 * {@code DataRightsOperationsImpl.create(orcsApi).refreshRequiredIndicators()} exactly once. These
 * tests exercise that same public entry point on a directly constructed instance so the trigger
 * wiring is isolated from the production singleton and from any real database access. The tests
 * reside in the same package as {@link DataRightsOperationsImpl} so they can use its package-private
 * constructor to inject a mock {@link QueryFactory} and mock {@link RequiredIndicatorRefresher}.
 * <p>
 * Two behaviors are verified:
 * <ul>
 * <li>Requirement 7.1: startup invokes the refresher once against the {@link CoreBranches#COMMON}
 * query when the query factory is available.</li>
 * <li>Requirement 7.3: the refresher is invoked only through the startup and cache-clear triggers,
 * never by construction and never by an attribute-access / publish retrieval path.</li>
 * </ul>
 * The negative case for Requirement 7.3 drives a representative retrieval overload,
 * {@link DataRightsOperationsImpl#getDataRights(List, Map, String)}, which is the operations-only
 * path used by publishing. That overload takes the artifact map directly (so it performs no artifact
 * query) and reaches the query factory only through
 * {@code DataRightClassificationMap.create(commonBranchQuery, formatter)}. Because
 * {@code create} wraps its query in a catch-all that falls back to the unspecified map on any
 * exception, the retrieval completes without a database by stubbing the query builder to throw on
 * {@code asArtifact()}. This keeps the test a true unit test while still executing the real
 * retrieval code path, proving that attribute access / publishing never invokes the refresher.
 *
 * @author David W. Miller
 */

public class DataRightsOperationsImplStartupTest {

   /**
    * Requirement 7.1: server startup must invoke the refresher exactly once with a common-branch
    * query. {@code DefineOperationsImpl.start()} calls {@code refreshRequiredIndicators()} once on the
    * singleton; this test verifies that entry point builds the {@link CoreBranches#COMMON} query and
    * passes exactly that query object to the refresher a single time.
    */

   @Test
   public void testStartupRefreshInvokesRefresherOnceWithCommonBranchQuery() {

      QueryFactory queryFactory = mock(QueryFactory.class);
      RequiredIndicatorRefresher refresher = mock(RequiredIndicatorRefresher.class);
      QueryBuilder commonQuery = mock(QueryBuilder.class);

      when(queryFactory.fromBranch(CoreBranches.COMMON)).thenReturn(commonQuery);

      DataRightsOperationsImpl dataRightsOperationsImpl =
         new DataRightsOperationsImpl(queryFactory, refresher);

      dataRightsOperationsImpl.refreshRequiredIndicators();

      verify(queryFactory, times(1)).fromBranch(CoreBranches.COMMON);
      verify(refresher, times(1)).refresh(commonQuery);
      verifyNoMoreInteractions(refresher);
   }

   /**
    * Requirement 7.3: constructing the operations object must not invoke the refresher. Refresh is
    * only triggered by the explicit startup and cache-clear entry points, so a freshly built instance
    * that has done no work must leave the refresher untouched.
    */

   @Test
   public void testConstructionDoesNotInvokeRefresher() {

      QueryFactory queryFactory = mock(QueryFactory.class);
      RequiredIndicatorRefresher refresher = mock(RequiredIndicatorRefresher.class);

      new DataRightsOperationsImpl(queryFactory, refresher);

      verifyZeroInteractions(refresher);
   }

   /**
    * Requirement 7.3: an attribute-access / publish retrieval must not invoke the refresher. The
    * operations-only {@link DataRightsOperationsImpl#getDataRights(List, Map, String)} overload is the
    * representative publishing retrieval path. It is exercised end to end here; the query builder is
    * stubbed to throw on {@code asArtifact()} so
    * {@code DataRightClassificationMap.create} takes its documented catch-all fallback to the
    * unspecified map, keeping the test free of a database while still running the real retrieval code.
    * The refresher must remain untouched, proving refresh is not wired to per-access / per-publish
    * work.
    */

   @Test
   public void testRetrievalDoesNotInvokeRefresher() {

      QueryFactory queryFactory = mock(QueryFactory.class);
      RequiredIndicatorRefresher refresher = mock(RequiredIndicatorRefresher.class);
      QueryBuilder commonQuery = mock(QueryBuilder.class);

      /*
       * The NoOpPublishingOutputFormatter used by the operations-only getDataRights overload maps to
       * CoreArtifactTokens.DataRightsFooters, so getDataRightsClassificationMap reaches
       * DataRightClassificationMap.create with the common-branch query. Stub the query chain to throw
       * on asArtifact() so create() takes its catch-all fallback to the unspecified map without a
       * database.
       */

      when(queryFactory.fromBranch(CoreBranches.COMMON)).thenReturn(commonQuery);
      when(commonQuery.andId(CoreArtifactTokens.DataRightsFooters)).thenReturn(commonQuery);
      when(commonQuery.asArtifact()).thenThrow(new IllegalStateException("no database in unit test"));

      DataRightsOperationsImpl dataRightsOperationsImpl =
         new DataRightsOperationsImpl(queryFactory, refresher);

      /*
       * Use ArtifactReadable.SENTINEL as the map value so DataRightEntry skips its artifact-identity
       * check and no artifact attribute is read. With a valid "Unspecified" override classification,
       * the retrieval resolves entirely from the supplied map, so this stays a pure unit test that
       * still runs the real getDataRights code path.
       */

      ArtifactId artifactId = ArtifactId.valueOf(1L);

      var result = dataRightsOperationsImpl.getDataRights(List.of(artifactId),
         Map.of(artifactId, ArtifactReadable.SENTINEL), "Unspecified");

      assertEquals("getDataRights must return one data right anchor for every requested artifact.", 1,
         result.getDataRightAnchorSkinnys().size());

      verifyZeroInteractions(refresher);
   }

   /**
    * Requirement 7.3: repeated startup-style refresh calls must fire once per call and never
    * accumulate extra invocations. Two explicit {@code refreshRequiredIndicators()} calls invoke the
    * refresher exactly twice, each with the common-branch query, confirming the trigger count tracks
    * the trigger points rather than any incidental access.
    */

   @Test
   public void testEachStartupRefreshInvokesRefresherOnce() {

      QueryFactory queryFactory = mock(QueryFactory.class);
      RequiredIndicatorRefresher refresher = mock(RequiredIndicatorRefresher.class);
      QueryBuilder commonQuery = mock(QueryBuilder.class);

      when(queryFactory.fromBranch(any())).thenReturn(commonQuery);

      DataRightsOperationsImpl dataRightsOperationsImpl =
         new DataRightsOperationsImpl(queryFactory, refresher);

      dataRightsOperationsImpl.refreshRequiredIndicators();
      dataRightsOperationsImpl.refreshRequiredIndicators();

      verify(refresher, times(2)).refresh(commonQuery);
      verifyNoMoreInteractions(refresher);
   }

}
