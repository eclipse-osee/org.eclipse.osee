/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.search;

import java.util.List;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilder;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class CallableQueryFactory {

   private final QueryEngine queryEngine;
   private final GraphBuilderFactory builderFactory;
   private final GraphProvider provider;
   private final ExternalArtifactManager proxyManager;

   public CallableQueryFactory(Log logger, QueryEngine queryEngine, GraphBuilderFactory builderFactory, GraphProvider provider, ExternalArtifactManager proxyManager) {
      this.queryEngine = queryEngine;
      this.builderFactory = builderFactory;
      this.provider = provider;
      this.proxyManager = proxyManager;
   }

   public ResultSet<ArtifactReadable> createSearch(QueryData queryData) {
      GraphBuilder handler = builderFactory.createGraphBuilder(provider);
      OptionsUtil.setLoadLevel(queryData.getOptions(), LoadLevel.ALL);
      queryEngine.runArtifactQuery(queryData, handler);
      Iterable<Artifact> results = handler.getArtifacts();
      return proxyManager.asExternalArtifacts(null, results);
   }

   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> createSearchWithMatches(OrcsSession session, QueryData queryData) {
      GraphBuilder handler = builderFactory.createGraphBuilder(provider);
      ArtifactMatchDataHandler matchHandler = new ArtifactMatchDataHandler(handler, proxyManager);
      OptionsUtil.setLoadLevel(queryData.getOptions(), LoadLevel.ALL);
      queryEngine.runArtifactQuery(queryData, matchHandler);
      List<Match<ArtifactReadable, AttributeReadable<?>>> results = matchHandler.getResults();
      return ResultSets.newResultSet(results);
   }
}