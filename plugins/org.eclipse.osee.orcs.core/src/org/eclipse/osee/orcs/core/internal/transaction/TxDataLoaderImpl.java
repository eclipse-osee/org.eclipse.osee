/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilder;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManager.TxDataLoader;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TxDataLoaderImpl implements TxDataLoader {

   private final DataLoaderFactory dataLoaderFactory;
   private final GraphFactory graphFactory;
   private final GraphBuilderFactory graphBuilderFactory;
   private final GraphProvider graphProvider;
   private final TransactionProvider txProvider;

   public static interface TransactionProvider {
      TransactionId getHeadTransaction(OrcsSession session, BranchId branch);
   }

   public TxDataLoaderImpl(DataLoaderFactory dataLoaderFactory, GraphFactory graphFactory, GraphBuilderFactory graphBuilderFactory, GraphProvider graphProvider, TransactionProvider txProvider) {
      super();
      this.dataLoaderFactory = dataLoaderFactory;
      this.graphProvider = graphProvider;
      this.graphFactory = graphFactory;
      this.graphBuilderFactory = graphBuilderFactory;
      this.txProvider = txProvider;
   }

   private DataLoader createLoader(OrcsSession session, BranchId branch, Collection<ArtifactId> artifactIds)  {
      Set<Integer> ids = new LinkedHashSet<>();
      for (ArtifactId artifactId : artifactIds) {
         ids.add(artifactId.getUuid().intValue());
      }
      DataLoader loader = dataLoaderFactory.newDataLoaderFromIds(session, branch, ids);
      loader.withLoadLevel(LoadLevel.ALL);
      loader.includeDeletedAttributes();
      loader.includeDeletedRelations();
      return loader;
   }

   @Override
   public ResultSet<Artifact> loadArtifacts(OrcsSession session, BranchId branch, Collection<ArtifactId> artifactIds)  {
      DataLoader loader = createLoader(session, branch, artifactIds);
      GraphBuilder handler = graphBuilderFactory.createGraphBuilder(graphProvider);
      loader.load(null, handler);
      return ResultSets.newResultSet(handler.getArtifacts());
   }

   @Override
   public ResultSet<Artifact> loadArtifacts(OrcsSession session, GraphData graph, Collection<ArtifactId> artifactIds)  {
      DataLoader loader = createLoader(session, graph.getBranch(), artifactIds);
      loader.fromTransaction(graph.getTransaction());
      loader.fromBranchView(graph.getBranch().getViewId());
      GraphBuilder handler = graphBuilderFactory.createBuilderForGraph(graph);
      loader.load(null, handler);
      return ResultSets.newResultSet(handler.getArtifacts());
   }

   @Override
   public GraphData createGraph(OrcsSession session, BranchId branch)  {
      TransactionId headTransaction = txProvider.getHeadTransaction(session, branch);
      return graphFactory.createGraph(session, branch, headTransaction);
   }

}