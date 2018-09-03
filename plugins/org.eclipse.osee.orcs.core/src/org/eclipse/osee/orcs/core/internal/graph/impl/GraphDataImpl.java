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
package org.eclipse.osee.orcs.core.internal.graph.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.graph.GraphAdjacencies;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphNode;

/**
 * @author Roberto E. Escobar
 */
public class GraphDataImpl implements GraphData {

   private final Map<ArtifactId, GraphNode> nodesById = new ConcurrentHashMap<>();
   private final Map<ArtifactId, GraphAdjacencies> adjacenciesById = new ConcurrentHashMap<>();

   private final BranchId branch;
   private final TransactionId txId;
   private final OrcsSession orcsSession;

   public GraphDataImpl(OrcsSession session, BranchId branch, TransactionId txId) {
      super();
      this.orcsSession = session;
      this.branch = branch;
      this.txId = txId;
   }

   @Override
   public TransactionId getTransaction() {
      return txId;
   }

   @Override
   public <T extends GraphNode> T getNode(ArtifactId artifactId) {
      return (T) nodesById.get(artifactId);
   }

   @Override
   public void addNode(GraphNode node, boolean useBackingData) {
      boolean sameBranches = getBranch().equals(node.getBranch());
      if (!sameBranches) {
         throw new OseeArgumentException("Invalid node added to graph. Graph[%s] Node[%s]", this,
            node.getExceptionString());
      }

      GraphData oldGraph = node.getGraph();
      if (!this.equals(oldGraph) || useBackingData) {
         if (oldGraph != null) {
            oldGraph.removeNode(node);
         }
         nodesById.put(node, node);
         node.setGraph(this);
      }
   }

   @Override
   public <T extends GraphNode> T removeNode(ArtifactId node) {
      T removed = (T) nodesById.remove(node);
      removeAdjacencies(node);
      return removed;
   }

   @Override
   public <T extends GraphAdjacencies> T getAdjacencies(ArtifactId node) {
      return (T) adjacenciesById.get(node);
   }

   @Override
   public void addAdjacencies(ArtifactId node, GraphAdjacencies adjacencies) {
      adjacenciesById.put(node, adjacencies);
   }

   @Override
   public <T extends GraphAdjacencies> T removeAdjacencies(ArtifactId node) {
      return (T) adjacenciesById.remove(node);
   }

   @Override
   public String toString() {
      return String.format("Graph - branch[%s] txId[%s] nodes[%s] adjacencies[%s]", getBranch(), getTransaction(),
         nodesById.size(), adjacenciesById.size());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = getBranch().hashCode();
      result = prime * result + getTransaction().hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof GraphData)) {
         return false;
      }
      GraphData other = (GraphData) obj;
      if (!getBranch().equals(other.getBranch())) {
         return false;
      }
      if (getTransaction() != other.getTransaction()) {
         return false;
      }
      return true;
   }

   @Override
   public OrcsSession getSession() {
      return orcsSession;
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }
}