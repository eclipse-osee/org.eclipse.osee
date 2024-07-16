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

package org.eclipse.osee.orcs.core.internal.graph.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManager;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilder;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;

/**
 * @author Roberto E. Escobar
 */
public class GraphBuilderImpl extends LoadDataHandlerAdapter implements GraphBuilder {

   private final Log logger;
   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;
   private final RelationFactory relationFactory;
   private final GraphProvider graphProvider;

   private final Set<Artifact> updated = new LinkedHashSet<>();
   private GraphData graph;

   public GraphBuilderImpl(Log logger, ArtifactFactory artifactFactory, AttributeFactory attributeFactory, RelationFactory relationFactory, GraphProvider graphProvider) {
      super();
      this.logger = logger;
      this.graphProvider = graphProvider;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;
      this.relationFactory = relationFactory;
   }

   @Override
   public void onLoadStart() {
      graph = null;
   }

   private GraphData getGraph() {
      Conditions.checkNotNull(graph, "graph");
      return graph;
   }

   @Override
   public void onLoadDescription(LoadDescription data) {
      graph = graphProvider.getGraph(data.getSession(), data.getBranch(), data.getTransaction());
      Conditions.checkNotNull(graph, "graph");
   };

   @Override
   public void onData(ArtifactData data) {
      GraphData graph = getGraph();
      Artifact artifact = graph.getNode(data);
      if (artifact == null) {
         artifact = artifactFactory.createArtifact(graph.getSession(), data);
         graph.addNode(artifact, artifact.getOrcsData().isExistingVersionUsed());

         RelationNodeAdjacencies adjacencies = relationFactory.createRelationContainer();
         graph.addAdjacencies(data, adjacencies);
      }
      updated.add(artifact);
   }

   @Override
   public <T> void onData(AttributeData<T> data) {
      GraphData graph = getGraph();
      AttributeManager container = graph.getNode(data.getArtifactId());
      if (container == null) {
         logger.warn("Orphaned attribute detected - data[%s]", data);
      } else {
         attributeFactory.createAttribute(container, data);
      }
   }

   @Override
   public void onData(RelationData data) {
      GraphData graph = getGraph();

      RelationNodeAdjacencies aAdjacencies = getAdjacencies(graph, data.getArtifactIdA());
      RelationNodeAdjacencies bAdjacencies = getAdjacencies(graph, data.getArtifactIdB());
      Relation relation = findRelation(aAdjacencies, data);
      if (relation == null) {
         relation = findRelation(bAdjacencies, data);
      }
      if (relation == null) {
         relation = relationFactory.createRelation(data);
      }
      RelationTypeToken relationType = data.getType();
      aAdjacencies.add(relationType, relation);
      bAdjacencies.add(relationType, relation);
   }

   private Relation findRelation(RelationNodeAdjacencies adjacencies, RelationData data) {
      if (data.getType().isNewRelationTable()) {
         return adjacencies.getRelation(data.getArtifactIdA(), data.getType(), data.getArtifactIdB(),
            data.getRelOrder());
      }
      return adjacencies.getRelation(data.getArtifactIdA(), data.getType(), data.getArtifactIdB());
   }

   private RelationNodeAdjacencies getAdjacencies(GraphData graph, ArtifactId id) {
      RelationNodeAdjacencies adjacencies = graph.getAdjacencies(id);
      if (adjacencies == null) {
         adjacencies = relationFactory.createRelationContainer();
         graph.addAdjacencies(id, adjacencies);
      }
      return adjacencies;
   }

   @Override
   public void onLoadEnd() {
      //
   }

   @Override
   public Iterable<Artifact> getArtifacts() {
      return updated;
   }

   @Override
   public Iterable<GraphData> getGraphs() {
      return graph != null ? Collections.<GraphData> singleton(graph) : Collections.<GraphData> emptyList();
   }
}