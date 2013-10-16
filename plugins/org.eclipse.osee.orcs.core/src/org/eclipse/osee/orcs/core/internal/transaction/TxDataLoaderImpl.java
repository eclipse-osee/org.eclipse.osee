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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
import org.eclipse.osee.orcs.core.internal.util.ResultSetIterable;
import org.eclipse.osee.orcs.data.ArtifactId;

/**
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public class TxDataLoaderImpl implements TxDataLoader {

   private final DataLoaderFactory dataLoaderFactory;
   private final GraphFactory graphFactory;
   private final GraphBuilderFactory graphBuilderFactory;
   private final GraphProvider graphProvider;

   public TxDataLoaderImpl(DataLoaderFactory dataLoaderFactory, GraphFactory graphFactory, GraphBuilderFactory graphBuilderFactory, GraphProvider graphProvider) {
      super();
      this.dataLoaderFactory = dataLoaderFactory;
      this.graphProvider = graphProvider;
      this.graphFactory = graphFactory;
      this.graphBuilderFactory = graphBuilderFactory;
   }

   private DataLoader createLoader(OrcsSession session, IOseeBranch branch, Collection<ArtifactId> artifactIds) throws OseeCoreException {
      Set<String> ids = new LinkedHashSet<String>();
      for (ArtifactId artifactId : artifactIds) {
         ids.add(artifactId.getGuid());
      }
      DataLoader loader = dataLoaderFactory.fromBranchAndIds(session, branch, ids);
      loader.setLoadLevel(LoadLevel.FULL);
      return loader;
   }

   @Override
   public ResultSet<Artifact> loadArtifacts(OrcsSession session, IOseeBranch branch, Collection<ArtifactId> artifactIds) throws OseeCoreException {
      DataLoader loader = createLoader(session, branch, artifactIds);
      GraphBuilder handler = graphBuilderFactory.createGraphBuilder(graphProvider);
      loader.load(null, handler);
      return new ResultSetIterable<Artifact>(handler.getArtifacts());
   }

   @Override
   public ResultSet<Artifact> loadArtifacts(OrcsSession session, GraphData graph, Collection<ArtifactId> artifactIds) throws OseeCoreException {
      DataLoader loader = createLoader(session, graph.getBranch(), artifactIds);
      loader.fromTransaction(graph.getTransaction());
      GraphBuilder handler = graphBuilderFactory.createBuilderForGraph(graph);
      loader.load(null, handler);
      return new ResultSetIterable<Artifact>(handler.getArtifacts());
   }

   @Override
   public GraphData createGraph(IOseeBranch branch) throws OseeCoreException {
      return graphFactory.createGraphSetToHeadTx(branch);
   }

}