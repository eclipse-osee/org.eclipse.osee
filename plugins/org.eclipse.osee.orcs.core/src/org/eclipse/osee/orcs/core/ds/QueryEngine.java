/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.ArtifactTable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public interface QueryEngine {

   int getArtifactCount(QueryData queryData);

   void runArtifactQuery(QueryData queryData, LoadDataHandler handler);

   int getBranchCount(QueryData queryData);

   void runBranchQuery(QueryData queryData, List<? super Branch> branches);

   TupleQuery createTupleQuery();

   ApplicabilityDsQuery createApplicabilityDsQuery();

   int getTxCount(QueryData queryData);

   void runTxQuery(UserService userService, QueryData queryData, List<? super TransactionReadable> txs);

   List<ArtifactToken> asArtifactTokens(QueryData queryData);

   List<Map<String, Object>> asArtifactMaps(QueryData queryData);

   Map<ArtifactId, ArtifactToken> asArtifactTokenMap(QueryData queryData);

   List<ArtifactId> asArtifactIds(QueryData queryData);

   Map<ArtifactId, ArtifactReadable> asArtifactMap(QueryData queryData, QueryFactory queryFactory);

   List<ArtifactReadable> asArtifact(QueryData queryData, QueryFactory queryFactory);

   List<ArtifactReadable> asArtifacts(QueryData queryData, QueryFactory queryFactory);

   boolean isArchived(BranchId branchId);

   void getBranchCategoryGammaIds(Consumer<GammaId> consumer, BranchId branchId, BranchCategoryToken category);

   void getBranchCategories(Consumer<BranchCategoryToken> consumer, BranchId branchId);

   ArtifactTable asArtifactsTable(QueryData queryData, QueryFactory queryFactory);
}