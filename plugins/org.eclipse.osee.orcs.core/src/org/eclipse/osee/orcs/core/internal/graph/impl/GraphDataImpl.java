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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.internal.graph.GraphAdjacencies;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphNode;
import org.eclipse.osee.orcs.data.HasLocalId;

/**
 * @author Roberto E. Escobar
 */
public class GraphDataImpl implements GraphData {

   private final Map<Integer, GraphNode> nodesById = new ConcurrentHashMap<Integer, GraphNode>();
   private final Map<Integer, GraphAdjacencies> adjacenciesById = new ConcurrentHashMap<Integer, GraphAdjacencies>();

   private final IOseeBranch branch;
   private final int txId;

   public GraphDataImpl(IOseeBranch branch, int txId) {
      super();
      this.branch = branch;
      this.txId = txId;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   @Override
   public int getTransaction() {
      return txId;
   }

   @Override
   public <T extends GraphNode> T getNode(HasLocalId data) {
      return getNode(data.getLocalId());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends GraphNode> T getNode(int id) {
      return (T) nodesById.get(id);
   }

   @Override
   public void addNode(GraphNode node) throws OseeCoreException {
      boolean sameBranches = getBranch().equals(node.getBranch());
      Conditions.checkExpressionFailOnTrue(!sameBranches, "Invalid node added to graph. Graph[%s] Node[%s]", this,
         node.getExceptionString());

      GraphData oldGraph = node.getGraph();
      if (!this.equals(oldGraph)) {
         if (oldGraph != null) {
            oldGraph.removeNode(node);
         }
         nodesById.put(node.getLocalId(), node);
         node.setGraph(this);
      }
   }

   @Override
   public <T extends GraphNode> T removeNode(HasLocalId node) {
      return removeNode(node.getLocalId());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends GraphNode> T removeNode(int id) {
      T removed = (T) nodesById.remove(id);
      removeAdjacencies(id);
      return removed;
   }

   @Override
   public <T extends GraphAdjacencies> T getAdjacencies(HasLocalId node) {
      return getAdjacencies(node.getLocalId());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends GraphAdjacencies> T getAdjacencies(int id) {
      return (T) adjacenciesById.get(id);
   }

   @Override
   public void addAdjacencies(HasLocalId node, GraphAdjacencies adjacencies) {
      addAdjacencies(node.getLocalId(), adjacencies);
   }

   @Override
   public void addAdjacencies(int id, GraphAdjacencies adjacencies) {
      adjacenciesById.put(id, adjacencies);
   }

   @Override
   public <T extends GraphAdjacencies> T removeAdjacencies(HasLocalId node) {
      return removeAdjacencies(node.getLocalId());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends GraphAdjacencies> T removeAdjacencies(int id) {
      return (T) adjacenciesById.remove(id);
   }

   @Override
   public String toString() {
      return String.format("Graph - branch[%s] txId[%s] nodes[%s] adjacencies[%s]", getBranch(), getTransaction(),
         nodesById.size(), adjacenciesById.size());
   }

}
