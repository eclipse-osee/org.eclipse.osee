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
            if (!node.getNodeGroupId().isEmpty()) {
               String clusterId = node.getNodeGroupId();
               ClusterView cluster =
                  graph.getClusters().stream().filter(c -> c.getId().equals(clusterId)).findFirst().orElse(null);
               if (cluster == null) {
                  cluster = new ClusterView(clusterId, "");
                  graph.getClusters().add(cluster);
               }
               cluster.getChildNodeIds().add(node.getIdString());
            }
         }

         graph.getClusters().removeIf(c -> c.getChildNodeIds().size() < 2);

         for (InterfaceConnection connection : edges) {
            if (connection.getPrimaryNode() > -1 && connection.getSecondaryNode() > -1) {
               graph.addEdges(new ConnectionView(connection));
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
