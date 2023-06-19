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
package org.eclipse.osee.mim.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceGraphEndpoint;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.ClusterView;
import org.eclipse.osee.mim.types.ConnectionView;
import org.eclipse.osee.mim.types.GraphView;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.NodeView;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceGraphEndpointImpl implements InterfaceGraphEndpoint {

   private final BranchId branch;
   private final InterfaceNodeViewApi interfaceNodeApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;

   public InterfaceGraphEndpointImpl(BranchId branch, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi) {
      this.branch = branch;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi;
   }

   @Override
   public GraphView getAllNodesAndEdges(ArtifactId viewId) {
      try {
         GraphView graph = new GraphView();
         graph.setEdges(new LinkedList<ConnectionView>());
         graph.setNodes(new LinkedList<NodeView>());
         graph.setClusters(new LinkedList<ClusterView>());
         Collection<InterfaceNode> nodes = interfaceNodeApi.getAccessor().getAll(branch, viewId);
         Collection<InterfaceConnection> edges = interfaceConnectionApi.getAll(branch, viewId);
         for (InterfaceNode node : nodes) {
            graph.addNode(new NodeView(node));

            // If the node has a group id set, add it to the corresponding cluster
            if (!node.getInterfaceNodeGroupId().isEmpty()) {
               String clusterId = node.getInterfaceNodeGroupId();
               ClusterView cluster =
                  graph.getClusters().stream().filter(c -> c.getId().equals(clusterId)).findFirst().orElse(null);
               if (cluster == null) {
                  cluster = new ClusterView(clusterId);
                  graph.getClusters().add(cluster);
               }
               cluster.getChildNodeIds().add(node.getIdString());
            }
         }

         graph.getClusters().removeIf(c -> c.getChildNodeIds().size() < 2);

         for (InterfaceConnection connection : edges) {
            if (connection.getTransportType().isDirectConnection() && connection.getNodes().size() == 2) {
               graph.addEdges(new ConnectionView(connection, connection.getNodes().get(0).getIdString(),
                  connection.getNodes().get(1).getIdString()));
            } else if (!connection.getTransportType().isDirectConnection()) {
               ClusterView controllerCluster = new ClusterView(connection.getName() + "_controller");
               ClusterView nodesCluster = new ClusterView(connection.getName() + "_nodes");

               // Create a dummy node for the controller cluster
               InterfaceNode controllerNode = new InterfaceNode();
               controllerNode.setName(connection.getName() + " Controller");
               controllerNode.setInterfaceNodeBackgroundColor("#FFFFFF");
               NodeView controllerNodeView = new NodeView(controllerNode);
               controllerNodeView.setId(controllerNode.getName());
               graph.addNode(controllerNodeView);
               controllerCluster.getChildNodeIds().add(controllerNodeView.getId());

               // Create a dummy node for the nodes cluster
               InterfaceNode nodesClusterNode = new InterfaceNode();
               nodesClusterNode.setName(connection.getName() + " Nodes");
               nodesClusterNode.setInterfaceNodeBackgroundColor("#FFFFFF");
               NodeView nodesClusterNodeView = new NodeView(nodesClusterNode);
               nodesClusterNodeView.setId(nodesClusterNode.getName());
               graph.addNode(nodesClusterNodeView);
               nodesCluster.getChildNodeIds().add(nodesClusterNodeView.getId());

               for (InterfaceNode node : connection.getNodes()) {
                  nodesCluster.getChildNodeIds().add(node.getIdString());
               }

               graph.addEdges(new ConnectionView(connection, controllerNodeView.getId(), nodesClusterNodeView.getId()));
               graph.getClusters().add(controllerCluster);
               graph.getClusters().add(nodesCluster);
            }
         }
         return graph;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

}
