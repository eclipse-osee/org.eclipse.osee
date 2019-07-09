/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public interface QueryEngine {

   int getArtifactCount(QueryData queryData);

   void runArtifactQuery(QueryData queryData, LoadDataHandler handler) throws Exception;

   int getBranchCount(QueryData queryData);

   void runBranchQuery(QueryData queryData, List<? super Branch> branches);

   TupleQuery createTupleQuery();

   ApplicabilityDsQuery createApplicabilityDsQuery();

   int getTxCount(QueryData queryData);

   void runTxQuery(QueryData queryData, List<? super TransactionReadable> txs);

   CancellableCallable<Integer> createQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler);

   List<ArtifactToken> asArtifactTokens(QueryData queryData);

   List<Map<String, Object>> asArtifactMaps(QueryData queryData);

   Map<ArtifactId, ArtifactToken> asArtifactTokenMap(QueryData queryData);

   List<ArtifactId> asArtifactIds(QueryData queryData);

   Map<ArtifactId, ArtifactReadable> asArtifactMap(QueryData queryData, QueryFactory queryFactory);

   List<ArtifactReadable> asArtifacts(QueryData queryData, QueryFactory queryFactory);
}