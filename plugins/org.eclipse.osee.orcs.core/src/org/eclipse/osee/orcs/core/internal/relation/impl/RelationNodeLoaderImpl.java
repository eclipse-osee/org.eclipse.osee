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
package org.eclipse.osee.orcs.core.internal.relation.impl;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilder;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.RelationNodeLoader;

/**
 * @author Roberto E. Escobar
 */
public class RelationNodeLoaderImpl implements RelationNodeLoader {

   private final DataLoaderFactory dataLoaderFactory;
   private final GraphBuilderFactory graphBuilderFactory;

   public RelationNodeLoaderImpl(DataLoaderFactory dataLoaderFactory, GraphBuilderFactory graphBuilderFactory) {
      super();
      this.dataLoaderFactory = dataLoaderFactory;
      this.graphBuilderFactory = graphBuilderFactory;
   }

   @Override
   public <T extends Artifact> Iterable<T> loadNodes(OrcsSession session, final GraphData graph, Collection<Integer> ids, LoadLevel level) {
      GraphBuilder builder = graphBuilderFactory.createBuilderForGraph(graph);

      DataLoader loader = dataLoaderFactory.newDataLoaderFromIds(session, graph.getBranch(), ids);
      loader.withLoadLevel(level);
      loader.fromTransaction(graph.getTransaction());
      loader.fromBranchView(graph.getBranch().getViewId());
      loader.includeDeletedArtifacts();
      loader.load(null, builder);

      return getResults(builder);
   }

   @SuppressWarnings("unchecked")
   private static <T> Iterable<T> getResults(GraphBuilder builder) {
      return (Iterable<T>) builder.getArtifacts();
   }

}