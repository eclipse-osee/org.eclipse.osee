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
package org.eclipse.osee.orcs.core.internal.util;

import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkExpressionFailOnTrue;
import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkNotNull;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsConditions {

   private OrcsConditions() {
      // Utility class
   }

   public static void checkOnGraph(GraphData graph, RelationNode... nodes)  {
      checkNotNull(graph, "graph");
      for (RelationNode node : nodes) {
         checkNotNull(node, "node");
         GraphData graph2 = node.getGraph();
         checkExpressionFailOnTrue(!graph.equals(graph2), "Error - Node[%s] is on graph[%s] but should be on graph[%s]",
            node, graph2, graph);
      }
   }

   public static void checkBranch(GraphData graph, RelationNode... nodes)  {
      checkNotNull(graph, "graph");
      for (RelationNode node : nodes) {
         checkNotNull(node, "node");
         GraphData graph2 = node.getGraph();
         checkExpressionFailOnTrue(!graph.getBranch().equals(graph2.getBranch()),
            "Error - Node[%s] is on branch[%d] but should be on branch[%d]", node, graph2.getBranch(),
            graph.getBranch());
      }
   }

   public static void checkBranch(RelationNode node1, RelationNode node2)  {
      boolean areEqual = node1.getBranch().equals(node2.getBranch());
      checkExpressionFailOnTrue(!areEqual, "Cross branch linking is not yet supported.");
   }

   public static void checkRelateSelf(RelationNode node1, RelationNode node2)  {
      checkExpressionFailOnTrue(node1.equals(node2), "Not valid to relate [%s] to itself", node1);
   }
}
