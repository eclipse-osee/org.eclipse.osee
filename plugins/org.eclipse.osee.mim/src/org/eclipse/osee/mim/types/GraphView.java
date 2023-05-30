/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Luciano T. Vaglienti
 */
public class GraphView {
   Collection<ConnectionView> edges;
   Collection<NodeView> nodes;
   Collection<ClusterView> clusters;

   public GraphView() {
   }

   /**
    * @return the edges
    */
   public Collection<ConnectionView> getEdges() {
      return edges;
   }

   /**
    * @param edges the edges to set
    */
   public void setEdges(Collection<ConnectionView> edges) {
      this.edges = edges;
   }

   /**
    * @return the nodes
    */
   public Collection<NodeView> getNodes() {
      return nodes;
   }

   /**
    * @param nodes the nodes to set
    */
   public void setNodes(Collection<NodeView> nodes) {
      this.nodes = nodes;
   }

   @JsonIgnore
   public void addNode(NodeView node) {
      this.nodes.add(node);
   }

   @JsonIgnore
   public void removeNode(NodeView node) {
      if (this.nodes.contains(node)) {
         this.nodes.remove(node);
      }
   }

   @JsonIgnore
   public void removeNode(Long index) {
      nodes = this.nodes.stream().filter(x -> x.getId() != index).collect(Collectors.toList());
   }

   @JsonIgnore
   public void addEdges(ConnectionView connection) {
      this.edges.add(connection);
   }

   @JsonIgnore
   public void removeEdges(ConnectionView connection) {
      if (this.edges.contains(connection)) {
         this.edges.remove(connection);
      }
   }

   @JsonIgnore
   public void removeEdges(Long index) {
      edges = this.edges.stream().filter(x -> x.getId() != index).collect(Collectors.toList());
   }

   public Collection<ClusterView> getClusters() {
      return clusters;
   }

   public void setClusters(Collection<ClusterView> clusters) {
      this.clusters = clusters;
   }

}
